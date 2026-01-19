package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class CartaoUsuarioControllerIT extends IntegrationTestSupport {

    @Test
    void shouldCreateListUpdateAndDeleteCard() throws Exception {
        Usuario user = createUser("user7@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        ProgramaFidelidade programa = Objects
                .requireNonNull(programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                        .nome("Programa A")
                        .descricao("Descricao")
                        .build()));

        var payload = toJson(new CartaoRequest(
                "Cartao 1",
                Bandeira.VISA,
                TipoCartao.CREDITO,
                2.5,
                List.of(programa.getId())));

        mockMvc.perform(post("/cartao/criar")
                .with(bearerToken(token))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cartao")
                .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        CartaoUsuario cartao = cartaoUsuarioRepository.findByNomeAndUsuarioId("Cartao 1", user.getId())
                .orElseThrow();

        var updatePayload = toJson(new CartaoRequest(
                "Cartao Atualizado",
                null,
                null,
                3.0,
                List.of(programa.getId())));

        mockMvc.perform(put("/cartao/{id}", cartao.getId())
                .with(bearerToken(token))
                .contentType("application/json")
                .content(updatePayload))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/cartao/{id}", cartao.getId())
                .with(bearerToken(token)))
                .andExpect(status().isNoContent());

        assertThat(cartaoUsuarioRepository.findById(cartao.getId())).isEmpty();
    }

    private record CartaoRequest(
            String nome,
            Bandeira bandeira,
            TipoCartao tipo,
            Double multiplicadorPontos,
            List<Long> programaIds) {
    }
}
