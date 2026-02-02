package br.edu.ifs.academico.DTO.movimentacao.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentacaoRequestDTO(

                @Positive(message = "O id do cartão deve ser positivo") Long cartaoId,

                @NotNull(message = "O programa é obrigatório") @Positive(message = "O id do programa deve ser positivo") Long programaId,

                @Positive(message = "O id da promoção deve ser positivo") Long promocaoId,

                @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero") BigDecimal valor,

                @Positive(message = "A quantidade de pontos deve ser positiva") Integer quantidadePontos,

                @NotNull(message = "A data é obrigatória") @PastOrPresent(message = "A data não pode ser futura") LocalDate data) {
}
