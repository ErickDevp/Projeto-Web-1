package br.edu.ifs.academico.DTO;

public record HistoricoStatusMovimentacaoDTO(
        String status_antigo,
        String status_novo,
        String motivo
) {
}
