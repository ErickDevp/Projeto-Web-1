package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.Notificacao;
import br.edu.ifs.academico.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuarioIsNull();

    List<Notificacao> findByUsuarioIsNullOrUsuario(Usuario usuario);
}
