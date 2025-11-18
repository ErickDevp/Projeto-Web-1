package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaldoUsuarioProgramaRepository extends JpaRepository<SaldoUsuarioPrograma, Long> {
    List<SaldoUsuarioPrograma> findByUsuarioId(Long id);

}
