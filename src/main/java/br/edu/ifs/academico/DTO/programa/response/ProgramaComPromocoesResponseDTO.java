package br.edu.ifs.academico.DTO.programa.response;

import br.edu.ifs.academico.DTO.promocao.response.PromocaoProgramaResponseDTO;

import java.util.List;

public record ProgramaComPromocoesResponseDTO (
        Long id,
        String nome,
        String descricao,
        List<PromocaoProgramaResponseDTO> promocoes
){
}
