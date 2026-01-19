package br.edu.ifs.academico.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentacaoPontosDTO(
                Long cartaoId,
                Long programaId, // id do programa
                BigDecimal valor,
                LocalDate data) {
}
