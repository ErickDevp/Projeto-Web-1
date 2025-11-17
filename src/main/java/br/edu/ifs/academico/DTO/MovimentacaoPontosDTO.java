package br.edu.ifs.academico.DTO;

import java.math.BigDecimal;

public record MovimentacaoPontosDTO(
        Long usuarioId,
        BigDecimal valor,
        Integer pontos_calculados,
        String status
) {
}
