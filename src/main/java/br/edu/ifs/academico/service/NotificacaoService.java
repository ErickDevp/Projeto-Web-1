package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.NotificacaoDTO;
import br.edu.ifs.academico.entity.Notificacao;
import br.edu.ifs.academico.repository.NotificacaoRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<Notificacao> buscarNotificacoes() {
        return notificacaoRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public Long criarNotificacao(NotificacaoDTO notificacaoDTO) {
        Notificacao entity = Notificacao.builder()
                .titulo(notificacaoDTO.titulo())
                .mensagem(notificacaoDTO.mensagem())
                .tipo(notificacaoDTO.tipo())
                .build();
        
        return notificacaoRepository.save(entity).getId();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void atualizarNotificacao( NotificacaoDTO notificacaoDTO, Long id) {
        var notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("notificação não encontrado"));

        if(notificacaoDTO.titulo() != null) { notificacao.setTitulo(notificacaoDTO.titulo()); }
        if(notificacaoDTO.mensagem() != null) { notificacao.setMensagem(notificacaoDTO.mensagem()); }
        if(notificacaoDTO.tipo() != null) { notificacao.setTipo(notificacaoDTO.titulo()); }

        notificacaoRepository.save(notificacao);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void apagarNotificacao(Long id) {
        if(!notificacaoRepository.existsById(id)) {
            throw new RuntimeException("notificação não encontrado");
        }
        notificacaoRepository.deleteById(id);
    }
}
