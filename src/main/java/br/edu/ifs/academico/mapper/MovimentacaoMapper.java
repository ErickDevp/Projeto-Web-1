package br.edu.ifs.academico.mapper;

import br.edu.ifs.academico.DTO.movimentacao.response.MovimentacaoResponseDTO;
import br.edu.ifs.academico.DTO.status.response.StatusResponseDTO;
import br.edu.ifs.academico.DTO.comprovante.response.ComprovanteResponseDTO;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovimentacaoMapper {

    public MovimentacaoResponseDTO toResponseDTO(MovimentacaoPontos movi) {

        return new MovimentacaoResponseDTO(
                movi.getId(),
                movi.getValor(),
                movi.getPontos_calculados(),
                movi.getDataOcorrencia(),
                movi.getCartao().getId(),
                movi.getCartao().getNome(),
                movi.getSaldo().getPrograma().getId(),
                movi.getSaldo().getPrograma().getNome(),
                new StatusResponseDTO(
                        movi.getStatus().getStatus(),
                        movi.getStatus().getMotivo()),
                movi.getComprovantes() != null ? movi.getComprovantes().stream().map(c -> new ComprovanteResponseDTO(
                        c.getId(),
                        c.getCaminho(),
                        c.getTipo_arq(),
                        c.getTamanho_bytes())).toList() : List.of());
    }
}
