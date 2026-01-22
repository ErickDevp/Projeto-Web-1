package br.edu.ifs.academico.DTO.usuario.response;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
                Long id,
                String nome,
                String email,
                LocalDateTime criado_em,
                String caminhoFoto) {
}
