package br.edu.ifs.academico.mapper;

import br.edu.ifs.academico.DTO.programa.response.ProgramaComPromocoesResponseDTO;
import br.edu.ifs.academico.DTO.programa.response.ProgramaResponseDTO;
import br.edu.ifs.academico.DTO.promocao.response.PromocaoProgramaResponseDTO;
import br.edu.ifs.academico.DTO.promocao.response.PromocaoResponseDTO;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgramaMapper {

    public ProgramaResponseDTO toResponseDTO(ProgramaFidelidade programa) {

        return new ProgramaResponseDTO(
                programa.getId(),
                programa.getNome(),
                programa.getDescricao()
        );
    }

    public ProgramaComPromocoesResponseDTO toProgramaPromocaoResponseDTO(ProgramaFidelidade programa) {

        List<PromocaoProgramaResponseDTO> promocoes =
                programa.getPromocoes() == null
                        ? List.of()
                        : programa.getPromocoes().stream()
                        .map(pc -> new PromocaoProgramaResponseDTO(
                                pc.getId(),
                                pc.getTitulo(),
                                pc.getDescricao(),
                                pc.getPontosPorReal(),
                                pc.getDataInicio(),
                                pc.getDataFim(),
                                pc.getValido()
                        ))
                        .toList();

        return new ProgramaComPromocoesResponseDTO (
            programa.getId(),
            programa.getNome(),
            programa.getDescricao(),
            promocoes
        );
    }
}
