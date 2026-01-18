package br.edu.ifs.academico.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentacaoResponseDTO(
        Long id,
        BigDecimal valor,
        Integer pontosCalculados,
        LocalDate dataOcorrencia,
        String status,
        Long cartaoId,
        String cartaoNome,
        Long programaId,
        String programaNome) {
}
