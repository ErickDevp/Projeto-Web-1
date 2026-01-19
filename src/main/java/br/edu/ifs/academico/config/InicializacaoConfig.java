package br.edu.ifs.academico.config;

import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.entity.StatusMovimentacao;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import br.edu.ifs.academico.repository.CartaoUsuarioRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import br.edu.ifs.academico.repository.ProgramaFidelidadeRepository;
import br.edu.ifs.academico.repository.SaldoUsuarioProgramaRepository;
import br.edu.ifs.academico.repository.StatusMovimentacaoRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;

@Configuration
@Profile("!test")
@SuppressWarnings("null")
public class InicializacaoConfig {

    private static final Logger log = LoggerFactory.getLogger(InicializacaoConfig.class);

    private static final String ADMIN_EMAIL = "admin@milhas.com";
    private static final String ADMIN_SENHA = "123456";
    private static final String PROGRAMA_LIVELO = "Livelo";
    private static final String PROGRAMA_ESFERA = "Esfera";
    private static final String PROGRAMA_LATAM_PASS = "Latam Pass";

    private static final String CARTAO_VISA_INFINITE = "Cartão A (Visa Infinite)";
    private static final String CARTAO_MASTERCARD_BLACK = "Cartão B (Mastercard Black)";
    private static final String CARTAO_ELO_NANQUIM = "Cartão C (Elo Nanquim)";

    @Bean
    CommandLineRunner init(UsuarioRepository usuarioRepository,
            ProgramaFidelidadeRepository programaRepository,
            CartaoUsuarioRepository cartaoRepository,
            SaldoUsuarioProgramaRepository saldoRepository,
            MovimentacaoPontosRepository movimentacaoRepository,
            StatusMovimentacaoRepository statusRepository,
            PasswordEncoder encoder) {
        return args -> {

            Usuario admin;
            if (usuarioRepository.existsByEmail(ADMIN_EMAIL)) {
                admin = usuarioRepository.findByEmail(ADMIN_EMAIL)
                        .orElseThrow(() -> new IllegalStateException("Usuário admin deveria existir"));
                log.info("ADMIN já existe, não será recriado.");
            } else {
                Usuario novoAdmin = new Usuario();
                novoAdmin.setNome("Administrador");
                novoAdmin.setEmail(ADMIN_EMAIL);
                novoAdmin.setSenha(encoder.encode(ADMIN_SENHA));
                novoAdmin.setRole(Role.ADMIN);
                admin = usuarioRepository.save(novoAdmin);
                log.info("ADMIN criado automaticamente!");
            }

            ProgramaFidelidade livelo = getOrCreatePrograma(programaRepository, PROGRAMA_LIVELO,
                    "Programa de fidelidade Livelo");
            ProgramaFidelidade esfera = getOrCreatePrograma(programaRepository, PROGRAMA_ESFERA,
                    "Programa de fidelidade Esfera (Santander)");
            ProgramaFidelidade latam = getOrCreatePrograma(programaRepository, PROGRAMA_LATAM_PASS,
                    "Programa de fidelidade Latam Pass");

            SaldoUsuarioPrograma saldoLivelo = getOrCreateSaldo(saldoRepository, admin, livelo);
            SaldoUsuarioPrograma saldoEsfera = getOrCreateSaldo(saldoRepository, admin, esfera);
            SaldoUsuarioPrograma saldoLatam = getOrCreateSaldo(saldoRepository, admin, latam);

            CartaoUsuario cartaoVisa = getOrCreateCartao(cartaoRepository, admin, CARTAO_VISA_INFINITE, Bandeira.VISA,
                    2.5d);
            CartaoUsuario cartaoMaster = getOrCreateCartao(cartaoRepository, admin, CARTAO_MASTERCARD_BLACK,
                    Bandeira.MASTERCARD, 2.0d);
            CartaoUsuario cartaoElo = getOrCreateCartao(cartaoRepository, admin, CARTAO_ELO_NANQUIM, Bandeira.ELO,
                    1.2d);

            ensureProgramas(cartaoRepository, cartaoVisa, livelo, latam);
            ensureProgramas(cartaoRepository, cartaoMaster, esfera, livelo);
            ensureProgramas(cartaoRepository, cartaoElo, latam);

            if (movimentacaoRepository.findByUsuarioId(admin.getId()).isEmpty()) {
                criarMovimentacao(movimentacaoRepository, statusRepository, saldoRepository, cartaoRepository,
                        admin, saldoLivelo, cartaoVisa, BigDecimal.valueOf(12990.00), 12990,
                        LocalDate.now().minusMonths(3).withDayOfMonth(5),
                        br.edu.ifs.academico.entity.enums.StatusMovimentacao.CREDITADO,
                        "Compra grande no Visa Infinite");

                criarMovimentacao(movimentacaoRepository, statusRepository, saldoRepository, cartaoRepository,
                        admin, saldoEsfera, cartaoMaster, BigDecimal.valueOf(3290.00), 3290,
                        LocalDate.now().minusMonths(2).withDayOfMonth(12),
                        br.edu.ifs.academico.entity.enums.StatusMovimentacao.CREDITADO,
                        "Compra recorrente no Mastercard Black");

                criarMovimentacao(movimentacaoRepository, statusRepository, saldoRepository, cartaoRepository,
                        admin, saldoEsfera, cartaoMaster, BigDecimal.valueOf(1850.00), 1850,
                        LocalDate.now().minusMonths(1).withDayOfMonth(3),
                        br.edu.ifs.academico.entity.enums.StatusMovimentacao.CREDITADO,
                        "Compra de rotina no Mastercard Black");

                criarMovimentacao(movimentacaoRepository, statusRepository, saldoRepository, cartaoRepository,
                        admin, saldoLivelo, cartaoVisa, BigDecimal.valueOf(2250.00), 2250,
                        LocalDate.now().minusMonths(0).withDayOfMonth(8),
                        br.edu.ifs.academico.entity.enums.StatusMovimentacao.PENDENTE,
                        "Compra recente aguardando crédito");

                criarMovimentacao(movimentacaoRepository, statusRepository, saldoRepository, cartaoRepository,
                        admin, saldoLatam, cartaoElo, BigDecimal.valueOf(600.00), 600,
                        LocalDate.now().minusMonths(1).withDayOfMonth(22),
                        br.edu.ifs.academico.entity.enums.StatusMovimentacao.CREDITADO,
                        "Compra pequena no Elo Nanquim");

                criarMovimentacao(movimentacaoRepository, statusRepository, saldoRepository, cartaoRepository,
                        admin, saldoLatam, cartaoElo, BigDecimal.valueOf(320.00), 320,
                        LocalDate.now().minusMonths(3).withDayOfMonth(19),
                        br.edu.ifs.academico.entity.enums.StatusMovimentacao.CREDITADO,
                        "Uso pouco frequente do Elo Nanquim");

                log.info("Movimentações de exemplo criadas e saldos atualizados!");
            } else {
                log.info("Usuário admin já possui movimentações, não será criada outra.");
            }
        };
    }

    private ProgramaFidelidade getOrCreatePrograma(ProgramaFidelidadeRepository programaRepository, String nome,
            String descricao) {
        return programaRepository.findByNome(nome).orElseGet(() -> {
            ProgramaFidelidade novoPrograma = ProgramaFidelidade.builder()
                    .nome(nome)
                    .descricao(descricao)
                    .build();
            log.info("Programa {} criado!", nome);
            return programaRepository.save(novoPrograma);
        });
    }

    private SaldoUsuarioPrograma getOrCreateSaldo(SaldoUsuarioProgramaRepository saldoRepository, Usuario usuario,
            ProgramaFidelidade programa) {
        return saldoRepository.findByUsuarioIdAndProgramaId(usuario.getId(), programa.getId()).orElseGet(() -> {
            SaldoUsuarioPrograma novoSaldo = SaldoUsuarioPrograma.builder()
                    .usuario(usuario)
                    .programa(programa)
                    .pontos(0)
                    .build();
            log.info("Saldo criado para o programa {}", programa.getNome());
            return saldoRepository.save(novoSaldo);
        });
    }

    private CartaoUsuario getOrCreateCartao(CartaoUsuarioRepository cartaoRepository, Usuario usuario, String nome,
            Bandeira bandeira, Double multiplicadorPontos) {
        return cartaoRepository.findByNomeAndUsuarioId(nome, usuario.getId()).map(cartaoExistente -> {
            boolean updated = false;
            if (cartaoExistente.getMultiplicadorPontos() == null
                    || !cartaoExistente.getMultiplicadorPontos().equals(multiplicadorPontos)) {
                cartaoExistente.setMultiplicadorPontos(multiplicadorPontos);
                updated = true;
            }
            if (cartaoExistente.getBandeira() == null) {
                cartaoExistente.setBandeira(bandeira);
                updated = true;
            }
            if (cartaoExistente.getTipo() == null) {
                cartaoExistente.setTipo(TipoCartao.CREDITO);
                updated = true;
            }
            if (updated) {
                cartaoRepository.save(cartaoExistente);
                log.info("Cartão {} atualizado com multiplicador de pontos {}", nome, multiplicadorPontos);
            }
            return cartaoExistente;
        }).orElseGet(() -> {
            CartaoUsuario novoCartao = CartaoUsuario.builder()
                    .nome(nome)
                    .bandeira(bandeira)
                    .tipo(TipoCartao.CREDITO)
                    .multiplicadorPontos(multiplicadorPontos)
                    .usuario(usuario)
                    .programas(new HashSet<>())
                    .build();
            log.info("Cartão {} criado!", nome);
            return cartaoRepository.save(novoCartao);
        });
    }

    private void ensureProgramas(CartaoUsuarioRepository cartaoRepository, CartaoUsuario cartao,
            ProgramaFidelidade... programas) {
        cartaoRepository.findWithProgramasById(cartao.getId()).ifPresent(cartaoAtualizado -> {
            boolean updated = false;
            if (cartaoAtualizado.getProgramas() == null) {
                cartaoAtualizado.setProgramas(new HashSet<>());
                updated = true;
            }
            for (ProgramaFidelidade programa : programas) {
                if (cartaoAtualizado.getProgramas().add(programa)) {
                    updated = true;
                }
            }
            if (updated) {
                cartaoRepository.save(cartaoAtualizado);
            }
        });
    }

    private void criarMovimentacao(MovimentacaoPontosRepository movimentacaoRepository,
            StatusMovimentacaoRepository statusRepository, SaldoUsuarioProgramaRepository saldoRepository,
            CartaoUsuarioRepository cartaoRepository, Usuario usuario, SaldoUsuarioPrograma saldo,
            CartaoUsuario cartao, BigDecimal valor, Integer pontos, LocalDate data,
            br.edu.ifs.academico.entity.enums.StatusMovimentacao statusEnum, String motivo) {
        MovimentacaoPontos movimentacao = MovimentacaoPontos.builder()
                .valor(valor)
                .pontos_calculados(pontos)
                .dataOcorrencia(data)
                .usuario(usuario)
                .saldo(saldo)
                .cartao(cartao)
                .build();

        StatusMovimentacao status = StatusMovimentacao.builder()
                .status(statusEnum)
                .motivo(motivo)
                .movimentacao(movimentacao)
                .build();

        movimentacao.setStatus(status);
        movimentacaoRepository.save(movimentacao);
        statusRepository.save(status);

        saldo.setPontos(saldo.getPontos() + pontos);
        saldoRepository.save(saldo);
    }
}
