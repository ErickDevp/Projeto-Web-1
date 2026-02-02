package br.edu.ifs.academico.DTO.programa.response;

import br.edu.ifs.academico.DTO.promocao.response.PromocaoProgramaResponseDTO;
import br.edu.ifs.academico.entity.enums.CategoriaPrograma;

import java.util.List;

public record ProgramaComPromocoesResponseDTO(
                Long id,
                String nome,
                String descricao,
                CategoriaPrograma categoria,
                List<PromocaoProgramaResponseDTO> promocoes) {
}
