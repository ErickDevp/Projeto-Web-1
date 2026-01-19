package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class SaldoUsuarioProgramaControllerIT extends IntegrationTestSupport {

    @Test
    void shouldListUserBalances() throws Exception {
        Usuario user = createUser("user13@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        ProgramaFidelidade programa = programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                .nome("Programa Saldo")
                .descricao("Descricao")
                .build());

        saldoUsuarioProgramaRepository.save(SaldoUsuarioPrograma.builder()
                .usuario(user)
                .programa(programa)
                .pontos(100)
                .build());

        mockMvc.perform(get("/saldo")
                .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
