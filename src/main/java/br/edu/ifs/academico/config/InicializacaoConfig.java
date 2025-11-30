package br.edu.ifs.academico.config;

import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import br.edu.ifs.academico.repository.CartaoUsuarioRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import br.edu.ifs.academico.repository.ProgramaFidelidadeRepository;
import br.edu.ifs.academico.repository.SaldoUsuarioProgramaRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;

@Configuration
public class InicializacaoConfig {

    private static final String ADMIN_EMAIL = "admin@milhas.com";
    private static final String ADMIN_SENHA = "123456";
    private static final String PROGRAMA_PADRAO = "Programa Milhas Padrão";
    private static final String CARTAO_PADRAO = "Cartão Padrão";

    @Bean
    CommandLineRunner init(UsuarioRepository usuarioRepository,
                           ProgramaFidelidadeRepository programaRepository,
                           CartaoUsuarioRepository cartaoRepository,
                           SaldoUsuarioProgramaRepository saldoRepository,
                           MovimentacaoPontosRepository movimentacaoRepository,
                           PasswordEncoder encoder) {
        return args -> {

            Usuario admin;
            if (usuarioRepository.existsByEmail(ADMIN_EMAIL)) {
                admin = usuarioRepository.findByEmail(ADMIN_EMAIL)
                        .orElseThrow(() -> new IllegalStateException("Usuário admin deveria existir"));
                System.out.println(">>> ADMIN já existe, não será recriado.");
            } else {
                Usuario novoAdmin = new Usuario();
                novoAdmin.setNome("Administrador");
                novoAdmin.setEmail(ADMIN_EMAIL);
                novoAdmin.setSenha(encoder.encode(ADMIN_SENHA));
                novoAdmin.setRole(Role.ADMIN);
                admin = usuarioRepository.save(novoAdmin);
                System.out.println(">>> ADMIN criado automaticamente!");
            }

            ProgramaFidelidade programa = programaRepository.findByNome(PROGRAMA_PADRAO)
                    .orElseGet(() -> {
                        ProgramaFidelidade novoPrograma = ProgramaFidelidade.builder()
                                .nome(PROGRAMA_PADRAO)
                                .descricao("Programa padrão criado na inicialização")
                                .build();
                        System.out.println(">>> Programa padrão criado!");
                        return programaRepository.save(novoPrograma);
                    });

            SaldoUsuarioPrograma saldo = saldoRepository
                    .findByUsuarioIdAndProgramaId(admin.getId(), programa.getId())
                    .orElseGet(() -> {
                        SaldoUsuarioPrograma novoSaldo = SaldoUsuarioPrograma.builder()
                                .usuario(admin)
                                .programa(programa)
                                .pontos(0)
                                .build();
                        System.out.println(">>> Saldo padrão criado!");
                        return saldoRepository.save(novoSaldo);
                    });

            CartaoUsuario cartao = cartaoRepository
                    .findByNomeAndUsuarioId(CARTAO_PADRAO, admin.getId())
                    .orElseGet(() -> {
                        CartaoUsuario novoCartao = CartaoUsuario.builder()
                                .nome(CARTAO_PADRAO)
                                .bandeira(Bandeira.VISA)
                                .tipo(TipoCartao.CREDITO)
                                .pontos(0d)
                                .usuario(admin)
                                .programas(new HashSet<>())
                                .build();
                        novoCartao.getProgramas().add(programa);
                        System.out.println(">>> Cartão padrão criado!");
                        return cartaoRepository.save(novoCartao);
                    });

            if (movimentacaoRepository.findByUsuarioId(admin.getId()).isEmpty()) {
                MovimentacaoPontos movimentacao = MovimentacaoPontos.builder()
                        .valor(BigDecimal.valueOf(1000.00))
                        .pontos_calculados(1000)
                        .dataOcorrencia(LocalDate.now())
                        .usuario(admin)
                        .saldo(saldo)
                        .cartao(cartao)
                        .build();
                movimentacaoRepository.save(movimentacao);
                System.out.println(">>> Movimentação padrão criada!");
            } else {
                System.out.println(">>> Usuário admin já possui movimentações, não será criada outra.");
            }
        };
    }
}
