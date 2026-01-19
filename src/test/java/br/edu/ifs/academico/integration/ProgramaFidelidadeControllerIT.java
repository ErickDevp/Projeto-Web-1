package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class ProgramaFidelidadeControllerIT extends IntegrationTestSupport {

    @Test
    void shouldCreateListUpdateAndDeleteProgram() throws Exception {
        Usuario admin = createUser("admin2@teste.com", Role.ADMIN, DEFAULT_PASSWORD);
        String adminToken = loginAndGetToken(admin.getEmail(), DEFAULT_PASSWORD);

        var payload = toJson(new ProgramaRequest("Programa X", "Descricao"));

        mockMvc.perform(post("/programa/criar")
                .with(bearerToken(adminToken))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk());

        mockMvc.perform(get("/programa")
                .with(bearerToken(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        ProgramaFidelidade programa = programaFidelidadeRepository.findAll().get(0);

        var updatePayload = toJson(new ProgramaRequest("Programa Y", "Descricao 2"));

        mockMvc.perform(put("/programa/{id}", programa.getId())
                .with(bearerToken(adminToken))
                .contentType("application/json")
                .content(updatePayload))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/programa/{id}", programa.getId())
                .with(bearerToken(adminToken)))
                .andExpect(status().isNoContent());

        assertThat(programaFidelidadeRepository.findById(programa.getId())).isEmpty();
    }

    private record ProgramaRequest(String nome, String descricao) {
    }
}
