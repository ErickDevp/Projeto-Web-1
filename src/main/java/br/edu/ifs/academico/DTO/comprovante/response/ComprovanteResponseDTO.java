package br.edu.ifs.academico.DTO.comprovante.response;

public record ComprovanteResponseDTO(
        Long id,
        String caminho,
        String tipo_arq,
        Long tamanho_bytes
) {
}
