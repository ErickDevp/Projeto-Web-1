package br.edu.ifs.academico.integration;

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
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class ComprovanteControllerIT extends IntegrationTestSupport {

    @Test
    void shouldCreateReadAndDeleteComprovante() throws Exception {
        Usuario user = createUser("user10@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        ProgramaFidelidade programa = Objects.requireNonNull(
                programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                        .nome("Programa D")
                        .descricao("Programa para testes de comprovante")
                        .build())
        );

        Promocao promocao = Objects.requireNonNull(
                promocaoRepository.save(Promocao.builder()
                        .titulo("Promoção Teste")
                        .descricao("Promoção para testes")
                        .dataInicio(LocalDate.now().minusMonths(1))
                        .dataFim(LocalDate.now().plusMonths(1))
                        .programa(programa)
                        .pontosPorReal(1.5)
                        .build())
        );

        CartaoUsuario cartao = Objects.requireNonNull(
                cartaoUsuarioRepository.save(CartaoUsuario.builder()
                        .usuario(user)
                        .nome("Cartao 4")
                        .bandeira(Bandeira.ELO)
                        .tipo(TipoCartao.DEBITO)
                        .numero("6362970000000000")
                        .dataValidade(LocalDate.now().plusYears(2))
                        .programas(Set.of(programa))
                        .build())
        );

        SaldoUsuarioPrograma saldo = Objects.requireNonNull(
                saldoUsuarioProgramaRepository.save(SaldoUsuarioPrograma.builder()
                        .usuario(user)
                        .programa(programa)
                        .pontos(0)
                        .build())
        );

        MovimentacaoPontos movimentacao = MovimentacaoPontos.builder()
                .usuario(user)
                .cartao(cartao)
                .saldo(saldo)
                .valor(new BigDecimal("20.00"))
                .pontos_calculados(30)
                .dataOcorrencia(LocalDate.now())
                .build();

        StatusMovimentacao status = StatusMovimentacao.builder()
                .status(Status.PENDENTE)
                .motivo("Aguardando comprovante")
                .movimentacao(movimentacao)
                .build();

        movimentacao.setStatus(status);

        movimentacao = Objects.requireNonNull(
                movimentacaoPontosRepository.save(movimentacao)
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "comprovante.pdf",
                "application/pdf",
                "conteudo do comprovante de teste".getBytes()
        );

        mockMvc.perform(multipart("/comprovante/criar")
                        .file(file)
                        .param("movimentacaoId", movimentacao.getId().toString())
                        .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movimentacaoId").value(movimentacao.getId()))
                .andExpect(jsonPath("$.tipo_arq").value("application/pdf"));

        mockMvc.perform(get("/comprovante/{id}", movimentacao.getId())
                        .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo_arq").value("application/pdf"));

        var comprovante = comprovanteRepository.findByMovimentacaoId(movimentacao.getId()).get(0);

        mockMvc.perform(get("/comprovante/{id}/arquivo", comprovante.getId())
                        .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().exists("Content-Disposition"));

        mockMvc.perform(delete("/comprovante/{id}", comprovante.getId())
                        .with(bearerToken(token)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/comprovante/{id}", movimentacao.getId())
                        .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldNotCreateComprovanteForNonExistentMovimentacao() throws Exception {
        Usuario user = createUser("user11@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "comprovante.pdf",
                "application/pdf",
                "conteudo".getBytes()
        );

        mockMvc.perform(multipart("/comprovante/criar")
                        .file(file)
                        .param("movimentacaoId", "999999")
                        .with(bearerToken(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotAccessComprovanteFromOtherUser() throws Exception {
        Usuario user1 = createUser("user12@teste.com", Role.USER, DEFAULT_PASSWORD);

        ProgramaFidelidade programa = Objects.requireNonNull(
                programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                        .nome("Programa E")
                        .descricao("Programa teste segurança")
                        .build())
        );

        Promocao promocao = Objects.requireNonNull(
                promocaoRepository.save(Promocao.builder()
                        .titulo("Promoção Segurança")
                        .descricao("Teste de segurança")
                        .dataInicio(LocalDate.now().minusMonths(1))
                        .dataFim(LocalDate.now().plusMonths(1))
                        .programa(programa)
                        .pontosPorReal(1.0)
                        .build())
        );

        CartaoUsuario cartao1 = Objects.requireNonNull(
                cartaoUsuarioRepository.save(CartaoUsuario.builder()
                        .usuario(user1)
                        .nome("Cartao User1")
                        .bandeira(Bandeira.VISA)
                        .tipo(TipoCartao.CREDITO)
                        .numero("4532123456789012")
                        .dataValidade(LocalDate.now().plusYears(3))
                        .programas(Set.of(programa))
                        .build())
        );

        SaldoUsuarioPrograma saldo1 = Objects.requireNonNull(
                saldoUsuarioProgramaRepository.save(SaldoUsuarioPrograma.builder()
                        .usuario(user1)
                        .programa(programa)
                        .pontos(0)
                        .build())
        );

        MovimentacaoPontos movimentacao1 = MovimentacaoPontos.builder()
                .usuario(user1)
                .cartao(cartao1)
                .saldo(saldo1)
                .valor(new BigDecimal("50.00"))
                .pontos_calculados(50)
                .dataOcorrencia(LocalDate.now())
                .build();

        StatusMovimentacao status1 = StatusMovimentacao.builder()
                .status(Status.PENDENTE)
                .motivo("Teste")
                .movimentacao(movimentacao1)
                .build();

        movimentacao1.setStatus(status1);
        movimentacao1 = movimentacaoPontosRepository.save(movimentacao1);

        String token1 = loginAndGetToken(user1.getEmail(), DEFAULT_PASSWORD);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "comprovante.pdf",
                "application/pdf",
                "conteudo".getBytes()
        );

        mockMvc.perform(multipart("/comprovante/criar")
                        .file(file)
                        .param("movimentacaoId", movimentacao1.getId().toString())
                        .with(bearerToken(token1)))
                .andExpect(status().isOk());

        var comprovante = comprovanteRepository.findByMovimentacaoId(movimentacao1.getId()).get(0);

        Usuario user2 = createUser("user13@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token2 = loginAndGetToken(user2.getEmail(), DEFAULT_PASSWORD);

        mockMvc.perform(get("/comprovante/{id}/arquivo", comprovante.getId())
                        .with(bearerToken(token2)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/comprovante/{id}", comprovante.getId())
                        .with(bearerToken(token2)))
                .andExpect(status().isForbidden());
    }
}