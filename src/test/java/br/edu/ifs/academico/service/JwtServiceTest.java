package br.edu.ifs.academico.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService service = new JwtService();

    @Test
    @DisplayName("Deve gerar token contendo o username correto")
    void generateToken_DeveConterUsername() {
        // Arrange
        UserDetails userDetails = new User("user@test.com", "pass", Collections.emptyList());

        // Act
        String token = service.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        String username = service.extractUsername(token);
        assertEquals("user@test.com", username);
    }

    @Test
    @DisplayName("Deve validar a expiração do token (token recém criado deve ser válido)")
    void isTokenValid_DeveValidarExpiracao() {
         // Arrange
        UserDetails userDetails = new User("user@test.com", "pass", Collections.emptyList());
        String token = service.generateToken(userDetails);

        // Act
        boolean isValid = service.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }
}
