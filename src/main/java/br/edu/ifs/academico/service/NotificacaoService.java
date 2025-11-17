package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.NotificacaoDTO;
import br.edu.ifs.academico.entity.Notificacao;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.repository.NotificacaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public Optional<Notificacao> buscarNotificacaoPorId(Long id) {
        return notificacaoRepository.findById(id);
    }

    public Long salvarNotificacao(NotificacaoDTO notificacaoDTO, Usuario usuarioLogado) {
        if (usuarioLogado.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente administradores podem criar notificações.");
        }

        Notificacao entity = Notificacao.builder()
                .titulo(notificacaoDTO.titulo())
                .mensagem(notificacaoDTO.mensagem())
                .tipo(notificacaoDTO.tipo())
                .build();
        
        return notificacaoRepository.save(entity).getId();
    }

    public void atualizarNotificacao(Long id, NotificacaoDTO notificacaoDTO) {
        var notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("notificação não encontrado"));

        if(notificacaoDTO.titulo() != null) { notificacao.setTitulo(notificacaoDTO.titulo()); }
        if(notificacaoDTO.mensagem() != null) { notificacao.setMensagem(notificacaoDTO.mensagem()); }
        if(notificacaoDTO.tipo() != null) { notificacao.setTipo(notificacaoDTO.titulo()); }

        notificacaoRepository.save(notificacao);
    }

    public void apagarNotificacao(Long id) {
        if(!notificacaoRepository.existsById(id)) {
            throw new RuntimeException("notificação não encontrado");
        }
        notificacaoRepository.deleteById(id);
    }
    

}
