package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.Comprovante;
import br.edu.ifs.academico.entity.HistoricoStatusMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoricoStatusMovimentacaoRepository extends JpaRepository<HistoricoStatusMovimentacao, Long> {
    Optional<HistoricoStatusMovimentacao> findByMovimentacaoId(Long id);
}
