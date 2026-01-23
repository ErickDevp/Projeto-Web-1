package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.comprovante.response.ArquivoBytesResponseDTO;
import br.edu.ifs.academico.DTO.usuario.request.UsuarioRequestDTO;
import br.edu.ifs.academico.DTO.usuario.response.UsuarioResponseDTO;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.mapper.UsuarioMapper;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@SuppressWarnings("null")
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${usuario.foto.storage.path:uploads/usuarios}")
    private String storagePath;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public UsuarioResponseDTO buscarUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        return usuarioMapper.toResponseDTO(usuario);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public UsuarioResponseDTO atualizarUsuario(UsuarioRequestDTO usuarioRequestDTO, String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (usuarioRequestDTO.nome() != null)
            usuario.setNome(usuarioRequestDTO.nome());
        if (usuarioRequestDTO.email() != null)
            usuario.setEmail(usuarioRequestDTO.email());
        if (usuarioRequestDTO.novaSenha() != null) {
            usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.novaSenha()));
        }

        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo vazio");
        }

        // 🔒 tamanho máximo 5MB
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Arquivo excede 5MB");
        }

        // 🖼️ tipos permitidos
        String contentType = file.getContentType();
        if (contentType == null || !isTipoPermitido(contentType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Formato inválido. Aceito: JPG, PNG, GIF");
        }

        try {
            Path base = Paths.get(storagePath).toAbsolutePath().normalize();
            Files.createDirectories(base);

            String ext = obterExtensao(file, contentType);
            String filename = UUID.randomUUID() + ext;
            Path target = base.resolve(filename);

            // sobrescreve se existir (mais seguro)
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            usuario.setCaminhoFoto(target.toString());
            usuarioRepository.save(usuario);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo", e);
        }
    }

    private boolean isTipoPermitido(String contentType) {
        return contentType.equals("image/jpeg")
                || contentType.equals("image/png")
                || contentType.equals("image/gif");
    }

    private String obterExtensao(MultipartFile file, String contentType) {
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            return original.substring(original.lastIndexOf("."));
        }

        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/gif" -> ".gif";
            default -> "";
        };
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ArquivoBytesResponseDTO lerFotoPerfil(String emailLogado) {

        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (usuario.getCaminhoFoto() == null || usuario.getCaminhoFoto().isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto não encontrada");
        }

        try {
            Path path = Paths.get(usuario.getCaminhoFoto());

            if (!Files.exists(path)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Arquivo não existe no servidor");
            }

            byte[] bytes = Files.readAllBytes(path);
            String contentType = Files.probeContentType(path);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return new ArquivoBytesResponseDTO(bytes, contentType);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo", e);
        }
    }
}
