package br.edu.ifs.academico.DTO.saldo.response;

import br.edu.ifs.academico.DTO.programa.response.ProgramaResponseDTO;

public record SaldoResponseDTO(
        Long id,
        Integer pontos,
        ProgramaResponseDTO programaId
) {
}
