package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.Comprovante;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovimentacaoPontosRepository extends JpaRepository<MovimentacaoPontos, Long > {
    List<MovimentacaoPontos> findByUsuarioId(Long id);
    Optional<MovimentacaoPontos> findByIdAndUsuarioEmail(Long id, String email);
}
