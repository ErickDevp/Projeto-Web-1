package br.edu.ifs.academico.DTO;

import java.math.BigDecimal;

public record MovimentacaoPontosDTO(
                Long cartaoId,
                Long programaId, // id do programa
                BigDecimal valor) {
}
