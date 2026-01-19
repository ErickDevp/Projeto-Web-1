package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class MovimentacaoPontosControllerIT extends IntegrationTestSupport {

    @Test
    void shouldCreateListUpdateAndDeleteMovimentacao() throws Exception {
        Usuario user = createUser("user8@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        ProgramaFidelidade programa = Objects
                .requireNonNull(programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                        .nome("Programa B")
                        .descricao("Descricao")
                        .build()));

        CartaoUsuario cartao = Objects.requireNonNull(cartaoUsuarioRepository.save(CartaoUsuario.builder()
                .usuario(user)
                .nome("Cartao 2")
                .bandeira(Bandeira.VISA)
                .tipo(TipoCartao.CREDITO)
                .multiplicadorPontos(2.0)
                .programas(Set.of(programa))
                .build()));

        var payload = toJson(new MovimentacaoRequest(
                cartao.getId(),
                programa.getId(),
                new BigDecimal("100.00")));

        mockMvc.perform(post("/movimentacao/criar")
                .with(bearerToken(token))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk());

        mockMvc.perform(get("/movimentacao")
                .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        MovimentacaoPontos movimentacao = movimentacaoPontosRepository.findByUsuarioId(user.getId())
                .stream().findFirst().orElseThrow();

        var updatePayload = toJson(new MovimentacaoRequest(
                cartao.getId(),
                programa.getId(),
                new BigDecimal("50.00")));

        mockMvc.perform(put("/movimentacao/{id}", movimentacao.getId())
                .with(bearerToken(token))
                .contentType("application/json")
                .content(updatePayload))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/movimentacao/{id}", movimentacao.getId())
                .with(bearerToken(token)))
                .andExpect(status().isNoContent());

        assertThat(movimentacaoPontosRepository.findById(movimentacao.getId())).isEmpty();
    }

    private record MovimentacaoRequest(Long cartaoId, Long programaId, BigDecimal valor) {
    }
}
