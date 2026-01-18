package br.edu.ifs.academico.DTO;

import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.TipoCartao;

import java.util.Set;

public record CartaoUsuarioResponseDTO(
        Long id,
        String nome,
        Bandeira bandeira,
        TipoCartao tipo,
        Double multiplicadorPontos,
        Double pontos,
        Set<Long> programaIds) {
}
