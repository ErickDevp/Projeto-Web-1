package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.auth.request.PasswordChangeRequestDTO;
import br.edu.ifs.academico.entity.PasswordResetToken;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.repository.PasswordResetTokenRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock // Needed for constructor but not used directly in chosen tests
    private JwtService jwtService;
    @Mock // Needed for constructor
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService service;

    @Test
    @DisplayName("Deve lançar erro ao tentar redefinir senha com token expirado")
    void redefinirSenha_DeveLancarErro_QuandoTokenExpirado() {
        // Arrange
        String tokenString = "expired-token";
        PasswordChangeRequestDTO dto = new PasswordChangeRequestDTO(tokenString, "newpass123");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(tokenString);
        resetToken.setExpiryDate(LocalDateTime.now().minusMinutes(1)); // Expirado

        when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.of(resetToken));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.redefinirSenha(dto));
        String reason = exception.getReason();
        assertNotNull(reason);
        assertTrue(reason.contains("Token expirado"));
    }

    @Test
    @DisplayName("Deve criptografar a senha antes de salvar no banco")
    void redefinirSenha_DeveCriptografarSenha() {
        // Arrange
        String tokenString = "valid-token";
        String novaSenha = "newpass123";
        PasswordChangeRequestDTO dto = new PasswordChangeRequestDTO(tokenString, novaSenha);

        Usuario usuario = new Usuario();
        usuario.setSenha("oldpass");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(tokenString);
        resetToken.setUsuario(usuario);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // Valido

        when(tokenRepository.findByToken(tokenString)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(novaSenha)).thenReturn("encoded-new-pass");

        // Act
        service.redefinirSenha(dto);

        // Assert
        verify(passwordEncoder).encode(novaSenha);
        verify(usuarioRepository)
                .save(argThat((Usuario user) -> user != null && user.getSenha().equals("encoded-new-pass")));
    }
}
