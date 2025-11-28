package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.ProgramaFidelidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramaFidelidadeRepository extends JpaRepository<ProgramaFidelidade, Long> {
	Optional<ProgramaFidelidade> findByNome(String nome);
}
