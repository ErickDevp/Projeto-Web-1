package br.edu.ifs.academico.DTO;

public record ArquivoBytesDTO(
        byte[] bytes,
        String contentType) {
}