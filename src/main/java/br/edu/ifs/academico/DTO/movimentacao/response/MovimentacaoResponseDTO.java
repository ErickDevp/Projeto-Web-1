package br.edu.ifs.academico.DTO.movimentacao.response;

import br.edu.ifs.academico.DTO.status.response.StatusResponseDTO;
import br.edu.ifs.academico.DTO.comprovante.response.ComprovanteResponseDTO;

import java.util.List;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentacaoResponseDTO(
                Long id,
                BigDecimal valor,
                Integer pontosCalculados,
                LocalDate dataOcorrencia,

                Long cartaoId,
                String cartaoNome,

                Long programaId,
                String programaNome,

                StatusResponseDTO status,

                List<ComprovanteResponseDTO> comprovantes) {
}
