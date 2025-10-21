package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<Cartao, Long> {
}
