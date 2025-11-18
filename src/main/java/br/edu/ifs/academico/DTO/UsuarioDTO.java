package br.edu.ifs.academico.DTO;

public record UsuarioDTO(
        Long id,
        String nome,
        String email,
        String senha
) {
}
