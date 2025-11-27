package br.edu.ifs.academico.repository;

import br.edu.ifs.academico.entity.PasswordResetToken;
import br.edu.ifs.academico.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUsuario(Usuario usuario);
}