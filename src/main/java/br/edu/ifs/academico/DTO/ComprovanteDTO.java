package br.edu.ifs.academico.DTO;

public record ComprovanteDTO(
        Long movimentacaoId,
        String caminho,
        String tipo_arq,
        Long tamanho_bytes
) {
}
