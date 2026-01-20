package br.edu.ifs.academico.mapper;

import br.edu.ifs.academico.DTO.programa.response.ProgramaResponseDTO;
import br.edu.ifs.academico.DTO.promocao.response.PromocaoResponseDTO;
import br.edu.ifs.academico.entity.Promocao;
import org.springframework.stereotype.Component;

@Component
public class PromocaoMapper {

    public PromocaoResponseDTO toResponseDTO(Promocao promocao) {

        return new PromocaoResponseDTO(
                promocao.getId(),
                promocao.getTitulo(),
                promocao.getDescricao(),
                promocao.getPontosPorReal(),
                promocao.getDataInicio(),
                promocao.getDataFim(),
                promocao.getValido(),

                new ProgramaResponseDTO(
                        promocao.getPrograma().getId(),
                        promocao.getPrograma().getNome(),
                        promocao.getPrograma().getDescricao()
                )
        );
    }
}
