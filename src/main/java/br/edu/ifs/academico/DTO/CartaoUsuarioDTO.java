package br.edu.ifs.academico.DTO;

import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.TipoCartao;

import java.util.Set;

public record CartaoUsuarioDTO(
        String nome,
        Bandeira bandeira,
        TipoCartao tipo,
        Double pontos,
        Set<Long> programaIds // IDs dos programas associados
) {
}
