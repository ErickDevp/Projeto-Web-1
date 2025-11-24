package br.edu.ifs.academico.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentacaoPontosDTO(
        Long saldoId,
        Long cartaoId,
        BigDecimal valor,
        LocalDate data_ocorrencia
) {
}
