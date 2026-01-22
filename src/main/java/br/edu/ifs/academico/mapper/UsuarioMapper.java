package br.edu.ifs.academico.mapper;

import br.edu.ifs.academico.DTO.usuario.response.UsuarioResponseDTO;
import br.edu.ifs.academico.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getCriado_em(),
                usuario.getCaminhoFoto());
    }
}
