package br.edu.ifs.academico.DTO.promocao.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.DecimalMin;

import java.time.LocalDate;

public record PromocaoRequestDTO(

        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        @NotNull(message = "A data de início é obrigatória")
        @FutureOrPresent(message = "A data de início não pode ser no passado")
        LocalDate dataInicio,

        @NotNull(message = "A data de fim é obrigatória")
        @FutureOrPresent(message = "A data de fim não pode ser no passado")
        LocalDate dataFim,

        @NotNull(message = "O programa é obrigatório")
        @Positive(message = "O id do programa deve ser positivo")
        Long programaId,

        @NotNull(message = "Os pontos por real são obrigatórios")
        @DecimalMin(value = "0.1", message = "Os pontos por real devem ser maiores que zero")
        Double pontosPorReal
) {
}
