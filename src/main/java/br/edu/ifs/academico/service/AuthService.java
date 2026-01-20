package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.auth.request.LoginRequestDTO;
import br.edu.ifs.academico.DTO.auth.request.PasswordChangeRequestDTO;
import br.edu.ifs.academico.DTO.auth.request.PasswordResetRequestDTO;
import br.edu.ifs.academico.DTO.auth.request.RegisterRequestDTO;
import br.edu.ifs.academico.DTO.auth.response.AuthResponseDTO;
import br.edu.ifs.academico.DTO.auth.response.PasswordResetResponseDTO;
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
@SuppressWarnings("null")
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
    public AuthResponseDTO saveUsuario(RegisterRequestDTO registerRequestDTO) {
        if (existsByEmail(registerRequestDTO.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        Usuario entity = Usuario.builder()
                .nome(registerRequestDTO.nome())
                .email(registerRequestDTO.email())
                .senha(passwordEncoder.encode(registerRequestDTO.senha()))
                .role(Role.USER)
                .build();

        Usuario saved = usuarioRepository.save(entity);

        // sem fazer SELECT
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                saved.getEmail(),
                saved.getSenha(),
                List.of(new SimpleGrantedAuthority("ROLE_" + saved.getRole().name())));

        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(token);
    }

    // login usuario
    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.senha()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            return new AuthResponseDTO(token);

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }
    }

    // 1. Gera o token e o retorna (para ser exibido na API)
    public PasswordResetResponseDTO solicitarRedefinicaoSenha(PasswordResetRequestDTO passwordResetRequestDTO) {
        Usuario usuario = usuarioRepository.findByEmail(passwordResetRequestDTO.email())
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
        return new PasswordResetResponseDTO(token);
    }

    // 2. Recebe o token e a nova senha para efetivar a troca
    public void redefinirSenha(PasswordChangeRequestDTO passwordChangeRequestDTO) {
        PasswordResetToken resetToken = tokenRepository.findByToken(passwordChangeRequestDTO.token())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido"));

        if (resetToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expirado. Solicite um novo.");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(passwordChangeRequestDTO.novaSenha())); // Criptografa a nova senha
        usuarioRepository.save(usuario);
        tokenRepository.delete(resetToken);
    }
}
