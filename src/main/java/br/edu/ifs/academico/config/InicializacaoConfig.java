package br.edu.ifs.academico.config;

import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.Promocao;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.entity.StatusMovimentacao;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.entity.enums.Status;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import br.edu.ifs.academico.repository.CartaoUsuarioRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import br.edu.ifs.academico.repository.ProgramaFidelidadeRepository;
import br.edu.ifs.academico.repository.PromocaoRepository;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;

@Configuration
@Profile("!test")
@SuppressWarnings("null")
public class InicializacaoConfig {

    private final UsuarioRepository usuarioRepository;
    private final ProgramaFidelidadeRepository programaRepository;
    private final CartaoUsuarioRepository cartaoRepository;
    private final SaldoUsuarioProgramaRepository saldoRepository;
    private final MovimentacaoPontosRepository movimentacaoRepository;
    private final StatusMovimentacaoRepository statusRepository;
    private final PromocaoRepository promocaoRepository;
    private final PasswordEncoder encoder;

    public InicializacaoConfig(UsuarioRepository usuarioRepository,
                               ProgramaFidelidadeRepository programaRepository,
                               CartaoUsuarioRepository cartaoRepository,
                               SaldoUsuarioProgramaRepository saldoRepository,
                               MovimentacaoPontosRepository movimentacaoRepository,
                               StatusMovimentacaoRepository statusRepository,
                               PromocaoRepository promocaoRepository,
                               PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.programaRepository = programaRepository;
        this.cartaoRepository = cartaoRepository;
        this.saldoRepository = saldoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.statusRepository = statusRepository;
        this.promocaoRepository = promocaoRepository;
        this.encoder = encoder;
    }

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
    CommandLineRunner init() {
        return args -> {
            log.info("=== Iniciando carga de dados ===");

            // 1. Criação dos Programas de Fidelidade
            ProgramaFidelidade livelo = getOrCreatePrograma(PROGRAMA_LIVELO,
                    "Programa de fidelidade Livelo");
            ProgramaFidelidade esfera = getOrCreatePrograma(PROGRAMA_ESFERA,
                    "Programa de fidelidade Esfera (Santander)");
            ProgramaFidelidade latam = getOrCreatePrograma(PROGRAMA_LATAM_PASS,
                    "Programa de fidelidade Latam Pass");

            // 2. Criação das Promoções para cada Programa
            Promocao promocaoLivelo = getOrCreatePromocao(livelo,
                    "Promoção Padrão Livelo",
                    "Acumule pontos em todas as compras",
                    2.5);

            Promocao promocaoEsfera = getOrCreatePromocao(esfera,
                    "Promoção Padrão Esfera",
                    "Ganhe pontos Esfera em compras",
                    2.0);

            Promocao promocaoLatam = getOrCreatePromocao(latam,
                    "Promoção Padrão Latam Pass",
                    "Acumule milhas em suas compras",
                    1.2);

            // 3. Criação do Usuário Admin
            Usuario admin = getOrCreateAdmin();

            // 4. Criação dos Saldos
            SaldoUsuarioPrograma saldoLivelo = getOrCreateSaldo(admin, livelo);
            SaldoUsuarioPrograma saldoEsfera = getOrCreateSaldo(admin, esfera);
            SaldoUsuarioPrograma saldoLatam = getOrCreateSaldo(admin, latam);

            // 5. Criação dos Cartões
            CartaoUsuario cartaoVisa = getOrCreateCartao(admin, CARTAO_VISA_INFINITE,
                    Bandeira.VISA);
            CartaoUsuario cartaoMaster = getOrCreateCartao(admin, CARTAO_MASTERCARD_BLACK,
                    Bandeira.MASTERCARD);
            CartaoUsuario cartaoElo = getOrCreateCartao(admin, CARTAO_ELO_NANQUIM,
                    Bandeira.ELO);

            // 6. Associação dos Programas aos Cartões
            ensureProgramas(cartaoVisa, livelo, latam);
            ensureProgramas(cartaoMaster, esfera, livelo);
            ensureProgramas(cartaoElo, latam);

            // 7. Criação das Movimentações
            criarMovimentacoesExemplo(admin, saldoLivelo, saldoEsfera, saldoLatam,
                    cartaoVisa, cartaoMaster, cartaoElo,
                    promocaoLivelo, promocaoEsfera, promocaoLatam);

            log.info("=== Carga de dados concluída ===");
        };
    }

    private ProgramaFidelidade getOrCreatePrograma(String nome, String descricao) {
        return programaRepository.findByNome(nome).orElseGet(() -> {
            ProgramaFidelidade novoPrograma = ProgramaFidelidade.builder()
                    .nome(nome)
                    .descricao(descricao)
                    .build();
            ProgramaFidelidade salvo = programaRepository.save(novoPrograma);
            log.info("✓ Programa '{}' criado (ID: {})", nome, salvo.getId());
            return salvo;
        });
    }

    private Promocao getOrCreatePromocao(ProgramaFidelidade programa, String titulo,
                                         String descricao, Double pontosPorReal) {

        // Busca promoção ativa existente para este programa
        return promocaoRepository.findByProgramaIdAndDataFimGreaterThanEqual(programa.getId(), LocalDate.now())
                .stream()
                .filter(p -> p.getTitulo().equals(titulo))
                .findFirst()
                .orElseGet(() -> {
                    Promocao novaPromocao = Promocao.builder()
                            .titulo(titulo)
                            .descricao(descricao)
                            .dataInicio(LocalDate.now().minusMonths(6))
                            .dataFim(LocalDate.now().plusYears(1))
                            .programa(programa)
                            .pontosPorReal(pontosPorReal)
                            .build();
                    Promocao salva = promocaoRepository.save(novaPromocao);
                    log.info("✓ Promoção '{}' criada para '{}' ({}x pontos, ID: {})",
                            titulo, programa.getNome(), pontosPorReal, salva.getId());
                    return salva;
                });
    }

    private Usuario getOrCreateAdmin() {
        if (usuarioRepository.existsByEmail(ADMIN_EMAIL)) {
            Usuario admin = usuarioRepository.findByEmail(ADMIN_EMAIL)
                    .orElseThrow(() -> new IllegalStateException("Usuário admin deveria existir"));
            log.info("✓ ADMIN já existe (ID: {})", admin.getId());
            return admin;
        } else {
            Usuario novoAdmin = new Usuario();
            novoAdmin.setNome("Administrador");
            novoAdmin.setEmail(ADMIN_EMAIL);
            novoAdmin.setSenha(encoder.encode(ADMIN_SENHA));
            novoAdmin.setRole(Role.ADMIN);
            Usuario salvo = usuarioRepository.save(novoAdmin);
            log.info("✓ ADMIN criado (ID: {}, Email: {}, Senha: {})",
                    salvo.getId(), ADMIN_EMAIL, ADMIN_SENHA);
            return salvo;
        }
    }

    private SaldoUsuarioPrograma getOrCreateSaldo(Usuario usuario, ProgramaFidelidade programa) {
        return saldoRepository.findByUsuarioIdAndProgramaId(usuario.getId(), programa.getId())
                .orElseGet(() -> {
                    SaldoUsuarioPrograma novoSaldo = SaldoUsuarioPrograma.builder()
                            .usuario(usuario)
                            .programa(programa)
                            .pontos(0)
                            .build();
                    SaldoUsuarioPrograma salvo = saldoRepository.save(novoSaldo);
                    log.info("✓ Saldo criado para programa '{}' (ID: {})",
                            programa.getNome(), salvo.getId());
                    return salvo;
                });
    }

    private CartaoUsuario getOrCreateCartao(Usuario usuario, String nome, Bandeira bandeira) {
        return cartaoRepository.findByNomeAndUsuarioId(nome, usuario.getId())
                .map(cartaoExistente -> {
                    boolean updated = false;

                    if (cartaoExistente.getBandeira() == null) {
                        cartaoExistente.setBandeira(bandeira);
                        updated = true;
                    }

                    if (cartaoExistente.getTipo() == null) {
                        cartaoExistente.setTipo(TipoCartao.CREDITO);
                        updated = true;
                    }

                    if (updated) {
                        CartaoUsuario salvo = cartaoRepository.save(cartaoExistente);
                        log.info("✓ Cartão '{}' atualizado", nome);
                        return salvo;
                    }

                    return cartaoExistente;
                })
                .orElseGet(() -> {
                    CartaoUsuario novoCartao = CartaoUsuario.builder()
                            .nome(nome)
                            .bandeira(bandeira)
                            .tipo(TipoCartao.CREDITO)
                            .usuario(usuario)
                            .programas(new HashSet<>())
                            .build();
                    CartaoUsuario salvo = cartaoRepository.save(novoCartao);
                    log.info("✓ Cartão '{}' criado (ID: {})", nome, salvo.getId());
                    return salvo;
                });
    }

    private void ensureProgramas(CartaoUsuario cartao, ProgramaFidelidade... programas) {
        cartaoRepository.findWithProgramasById(cartao.getId()).ifPresent(cartaoAtualizado -> {
            boolean updated = false;

            if (cartaoAtualizado.getProgramas() == null) {
                cartaoAtualizado.setProgramas(new HashSet<>());
            }

            for (ProgramaFidelidade programa : programas) {
                if (cartaoAtualizado.getProgramas().add(programa)) {
                    updated = true;
                    log.info("  → Programa '{}' associado ao cartão '{}'",
                            programa.getNome(), cartao.getNome());
                }
            }

            if (updated) {
                cartaoRepository.save(cartaoAtualizado);
            }
        });
    }

    private void criarMovimentacoesExemplo(Usuario admin,
                                           SaldoUsuarioPrograma saldoLivelo, SaldoUsuarioPrograma saldoEsfera,
                                           SaldoUsuarioPrograma saldoLatam, CartaoUsuario cartaoVisa,
                                           CartaoUsuario cartaoMaster, CartaoUsuario cartaoElo,
                                           Promocao promocaoLivelo, Promocao promocaoEsfera, Promocao promocaoLatam) {

        if (!movimentacaoRepository.findByUsuarioId(admin.getId()).isEmpty()) {
            log.info("✓ Movimentações já existem para o admin");
            return;
        }

        log.info("Criando movimentações de exemplo...");

        // Movimentação 1: Compra grande no Visa Infinite (Livelo - 2.5x)
        criarMovimentacaoSeNaoExiste(admin, saldoLivelo, cartaoVisa, promocaoLivelo,
                BigDecimal.valueOf(12990.00),
                LocalDate.now().minusMonths(3).withDayOfMonth(5),
                Status.CREDITADO,
                "Compra grande no Visa Infinite");

        // Movimentação 2: Compra recorrente no Mastercard Black (Esfera - 2.0x)
        criarMovimentacaoSeNaoExiste(admin, saldoEsfera, cartaoMaster, promocaoEsfera,
                BigDecimal.valueOf(3290.00),
                LocalDate.now().minusMonths(2).withDayOfMonth(12),
                Status.CREDITADO,
                "Compra recorrente no Mastercard Black");

        // Movimentação 3: Compra de rotina no Mastercard Black (Esfera - 2.0x)
        criarMovimentacaoSeNaoExiste(admin, saldoEsfera, cartaoMaster, promocaoEsfera,
                BigDecimal.valueOf(1850.00),
                LocalDate.now().minusMonths(1).withDayOfMonth(3),
                Status.CREDITADO,
                "Compra de rotina no Mastercard Black");

        // Movimentação 4: Compra recente PENDENTE (Livelo - 2.5x)
        criarMovimentacaoSeNaoExiste(admin, saldoLivelo, cartaoVisa, promocaoLivelo,
                BigDecimal.valueOf(2250.00),
                LocalDate.now().withDayOfMonth(8),
                Status.PENDENTE,
                "Compra recente aguardando crédito");

        // Movimentação 5: Compra pequena no Elo Nanquim (Latam - 1.2x)
        criarMovimentacaoSeNaoExiste(admin, saldoLatam, cartaoElo, promocaoLatam,
                BigDecimal.valueOf(600.00),
                LocalDate.now().minusMonths(1).withDayOfMonth(22),
                Status.CREDITADO,
                "Compra pequena no Elo Nanquim");

        // Movimentação 6: Uso pouco frequente do Elo Nanquim (Latam - 1.2x)
        criarMovimentacaoSeNaoExiste(admin, saldoLatam, cartaoElo, promocaoLatam,
                BigDecimal.valueOf(320.00),
                LocalDate.now().minusMonths(3).withDayOfMonth(19),
                Status.CREDITADO,
                "Uso pouco frequente do Elo Nanquim");

        log.info("✓ Movimentações de exemplo criadas!");
    }

    private void criarMovimentacaoSeNaoExiste(Usuario usuario,
                                              SaldoUsuarioPrograma saldo, CartaoUsuario cartao, Promocao promocao,
                                              BigDecimal valor, LocalDate data, Status statusEnum, String motivo) {

        // Calcula pontos baseado na promoção (mesmo cálculo do service)
        int pontosCalculados = valor
                .multiply(BigDecimal.valueOf(promocao.getPontosPorReal()))
                .setScale(0, RoundingMode.DOWN)
                .intValue();

        // Verifica se já existe
        boolean exists = movimentacaoRepository.findByUsuarioId(usuario.getId()).stream()
                .anyMatch(m -> m.getSaldo().getId().equals(saldo.getId())
                        && m.getCartao().getId().equals(cartao.getId())
                        && m.getValor().compareTo(valor) == 0
                        && m.getDataOcorrencia().equals(data)
                        && m.getStatus() != null
                        && m.getStatus().getStatus() == statusEnum);

        if (exists) {
            log.info("  → Movimentação já existe: {} em {}", motivo, data);
            return;
        }

        // Cria a movimentação
        MovimentacaoPontos movimentacao = MovimentacaoPontos.builder()
                .valor(valor)
                .pontos_calculados(pontosCalculados)
                .dataOcorrencia(data)
                .usuario(usuario)
                .saldo(saldo)
                .cartao(cartao)
                .build();

        // Cria o status associado
        StatusMovimentacao status = StatusMovimentacao.builder()
                .status(statusEnum)
                .motivo(motivo)
                .movimentacao(movimentacao)
                .build();

        // Relacionamento bidirecional
        movimentacao.setStatus(status);

        // Salva apenas a raiz (com cascade)
        movimentacaoRepository.save(movimentacao);

        // Atualiza saldo apenas se CREDITADO
        if (statusEnum == Status.CREDITADO) {
            saldo.setPontos(saldo.getPontos() + pontosCalculados);
            saldoRepository.save(saldo);
            log.info("  ✓ R$ {} → {} pontos ({}x = {}) | Status: CREDITADO | Saldo total: {}",
                    valor, pontosCalculados, promocao.getPontosPorReal(),
                    promocao.getTitulo(), saldo.getPontos());
        } else {
            log.info("  ✓ R$ {} → {} pontos ({}x = {}) | Status: {}",
                    valor, pontosCalculados, promocao.getPontosPorReal(),
                    promocao.getTitulo(), statusEnum);
        }
    }}