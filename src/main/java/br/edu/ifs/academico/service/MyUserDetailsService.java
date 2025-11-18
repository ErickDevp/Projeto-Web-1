package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.UsuarioDTO;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public MyUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name())
        );

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getSenha(),
                authorities
        );
    }

    // Verifica se email já existe
    public boolean existsByEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    // Salva usuário com senha criptografada
    public Usuario saveUsuario(UsuarioDTO usuarioDTO) {
        Usuario entity = Usuario.builder()
                .nome(usuarioDTO.nome())
                .email(usuarioDTO.email())
                .senha(passwordEncoder.encode(usuarioDTO.senha()))
                .role(Role.USER)
                .build();

        return usuarioRepository.save(entity);
    }
}
