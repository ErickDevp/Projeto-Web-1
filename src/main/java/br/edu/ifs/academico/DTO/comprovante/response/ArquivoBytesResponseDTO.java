package br.edu.ifs.academico.DTO.comprovante.response;

public record ArquivoBytesResponseDTO(
        byte[] bytes,
        String contentType) {
}