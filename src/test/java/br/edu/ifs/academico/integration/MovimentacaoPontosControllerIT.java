package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.Promocao;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.entity.enums.Status;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class MovimentacaoPontosControllerIT extends IntegrationTestSupport {

    @Test
    void shouldCreateListUpdateAndDeleteMovimentacao() throws Exception {
        // 1. Criar usuário e fazer login
        Usuario user = createUser("user8@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        // 2. Criar programa de fidelidade
        ProgramaFidelidade programa = Objects.requireNonNull(
                programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                        .nome("Programa B")
                        .descricao("Programa de pontos para testes")
                        .build())
        );

        // 3. Criar promoção ativa para o programa
        Promocao promocao = Objects.requireNonNull(
                promocaoRepository.save(Promocao.builder()
                        .titulo("Promoção Teste Movimentação")
                        .descricao("2x pontos em todas as compras")
                        .dataInicio(LocalDate.now().minusMonths(1))
                        .dataFim(LocalDate.now().plusMonths(2))
                        .programa(programa)
                        .pontosPorReal(2.0) // 2 pontos por real
                        .build())
        );

        // 4. Criar cartão do usuário vinculado ao programa
        CartaoUsuario cartao = Objects.requireNonNull(
                cartaoUsuarioRepository.save(CartaoUsuario.builder()
                        .usuario(user)
                        .nome("Cartao 2")
                        .bandeira(Bandeira.VISA)
                        .tipo(TipoCartao.CREDITO)
                        .programas(Set.of(programa))
                        .build())
        );

        // 5. Criar saldo inicial (opcional, mas recomendado)
        saldoUsuarioProgramaRepository.save(SaldoUsuarioPrograma.builder()
                .usuario(user)
                .programa(programa)
                .pontos(0)
                .build());

        // 6. Criar movimentação (com promocaoId e data)
        var payload = toJson(new MovimentacaoRequest(
                cartao.getId(),
                programa.getId(),
                promocao.getId(),
                new BigDecimal("100.00"),
                LocalDate.now().minusDays(1)
        ));

        mockMvc.perform(post("/movimentacao/criar")
                        .with(bearerToken(token))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(100.00))
                .andExpect(jsonPath("$.pontos_calculados").value(200)) // 100 * 2.0
                .andExpect(jsonPath("$.status.status").value("PENDENTE"));

        // 7. Listar movimentações
        mockMvc.perform(get("/movimentacao")
                        .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].valor").value(100.00))
                .andExpect(jsonPath("$[0].pontos_calculados").value(200));

        // 8. Buscar a movimentação criada
        MovimentacaoPontos movimentacao = movimentacaoPontosRepository
                .findByUsuarioId(user.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Movimentação não encontrada"));

        // 9. Atualizar movimentação
        var updatePayload = toJson(new MovimentacaoRequest(
                cartao.getId(),
                programa.getId(),
                promocao.getId(),
                new BigDecimal("50.00"),
                LocalDate.now().minusDays(1)
        ));

        mockMvc.perform(put("/movimentacao/{id}", movimentacao.getId())
                        .with(bearerToken(token))
                        .contentType("application/json")
                        .content(updatePayload))
                .andExpect(status().isNoContent());

        // 10. Verificar atualização
        mockMvc.perform(get("/movimentacao")
                        .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].valor").value(50.00))
                .andExpect(jsonPath("$[0].pontos_calculados").value(100)); // 50 * 2.0

        // 11. Deletar movimentação
        mockMvc.perform(delete("/movimentacao/{id}", movimentacao.getId())
                        .with(bearerToken(token)))
                .andExpect(status().isNoContent());

        // 12. Verificar deleção
        assertThat(movimentacaoPontosRepository.findById(movimentacao.getId())).isEmpty();

        mockMvc.perform(get("/movimentacao")
                        .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldNotCreateMovimentacaoWithInvalidCartao() throws Exception {
        Usuario user = createUser("user9@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        var payload = toJson(new MovimentacaoRequest(
                999L, // Cartão inexistente
                1L,
                1L,
                new BigDecimal("100.00"),
                LocalDate.now()
        ));

        mockMvc.perform(post("/movimentacao/criar")
                        .with(bearerToken(token))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotCreateMovimentacaoWithCartaoFromAnotherUser() throws Exception {
        // Criar primeiro usuário com cartão
        Usuario user1 = createUser("user10@teste.com", Role.USER, DEFAULT_PASSWORD);

        ProgramaFidelidade programa = Objects.requireNonNull(
                programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                        .nome("Programa C")
                        .descricao("Programa teste segurança")
                        .build())
        );

        Promocao promocao = Objects.requireNonNull(
                promocaoRepository.save(Promocao.builder()
                        .titulo("Promoção C")
                        .descricao("Teste")
                        .dataInicio(LocalDate.now().minusMonths(1))
                        .dataFim(LocalDate.now().plusMonths(1))
                        .programa(programa)
                        .pontosPorReal(1.5)
                        .build())
        );

        CartaoUsuario cartaoUser1 = Objects.requireNonNull(
                cartaoUsuarioRepository.save(CartaoUsuario.builder()
                        .usuario(user1)
                        .nome("Cartao User1")
                        .bandeira(Bandeira.MASTERCARD)
                        .tipo(TipoCartao.CREDITO)
                        .programas(Set.of(programa))
                        .build())
        );

        // Criar segundo usuário e tentar usar cartão do primeiro
        Usuario user2 = createUser("user11@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token2 = loginAndGetToken(user2.getEmail(), DEFAULT_PASSWORD);

        var payload = toJson(new MovimentacaoRequest(
                cartaoUser1.getId(), // Cartão de outro usuário
                programa.getId(),
                promocao.getId(),
                new BigDecimal("100.00"),
                LocalDate.now()
        ));

        mockMvc.perform(post("/movimentacao/criar")
                        .with(bearerToken(token2))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotCreateMovimentacaoWithProgramaNotLinkedToCartao() throws Exception {
        Usuario user = createUser("user12@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        // Criar dois programas
        ProgramaFidelidade programa1 = Objects.requireNonNull(
                programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                        .nome("Programa D")
                        .descricao("Programa vinculado")
                        .build())
        );

        ProgramaFidelidade programa2 = Objects.requireNonNull(
                programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                        .nome("Programa E")
                        .descricao("Programa NÃO vinculado")
                        .build())
        );

        Promocao promocao2 = Objects.requireNonNull(
                promocaoRepository.save(Promocao.builder()
                        .titulo("Promoção E")
                        .descricao("Teste")
                        .dataInicio(LocalDate.now().minusMonths(1))
                        .dataFim(LocalDate.now().plusMonths(1))
                        .programa(programa2)
                        .pontosPorReal(1.0)
                        .build())
        );

        // Cartão vinculado apenas ao programa1
        CartaoUsuario cartao = Objects.requireNonNull(
                cartaoUsuarioRepository.save(CartaoUsuario.builder()
                        .usuario(user)
                        .nome("Cartao Teste")
                        .bandeira(Bandeira.ELO)
                        .tipo(TipoCartao.CREDITO)
                        .programas(Set.of(programa1)) // Apenas programa1
                        .build())
        );

        // Tentar criar movimentação com programa2
        var payload = toJson(new MovimentacaoRequest(
                cartao.getId(),
                programa2.getId(), // Programa não vinculado ao cartão
                promocao2.getId(),
                new BigDecimal("100.00"),
                LocalDate.now()
        ));

        mockMvc.perform(post("/movimentacao/criar")
                        .with(bearerToken(token))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCalculatePointsCorrectlyBasedOnPromocao() throws Exception {
        Usuario user = createUser("user13@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        ProgramaFidelidade programa = Objects.requireNonNull(
                programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                        .nome("Programa F")
                        .descricao("Teste cálculo de pontos")
                        .build())
        );

        // Promoção com 3.5 pontos por real
        Promocao promocao = Objects.requireNonNull(
                promocaoRepository.save(Promocao.builder()
                        .titulo("Promoção 3.5x")
                        .descricao("3.5 pontos por real")
                        .dataInicio(LocalDate.now().minusMonths(1))
                        .dataFim(LocalDate.now().plusMonths(1))
                        .programa(programa)
                        .pontosPorReal(3.5)
                        .build())
        );

        CartaoUsuario cartao = Objects.requireNonNull(
                cartaoUsuarioRepository.save(CartaoUsuario.builder()
                        .usuario(user)
                        .nome("Cartao Teste Calculo")
                        .bandeira(Bandeira.VISA)
                        .tipo(TipoCartao.CREDITO)
                        .programas(Set.of(programa))
                        .build())
        );

        saldoUsuarioProgramaRepository.save(SaldoUsuarioPrograma.builder()
                .usuario(user)
                .programa(programa)
                .pontos(0)
                .build());

        var payload = toJson(new MovimentacaoRequest(
                cartao.getId(),
                programa.getId(),
                promocao.getId(),
                new BigDecimal("123.45"), // Valor com decimais
                LocalDate.now()
        ));

        mockMvc.perform(post("/movimentacao/criar")
                        .with(bearerToken(token))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(123.45))
                .andExpect(jsonPath("$.pontos_calculados").value(432)); // 123.45 * 3.5 = 432.075 -> 432 (arredondado para baixo)
    }

    private record MovimentacaoRequest(
            Long cartaoId,
            Long programaId,
            Long promocaoId,
            BigDecimal valor,
            LocalDate data
    ) {}
}