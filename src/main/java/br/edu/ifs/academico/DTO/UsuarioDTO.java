package br.edu.ifs.academico.DTO;

import java.time.LocalDateTime;

public record UsuarioDTO(
        String nome,
        String email,
        String senha
) {
}
