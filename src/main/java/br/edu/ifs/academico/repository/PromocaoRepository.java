package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromocaoRepository extends JpaRepository<Promocao, Long>{
    List<Promocao> findByProgramaIdAndDataFimGreaterThanEqual(
            Long programaId,
            LocalDate data
    );
}
