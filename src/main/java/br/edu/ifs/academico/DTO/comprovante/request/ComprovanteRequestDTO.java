package br.edu.ifs.academico.DTO.comprovante.request;

public record ComprovanteRequestDTO(
        Long movimentacaoId,
        String caminho,
        String tipo_arq,
        Long tamanho_bytes
) {
}
