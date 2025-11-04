package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.UsuarioDTO;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioDTO buscarUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                null
        );
    }

    public void atualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        var usuarioEntity = usuarioRepository.findById(id);

        if(usuarioEntity.isPresent()) {
            var usuario = usuarioEntity.get();

            if(usuarioDTO.nome() != null) {
                usuario.setNome(usuarioDTO.nome());
            }

            if(usuarioDTO.email() != null) {
                usuario.setEmail(usuarioDTO.email());
            }

            /* Ainda nao sei se devo coloca-lo aqui
            if (usuarioDTO.senha() != null && !usuarioDTO.senha().isBlank()) {
                usuario.setSenha(passwordEncoder.encode(usuarioDTO.senha()));
            }
            */

            usuarioRepository.save(usuario);
        }
    }

    public void apagarUsuario(Long id) {
        var usuarioExiste = usuarioRepository.existsById(id);

        if(usuarioExiste) {
            usuarioRepository.deleteById(id);
        }
    }

}
