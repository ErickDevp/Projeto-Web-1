package br.edu.ifs.academico.config;

import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;

@Configuration
public class InicializacaoConfig {

    @Bean
    CommandLineRunner init(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {

            // Verifica se já existe um admin no banco
            if (!repo.existsByEmail("admin@milhas.com")) {

                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail("admin@milhas.com");
                admin.setSenha(encoder.encode("123456")); // senha forte depois
                admin.setRole(Role.ADMIN);

                repo.save(admin);

                System.out.println(">>> ADMIN criado automaticamente!");
            } else {
                System.out.println(">>> ADMIN já existe, não será recriado.");
            }
        };
    }
}
