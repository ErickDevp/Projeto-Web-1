package br.edu.ifs.academico.DTO.cartao.request;

import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

public record CartaoRequestDTO(

        @NotBlank(message = "O nome do cartão é obrigatório")
        String nome,

        @NotNull(message = "A bandeira do cartão é obrigatória")
        Bandeira bandeira,

        @NotNull(message = "O tipo do cartão é obrigatório")
        TipoCartao tipo,

        @NotNull(message = "O número do cartão é obrigatório")
        @Positive(message = "O número do cartão deve ser positivo")
        @Pattern(
                regexp = "\\d{16}",
                message = "Informe os 16 dígitos do cartão"
        )
        String numero,

        @NotNull(message = "A data de validade é obrigatória")
        @Future(message = "A data de validade deve ser futura")
        LocalDate dataValidade,

        @NotNull(message = "É obrigatório informar ao menos um programa")
        @Size(min = 1, message = "Deve existir pelo menos um programa vinculado")
        Set<Long> programaIds

) {
}