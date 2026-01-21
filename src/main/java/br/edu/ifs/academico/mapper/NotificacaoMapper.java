package br.edu.ifs.academico.mapper;

import br.edu.ifs.academico.DTO.notificacao.response.NotificacaoResponseDTO;
import br.edu.ifs.academico.entity.Notificacao;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoMapper {

    public NotificacaoResponseDTO toResponseDTO(Notificacao notificacao) {

        return new NotificacaoResponseDTO(
                notificacao.getId(),
                notificacao.getTitulo(),
                notificacao.getMensagem(),
                notificacao.getTipo(),
                notificacao.getDataCriacao(),
                notificacao.isLida()
        );
    }
}
