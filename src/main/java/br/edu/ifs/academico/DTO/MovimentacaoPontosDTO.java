package br.edu.ifs.academico.DTO;

import java.math.BigDecimal;

public record MovimentacaoPontosDTO(
        BigDecimal valor,
        Integer ppontos_calculados,
        String status
) {
}
