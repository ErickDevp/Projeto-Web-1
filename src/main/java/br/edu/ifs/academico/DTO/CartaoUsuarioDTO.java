package br.edu.ifs.academico.DTO;

import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record CartaoUsuarioDTO(
                String nome,
                Bandeira bandeira,
                TipoCartao tipo,
                @JsonProperty("multiplicadorPontos") @JsonAlias("pontos") Double multiplicadorPontos,
                Set<Long> programaIds // IDs dos programas associados
) {
}
