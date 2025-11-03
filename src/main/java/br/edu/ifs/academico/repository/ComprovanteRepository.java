package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.Comprovante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComprovanteRepository extends JpaRepository<Comprovante, Long> {
}
