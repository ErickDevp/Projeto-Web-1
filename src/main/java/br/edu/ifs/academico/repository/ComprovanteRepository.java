package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.Comprovante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComprovanteRepository extends JpaRepository<Comprovante, Long> {
    List<Comprovante> findByMovimentacaoId(Long id);

}
