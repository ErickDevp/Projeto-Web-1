package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.auth.request.LoginRequestDTO;
import br.edu.ifs.academico.DTO.auth.request.PasswordChangeRequestDTO;
import br.edu.ifs.academico.DTO.auth.request.PasswordResetRequestDTO;
import br.edu.ifs.academico.DTO.auth.request.RegisterRequestDTO;
import br.edu.ifs.academico.DTO.auth.response.AuthResponseDTO;
import br.edu.ifs.academico.DTO.auth.response.PasswordResetResponseDTO;
import br.edu.ifs.academico.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.login(loginRequestDTO));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.saveUsuario(registerRequestDTO));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<PasswordResetResponseDTO> forgotPassword(
            @Valid @RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.solicitarRedefinicaoSenha(passwordResetRequestDTO));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordChangeRequestDTO passwordChangeRequestDTO) {
        authService.redefinirSenha(passwordChangeRequestDTO);
        return ResponseEntity.ok("Senha alterada com sucesso. Agora você pode fazer login.");
    }
}
