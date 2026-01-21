package br.edu.ifs.academico.mapper;

import br.edu.ifs.academico.DTO.programa.response.ProgramaResponseDTO;
import br.edu.ifs.academico.DTO.saldo.response.SaldoResponseDTO;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import org.springframework.stereotype.Component;

@Component
public class SaldoMapper {

    public SaldoResponseDTO toResponseDTO(SaldoUsuarioPrograma saldo) {

        return new SaldoResponseDTO(
                saldo.getId(),
                saldo.getPontos(),
                new ProgramaResponseDTO(
                        saldo.getPrograma().getId(),
                        saldo.getPrograma().getNome(),
                        saldo.getPrograma().getDescricao()
                )
        );
    }
}
