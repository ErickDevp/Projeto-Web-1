package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.ArquivoBytesDTO;
import br.edu.ifs.academico.DTO.UsuarioDTO;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@SuppressWarnings("null")
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Value("${usuario.foto.storage.path:uploads/usuarios}")
    private String storagePath;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public UsuarioDTO buscarUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                null);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void atualizarUsuario(UsuarioDTO usuarioDTO, String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (usuarioDTO.nome() != null)
            usuario.setNome(usuarioDTO.nome());
        if (usuarioDTO.email() != null)
            usuario.setEmail(usuarioDTO.email());

        usuarioRepository.save(usuario);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void apagarUsuario(String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        usuarioRepository.delete(usuario);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void salvarFotoPerfil(MultipartFile file, String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Arquivo vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/png") || contentType.equals("image/jpeg"))) {
            throw new RuntimeException("Tipo de arquivo não suportado. Aceito: png, jpg");
        }

        try {
            Path base = Paths.get(storagePath).toAbsolutePath().normalize();
            Files.createDirectories(base);

            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            } else if (contentType.equals("image/png"))
                ext = ".png";
            else if (contentType.equals("image/jpeg"))
                ext = ".jpg";

            String filename = UUID.randomUUID().toString() + ext;
            Path target = base.resolve(filename);

            Files.copy(file.getInputStream(), target);

            usuario.setCaminhoFoto(target.toString());
            usuarioRepository.save(usuario);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage(), e);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ArquivoBytesDTO lerFotoPerfil(String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (usuario.getCaminhoFoto() == null || usuario.getCaminhoFoto().isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto não encontrada");
        }

        try {
            Path path = Paths.get(usuario.getCaminhoFoto());
            byte[] bytes = Files.readAllBytes(path);
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return new ArquivoBytesDTO(bytes, contentType);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo: " + e.getMessage(), e);
        }
    }
}
