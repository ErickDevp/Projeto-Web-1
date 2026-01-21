package br.edu.ifs.academico.DTO.status.response;

import br.edu.ifs.academico.entity.enums.Status;

public record StatusResponseDTO(
        Status status,
        String motivo
) {
}
