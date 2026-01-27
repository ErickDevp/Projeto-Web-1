package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.notificacao.request.NotificacaoRequestDTO;
import br.edu.ifs.academico.DTO.notificacao.response.NotificacaoResponseDTO;
import br.edu.ifs.academico.entity.Notificacao;
import br.edu.ifs.academico.entity.NotificacaoUsuario;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.mapper.NotificacaoMapper;
import br.edu.ifs.academico.repository.NotificacaoRepository;
import br.edu.ifs.academico.repository.NotificacaoUsuarioRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

@Service
@SuppressWarnings("null")
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    private final NotificacaoRepository notificacaoRepository;
    private final NotificacaoUsuarioRepository notificacaoUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoMapper notificacaoMapper;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              NotificacaoUsuarioRepository notificacaoUsuarioRepository,
                              UsuarioRepository usuarioRepository, NotificacaoMapper notificacaoMapper) {
        this.notificacaoRepository = notificacaoRepository;
        this.notificacaoUsuarioRepository = notificacaoUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoMapper = notificacaoMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<NotificacaoResponseDTO> buscarNotificacoes(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioIsNullOrUsuario(usuario);

        List<NotificacaoUsuario> usuarioNotificacoes = notificacaoUsuarioRepository
                .findByUsuarioAndNotificacaoIn(usuario, notificacoes);

        Map<Long, Boolean> lidas = usuarioNotificacoes.stream()
                .collect(Collectors.toMap(
                        nu -> nu.getNotificacao().getId(),
                        NotificacaoUsuario::isLida,
                        (a, b) -> b));

        Set<Long> ocultas = usuarioNotificacoes.stream()
                .filter(NotificacaoUsuario::isOculta)
                .map(nu -> nu.getNotificacao().getId())
                .collect(Collectors.toSet());

        return notificacoes.stream()
                .filter(notificacao -> !ocultas.contains(notificacao.getId()))
                .map(notificacaoMapper::toResponseDTO)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<NotificacaoResponseDTO> buscarNotificacoesPublicas() {

        return notificacaoRepository.findByUsuarioIsNull()
                .stream()
                .map(notificacaoMapper::toResponseDTO)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public NotificacaoResponseDTO criarNotificacao(NotificacaoRequestDTO notificacaoRequestDTO) {
        Notificacao entity = Notificacao.builder()
                .titulo(notificacaoRequestDTO.titulo())
                .mensagem(notificacaoRequestDTO.mensagem())
                .tipo(notificacaoRequestDTO.tipo())
                .dataExpiracao(LocalDateTime.now().plusDays(notificacaoRequestDTO.prazoDia()))
                .build();

        return notificacaoMapper.toResponseDTO(notificacaoRepository.save(entity));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public NotificacaoResponseDTO atualizarNotificacao(NotificacaoRequestDTO notificacaoRequestDTO, Long id) {
        var notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("notificação não encontrado"));

        if (notificacaoRequestDTO.titulo() != null) {
            notificacao.setTitulo(notificacaoRequestDTO.titulo());
        }
        if (notificacaoRequestDTO.mensagem() != null) {
            notificacao.setMensagem(notificacaoRequestDTO.mensagem());
        }
        if (notificacaoRequestDTO.tipo() != null) {
            notificacao.setTipo(notificacaoRequestDTO.tipo());
        }

        return notificacaoMapper.toResponseDTO(notificacaoRepository.save(notificacao));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void marcarComoLida(Long notificacaoId, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada"));

        if (notificacao.getUsuario() != null
                && !notificacao.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Notificação não pertence ao usuário");
        }

        NotificacaoUsuario notificacaoUsuario = notificacaoUsuarioRepository
                .findByUsuarioAndNotificacao(usuario, notificacao)
                .orElseGet(() -> NotificacaoUsuario.builder()
                        .usuario(usuario)
                        .notificacao(notificacao)
                        .lida(false)
                        .oculta(false)
                        .build());

        notificacaoUsuario.setLida(true);
        notificacaoUsuario.setOculta(false);
        notificacaoUsuarioRepository.save(notificacaoUsuario);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public void dismissForUser(Long notificacaoId, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada"));

        if (notificacao.getUsuario() != null
                && !notificacao.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Notificação não pertence ao usuário");
        }

        log.info("Dismiss request: notificacaoId={}, usuarioId={}", notificacao.getId(), usuario.getId());

        NotificacaoUsuario notificacaoUsuario = notificacaoUsuarioRepository
                .findByNotificacao_IdAndUsuario_Id(notificacao.getId(), usuario.getId())
                .orElseGet(() -> NotificacaoUsuario.builder()
                        .usuario(usuario)
                        .notificacao(notificacao)
                        .lida(true)
                        .oculta(true)
                        .build());

        log.info("Dismiss record exists: {}", notificacaoUsuario.getId() != null);

        notificacaoUsuario.setOculta(true);
        notificacaoUsuarioRepository.save(notificacaoUsuario);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteForAll(Long notificacaoId) {
        if (!notificacaoRepository.existsById(notificacaoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada");
        }
        notificacaoUsuarioRepository.deleteByNotificacao_Id(notificacaoId);
        notificacaoRepository.deleteById(notificacaoId);
    }
}
