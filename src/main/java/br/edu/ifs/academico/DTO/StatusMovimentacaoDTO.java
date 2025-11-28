package br.edu.ifs.academico.DTO;

import br.edu.ifs.academico.entity.enums.StatusMovimentacao;

public record StatusMovimentacaoDTO(
        Long movimentacaoId,
        StatusMovimentacao status,
        String motivo
) {

}
