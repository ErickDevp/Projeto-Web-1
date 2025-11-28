package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.StatusMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusMovimentacaoRepository extends JpaRepository<StatusMovimentacao, Long> {
    Optional<StatusMovimentacao> findByMovimentacaoId(Long id);
}
