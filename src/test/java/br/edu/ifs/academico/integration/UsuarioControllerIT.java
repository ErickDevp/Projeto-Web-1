package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class UsuarioControllerIT extends IntegrationTestSupport {

    @Test
    void shouldGetAndUpdateUserProfile() throws Exception {
        Usuario user = createUser("user4@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        mockMvc.perform(get("/usuario/me").with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        var payload = toJson(new UpdateUserRequest("Novo Nome", "novo@teste.com"));

        mockMvc.perform(put("/usuario/me")
                .with(bearerToken(token))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk());

        var updated = usuarioRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getNome()).isEqualTo("Novo Nome");
        assertThat(updated.getEmail()).isEqualTo("novo@teste.com");
    }

    @Test
    void shouldUploadAndReadPhoto() throws Exception {
        Usuario user = createUser("user5@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "foto.png",
                "image/png",
                "conteudo".getBytes());

        mockMvc.perform(multipart("/usuario/foto")
                .file(file)
                .with(bearerToken(token)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/usuario/foto").with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"));
    }

    @Test
    void shouldDeleteUserAccount() throws Exception {
        Usuario user = createUser("user6@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        mockMvc.perform(delete("/usuario/me").with(bearerToken(token)))
                .andExpect(status().isNoContent());

        assertThat(usuarioRepository.findByEmail(user.getEmail())).isEmpty();
    }

    private record UpdateUserRequest(String nome, String email) {
    }
}
