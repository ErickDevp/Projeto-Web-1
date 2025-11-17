package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.CartaoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartaoUsuarioRepository extends JpaRepository<CartaoUsuario, Long> {
    List<CartaoUsuario> findByUsuarioId(Long id);
}
