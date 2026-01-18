package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.MovimentacaoPontos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovimentacaoPontosRepository extends JpaRepository<MovimentacaoPontos, Long> {
    List<MovimentacaoPontos> findByUsuarioId(Long id);

    Optional<MovimentacaoPontos> findByIdAndUsuarioEmail(Long id, String email);

    List<MovimentacaoPontos> findByUsuarioIdOrderByDataOcorrenciaDesc(Long userId);

    @Query("select m from MovimentacaoPontos m " +
            "left join fetch m.cartao c " +
            "left join fetch m.saldo s " +
            "left join fetch s.programa p " +
            "where m.usuario.id = :userId " +
            "order by m.dataOcorrencia desc")
    List<MovimentacaoPontos> findByUsuarioIdWithDetails(@Param("userId") Long userId);

}
