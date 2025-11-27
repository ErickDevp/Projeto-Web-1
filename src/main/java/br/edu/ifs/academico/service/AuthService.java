package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.UsuarioDTO;
import br.edu.ifs.academico.entity.PasswordResetToken;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import br.edu.ifs.academico.repository.PasswordResetTokenRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository tokenRepository;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService,
            BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
            PasswordResetTokenRepository tokenRepository) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
    }

    // Verifica se email já existe
    public boolean existsByEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    // Salva usuário com senha criptografada
    public String saveUsuario(UsuarioDTO usuarioDTO) {
        if (existsByEmail(usuarioDTO.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        Usuario entity = Usuario.builder()
                .nome(usuarioDTO.nome())
                .email(usuarioDTO.email())
                .senha(passwordEncoder.encode(usuarioDTO.senha()))
                .role(Role.USER)
                .build();

        Usuario saved = usuarioRepository.save(entity);

        // Cria o UserDetails manualmente (sem fazer SELECT)
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                saved.getEmail(),
                saved.getSenha(),
                List.of(new SimpleGrantedAuthority("ROLE_" + saved.getRole().name())));

        return jwtService.generateToken(userDetails);
    }

    // login usuario
    public String login(String email, String senha) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, senha));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return jwtService.generateToken(userDetails);

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }
    }

    // 1. Gera o token e o retorna (para ser exibido na API)
    public String solicitarRedefinicaoSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        // Se já existir um token antigo para esse usuário, apaga para gerar um novo
        tokenRepository.findByUsuario(usuario).ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .usuario(usuario)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build();

        tokenRepository.save(resetToken);

        // AQUI: Retornamos o token para o Controller devolver no JSON
        return token;
    }

    // 2. Recebe o token e a nova senha para efetivar a troca
    public void redefinirSenha(String token, String novaSenha) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido"));

        if (resetToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expirado. Solicite um novo.");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha)); // Criptografa a nova senha
        usuarioRepository.save(usuario);
        tokenRepository.delete(resetToken);
    }
}
