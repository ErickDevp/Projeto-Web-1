package br.edu.ifs.academico.DTO.programa.response;

import br.edu.ifs.academico.entity.enums.CategoriaPrograma;

public record ProgramaResponseDTO(
                Long id,
                String nome,
                String descricao,
                CategoriaPrograma categoria) {
}
