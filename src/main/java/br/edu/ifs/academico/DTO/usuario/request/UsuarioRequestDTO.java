package br.edu.ifs.academico.DTO.usuario.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(

        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Email(message = "O formato do email é inválido")
        String email,

        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String novaSenha

) {
}
