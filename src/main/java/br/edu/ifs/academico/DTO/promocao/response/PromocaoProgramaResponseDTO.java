package br.edu.ifs.academico.DTO.promocao.response;

import br.edu.ifs.academico.entity.enums.Valido;

import java.time.LocalDate;

public record PromocaoProgramaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        Double pontosPorReal,
        LocalDate dataInicio,
        LocalDate dataFim,
        Valido ativo
) {
}
