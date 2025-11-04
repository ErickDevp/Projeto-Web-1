package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.MovimentacaoPontos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimentacaoPontosRepository extends JpaRepository<MovimentacaoPontos, Long > {
}
