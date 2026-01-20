package br.edu.ifs.academico.mapper;

import br.edu.ifs.academico.DTO.comprovante.response.ComprovanteResponseDTO;
import br.edu.ifs.academico.entity.Comprovante;
import org.springframework.stereotype.Component;

@Component
public class ComprovanteMapper {

    public ComprovanteResponseDTO toResponseDTO(Comprovante comprovante) {

        return new ComprovanteResponseDTO(
                comprovante.getId(),
                comprovante.getCaminho(),
                comprovante.getTipo_arq(),
                comprovante.getTamanho_bytes()
        );
    }
}
