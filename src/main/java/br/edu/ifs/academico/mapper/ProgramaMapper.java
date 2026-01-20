package br.edu.ifs.academico.mapper;

import br.edu.ifs.academico.DTO.programa.response.ProgramaResponseDTO;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import org.springframework.stereotype.Component;

@Component
public class ProgramaMapper {

    public ProgramaResponseDTO toResponseDTO(ProgramaFidelidade programa) {

        return new ProgramaResponseDTO(
                programa.getId(),
                programa.getNome(),
                programa.getDescricao()
        );
    }
}
