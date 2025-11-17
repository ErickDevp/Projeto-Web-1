package br.edu.ifs.academico.DTO;

import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.TipoCartao;

public record CartaoUsuarioDTO(
    Long usuarioId,
    String nome,
    Bandeira bandeira,
    TipoCartao tipo,
    Double pontos
) {
}
