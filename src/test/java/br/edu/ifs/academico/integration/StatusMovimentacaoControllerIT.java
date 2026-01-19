package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.entity.enums.StatusMovimentacao;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class StatusMovimentacaoControllerIT extends IntegrationTestSupport {

    @Test
    void shouldCreateGetUpdateAndDeleteStatus() throws Exception {
        Usuario user = createUser("user9@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        ProgramaFidelidade programa = Objects
                .requireNonNull(programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                        .nome("Programa C")
                        .descricao("Descricao")
                        .build()));

        CartaoUsuario cartao = Objects.requireNonNull(cartaoUsuarioRepository.save(CartaoUsuario.builder()
                .usuario(user)
                .nome("Cartao 3")
                .bandeira(Bandeira.MASTERCARD)
                .tipo(TipoCartao.CREDITO)
                .multiplicadorPontos(1.5)
                .programas(Set.of(programa))
                .build()));

        SaldoUsuarioPrograma saldo = Objects
                .requireNonNull(saldoUsuarioProgramaRepository.save(SaldoUsuarioPrograma.builder()
                        .usuario(user)
                        .programa(programa)
                        .pontos(0)
                        .build()));

        MovimentacaoPontos movimentacao = Objects
                .requireNonNull(movimentacaoPontosRepository.save(MovimentacaoPontos.builder()
                        .usuario(user)
                        .cartao(cartao)
                        .saldo(saldo)
                        .valor(new BigDecimal("10.00"))
                        .pontos_calculados(15)
                        .build()));

        var payload = toJson(new StatusRequest(
                movimentacao.getId(),
                StatusMovimentacao.PENDENTE,
                "Aguardando"));

        mockMvc.perform(post("/status/criar")
                .with(bearerToken(token))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk());

        mockMvc.perform(get("/status/{id}", movimentacao.getId())
                .with(bearerToken(token)))
                .andExpect(status().isOk());

        var status = statusMovimentacaoRepository.findByMovimentacaoId(movimentacao.getId()).orElseThrow();

        var updatePayload = toJson(new StatusRequest(
                movimentacao.getId(),
                StatusMovimentacao.CREDITADO,
                "Ok"));

        mockMvc.perform(put("/status/{id}", status.getId())
                .with(bearerToken(token))
                .contentType("application/json")
                .content(updatePayload))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/status/{id}", status.getId())
                .with(bearerToken(token)))
                .andExpect(status().isNoContent());
    }

    private record StatusRequest(Long movimentacaoId, StatusMovimentacao status, String motivo) {
    }
}
