package br.edu.ifs.academico.DTO.relatorio;

public record PontosPorCartaoDTO(
        Long cartaoId,
        String nomeCartao,
        Long totalPontos
) {}

