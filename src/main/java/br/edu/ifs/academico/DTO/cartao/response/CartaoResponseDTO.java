package br.edu.ifs.academico.DTO.cartao.response;

import br.edu.ifs.academico.DTO.programa.response.ProgramaResponseDTO;
import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import br.edu.ifs.academico.entity.enums.Valido;

import java.time.LocalDate;
import java.util.List;

public record CartaoResponseDTO (
        Long id,
        String nome,
        Bandeira bandeira,
        TipoCartao tipo,
        String numero,
        LocalDate dataValidade,
        Valido valido,
        List<ProgramaResponseDTO> programas
) {
    public CartaoResponseDTO {
        if (programas == null) {
            programas = List.of();
        }
    }
}