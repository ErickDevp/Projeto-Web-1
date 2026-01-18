package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.Notificacao;
import br.edu.ifs.academico.entity.NotificacaoUsuario;
import br.edu.ifs.academico.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificacaoUsuarioRepository extends JpaRepository<NotificacaoUsuario, Long> {
    Optional<NotificacaoUsuario> findByUsuarioAndNotificacao(Usuario usuario, Notificacao notificacao);

    List<NotificacaoUsuario> findByUsuarioAndNotificacaoIn(Usuario usuario, List<Notificacao> notificacoes);

    void deleteByUsuarioAndNotificacao(Usuario usuario, Notificacao notificacao);

    void deleteByNotificacao_Id(Long notificacaoId);

    void deleteByNotificacao_IdAndUsuario_Id(Long notificacaoId, Long usuarioId);

    Optional<NotificacaoUsuario> findByNotificacao_IdAndUsuario_Id(Long notificacaoId, Long usuarioId);
}
