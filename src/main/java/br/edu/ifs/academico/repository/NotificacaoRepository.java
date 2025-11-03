package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
}
