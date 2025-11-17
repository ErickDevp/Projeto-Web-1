package br.edu.ifs.academico.DTO;

import br.edu.ifs.academico.entity.enums.Role;

public record UsuarioDTO(
        Long id,
        String nome,
        String email,
        String senha
) {
}
