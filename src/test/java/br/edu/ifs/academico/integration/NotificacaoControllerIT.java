package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.Notificacao;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class NotificacaoControllerIT extends IntegrationTestSupport {

    @Test
    void shouldManageNotificationsFlow() throws Exception {
        Usuario admin = createUser("admin1@teste.com", Role.ADMIN, DEFAULT_PASSWORD);
        Usuario user = createUser("user11@teste.com", Role.USER, DEFAULT_PASSWORD);

        String adminToken = loginAndGetToken(admin.getEmail(), DEFAULT_PASSWORD);
        String userToken = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        var createPayload = toJson(new NotificacaoRequest(
                "Titulo",
                "Mensagem",
                "INFO"));

        mockMvc.perform(post("/notificacao/criar")
                .with(bearerToken(adminToken))
                .contentType("application/json")
                .content(createPayload))
                .andExpect(status().isOk());

        mockMvc.perform(get("/notificacao/publicas")
                .with(bearerToken(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/notificacao")
                .with(bearerToken(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        Notificacao notificacao = notificacaoRepository.findAll().get(0);

        var updatePayload = toJson(new NotificacaoRequest(
                "Titulo 2",
                "Mensagem 2",
                "INFO"));

        mockMvc.perform(put("/notificacao/{id}", notificacao.getId())
                .with(bearerToken(adminToken))
                .contentType("application/json")
                .content(updatePayload))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/notificacao/{id}/lida", notificacao.getId())
                .with(bearerToken(userToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/notificacao/{id}", notificacao.getId())
                .with(bearerToken(userToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/notificacao/{id}/all", notificacao.getId())
                .with(bearerToken(adminToken)))
                .andExpect(status().isNoContent());
    }

    private record NotificacaoRequest(String titulo, String mensagem, String tipo) {
    }
}
