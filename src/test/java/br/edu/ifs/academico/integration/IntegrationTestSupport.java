package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.repository.CartaoUsuarioRepository;
import br.edu.ifs.academico.repository.ComprovanteRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import br.edu.ifs.academico.repository.NotificacaoRepository;
import br.edu.ifs.academico.repository.NotificacaoUsuarioRepository;
import br.edu.ifs.academico.repository.PasswordResetTokenRepository;
import br.edu.ifs.academico.repository.ProgramaFidelidadeRepository;
import br.edu.ifs.academico.repository.PromocaoRepository;
import br.edu.ifs.academico.repository.SaldoUsuarioProgramaRepository;
import br.edu.ifs.academico.repository.StatusMovimentacaoRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
public abstract class IntegrationTestSupport {

    protected static final String DEFAULT_PASSWORD = "Password123!";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    protected ProgramaFidelidadeRepository programaFidelidadeRepository;

    @Autowired
    protected PromocaoRepository promocaoRepository;

    @Autowired
    protected CartaoUsuarioRepository cartaoUsuarioRepository;

    @Autowired
    protected SaldoUsuarioProgramaRepository saldoUsuarioProgramaRepository;

    @Autowired
    protected MovimentacaoPontosRepository movimentacaoPontosRepository;

    @Autowired
    protected StatusMovimentacaoRepository statusMovimentacaoRepository;

    @Autowired
    protected ComprovanteRepository comprovanteRepository;

    @Autowired
    protected NotificacaoRepository notificacaoRepository;

    @Autowired
    protected NotificacaoUsuarioRepository notificacaoUsuarioRepository;

    @BeforeEach
    void cleanDatabase() {
        notificacaoUsuarioRepository.deleteAll();
        notificacaoRepository.deleteAll();
        comprovanteRepository.deleteAll();
        statusMovimentacaoRepository.deleteAll();
        movimentacaoPontosRepository.deleteAll();
        saldoUsuarioProgramaRepository.deleteAll();
        cartaoUsuarioRepository.deleteAll();
        promocaoRepository.deleteAll();
        programaFidelidadeRepository.deleteAll();
        passwordResetTokenRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    protected @NonNull Usuario createUser(@NonNull String email, @NonNull Role role, @NonNull String password) {
        Usuario usuario = Usuario.builder()
                .nome("Usuario Teste")
                .email(email)
                .senha(passwordEncoder.encode(password))
                .role(role)
                .build();
        return Objects.requireNonNull(usuarioRepository.save(usuario));
    }

    protected @NonNull String loginAndGetToken(@NonNull String email, @NonNull String password) throws Exception {
        var payload = toJson(new LoginRequest(email, password));

        var result = mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        var json = objectMapper.readTree(result.getResponse().getContentAsString());
        return Objects.requireNonNull(json.get("token")).asText();
    }

    protected @NonNull RequestPostProcessor bearerToken(@NonNull String token) {
        return request -> {
            request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }

    protected @NonNull String toJson(@NonNull Object value) throws Exception {
        return Objects.requireNonNull(objectMapper.writeValueAsString(value));
    }

    protected record LoginRequest(String email, String senha) {
    }
}
