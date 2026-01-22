package br.edu.ifs.academico.DTO.usuario.response;

import br.edu.ifs.academico.entity.enums.Role;
import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Role role,
        LocalDateTime criado_em,
        String caminhoFoto) {
}
