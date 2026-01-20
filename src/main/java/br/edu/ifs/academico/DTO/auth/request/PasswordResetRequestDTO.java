package br.edu.ifs.academico.DTO.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequestDTO(
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O formato do email é inválido")
        String email
) {
}
