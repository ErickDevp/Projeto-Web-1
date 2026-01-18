package br.edu.ifs.academico.DTO;

import java.time.LocalDateTime;

public record NotificacaoResponseDTO(
        Long id,
        String titulo,
        String mensagem,
        String tipo,
        LocalDateTime dataCriacao,
        boolean lida) {
}
