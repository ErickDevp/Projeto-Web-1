package br.edu.ifs.academico.DTO;

import java.time.LocalDate;

public record PromocaoDTO(
        String titulo,
        String descricao,
        LocalDate data_inicio,
        LocalDate data_fim,
        Boolean ativo
) {
}
