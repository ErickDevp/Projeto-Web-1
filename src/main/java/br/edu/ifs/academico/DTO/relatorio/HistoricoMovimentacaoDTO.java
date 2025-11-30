package br.edu.ifs.academico.DTO.relatorio;

import java.time.LocalDate;

public record HistoricoMovimentacaoDTO(
        Long movimentacaoId,
        String programa,
        Integer pontosCalculados,
        java.time.LocalDate data,
        String status
) {}

