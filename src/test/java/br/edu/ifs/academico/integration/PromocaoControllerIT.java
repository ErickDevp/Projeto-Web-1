package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.Promocao;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class PromocaoControllerIT extends IntegrationTestSupport {

    @Test
    void shouldCreateListUpdateAndDeletePromotion() throws Exception {
        Usuario admin = createUser("admin3@teste.com", Role.ADMIN, DEFAULT_PASSWORD);
        String adminToken = loginAndGetToken(admin.getEmail(), DEFAULT_PASSWORD);

        ProgramaFidelidade programa = programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                .nome("Programa Z")
                .descricao("Descricao")
                .build());

        var payload = toJson(new PromocaoRequest(
                programa.getId(),
                "Promo 1",
                "Descricao",
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                true));

        mockMvc.perform(post("/promocao/criar")
                .with(bearerToken(adminToken))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk());

        mockMvc.perform(get("/promocao")
                .with(bearerToken(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        Promocao promocao = promocaoRepository.findAll().get(0);

        var updatePayload = toJson(new PromocaoRequest(
                programa.getId(),
                "Promo 2",
                "Descricao 2",
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                false));

        mockMvc.perform(put("/promocao/{id}", promocao.getId())
                .with(bearerToken(adminToken))
                .contentType("application/json")
                .content(updatePayload))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/promocao/{id}", promocao.getId())
                .with(bearerToken(adminToken)))
                .andExpect(status().isNoContent());

        assertThat(promocaoRepository.findById(promocao.getId())).isEmpty();
    }

    private record PromocaoRequest(Long programaId, String titulo, String descricao,
            LocalDate data_inicio, LocalDate data_fim, Boolean ativo) {
    }
}
