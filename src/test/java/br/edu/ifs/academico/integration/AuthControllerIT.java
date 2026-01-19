package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.enums.Role;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIT extends IntegrationTestSupport {

    @Test
    void registerShouldReturnToken() throws Exception {
        var payload = toJson(new RegisterRequest(
                "Usuario Teste",
                "user1@teste.com",
                DEFAULT_PASSWORD));

        var result = mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.get("token").asText()).isNotBlank();
    }

    @Test
    void loginShouldReturnToken() throws Exception {
        createUser("user2@teste.com", Role.USER, DEFAULT_PASSWORD);

        var payload = toJson(new LoginRequest("user2@teste.com", DEFAULT_PASSWORD));

        var result = mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.get("token").asText()).isNotBlank();
    }

    @Test
    void forgotAndResetPasswordShouldWork() throws Exception {
        createUser("user3@teste.com", Role.USER, DEFAULT_PASSWORD);

        var forgotPayload = toJson(new EmailRequest("user3@teste.com"));

        var forgotResult = mockMvc.perform(post("/auth/forgot-password")
                .contentType("application/json")
                .content(forgotPayload))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode forgotJson = objectMapper.readTree(forgotResult.getResponse().getContentAsString());
        String resetToken = forgotJson.get("reset_token").asText();
        assertThat(resetToken).isNotBlank();

        var resetPayload = toJson(new ResetRequest(resetToken, "NovaSenha123!"));

        mockMvc.perform(post("/auth/reset-password")
                .contentType("application/json")
                .content(resetPayload))
                .andExpect(status().isOk());

        String token = loginAndGetToken("user3@teste.com", "NovaSenha123!");
        assertThat(token).isNotBlank();
    }

    private record RegisterRequest(String nome, String email, String senha) {
    }

    private record EmailRequest(String email) {
    }

    private record ResetRequest(String token, String novaSenha) {
    }
}
