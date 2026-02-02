package br.edu.ifs.academico.service;

import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.mapper.UsuarioMapper;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(Objects.requireNonNull(service), "storagePath", "uploads/tests");
    }

    @Test
    @DisplayName("Deve lançar erro se o arquivo for maior que 5MB")
    void salvarFotoPerfil_DeveLancarErro_QuandoArquivoGrande() {
        // Arrange
        String email = "test@user.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        // Criar arquivo > 5MB. 5MB = 5 * 1024 * 1024 bytes = 5242880 bytes.
        // Vamos criar um array de bytes um pouco maior.
        byte[] content = new byte[5242881];
        MockMultipartFile file = new MockMultipartFile("file", "large.png", "image/png", content);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.salvarFotoPerfil(file, email));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertEquals("Arquivo excede 5MB", reason);
    }

    @Test
    @DisplayName("Deve lançar erro se a extensão ou tipo do arquivo for inválido")
    void salvarFotoPerfil_DeveLancarErro_QuandoExtensaoInvalida() {
        // Arrange
        String email = "test@user.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        MockMultipartFile file = new MockMultipartFile("file", "malware.exe", "application/x-msdownload",
                "fake content".getBytes());

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.salvarFotoPerfil(file, email));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertTrue(reason.contains("Formato inválido"));
    }

    @Test
    @DisplayName("Deve processar e salvar arquivo com extensão correta")
    void salvarFotoPerfil_DeveSalvarComExtensaoCorreta() {
        // Arrange
        String email = "test@user.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        MockMultipartFile file = new MockMultipartFile("file", "foto.png", "image/png", "img-content".getBytes());

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        // Act
        service.salvarFotoPerfil(file, email);

        // Assert
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario savedUser = captor.getValue();
        assertNotNull(savedUser);
        assertNotNull(savedUser.getCaminhoFoto());
        assertTrue(savedUser.getCaminhoFoto().endsWith(".png"));
    }
}
