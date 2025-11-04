package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaldoUsuarioProgramaRepository extends JpaRepository<SaldoUsuarioPrograma, Long> {
}
