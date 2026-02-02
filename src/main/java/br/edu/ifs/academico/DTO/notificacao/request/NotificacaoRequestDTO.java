package br.edu.ifs.academico.DTO.notificacao.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record NotificacaoRequestDTO(

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
        String titulo,

        @NotBlank(message = "A mensagem é obrigatória")
        @Size(max = 500, message = "A mensagem deve ter no máximo 500 caracteres")
        String mensagem,

        @NotBlank(message = "O tipo da notificação é obrigatório")
        String tipo,

        @NotNull(message = "Prazo é obrigatório")
        @Positive(message = "Prazo deve ser maior que zero")
        long prazoDia
) {
}
