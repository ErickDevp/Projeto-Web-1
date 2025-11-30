package br.edu.ifs.academico.DTO.relatorio;

import java.util.List;

public record RelatorioResponseDTO(
        List<PontosPorCartaoDTO> pontosPorCartao,
        List<HistoricoMovimentacaoDTO> historico,
        Double prazoMedio
) {}
