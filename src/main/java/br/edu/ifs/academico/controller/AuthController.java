package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.EsqueciSenhaDTO;
import br.edu.ifs.academico.DTO.RedefinirSenhaDTO;
import br.edu.ifs.academico.DTO.UsuarioDTO;
import br.edu.ifs.academico.DTO.UsuarioLoginDTO;
import br.edu.ifs.academico.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioLoginDTO usuario) {
        String token = authService.login(usuario.email(), usuario.senha());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioDTO usuario) {
        String token = authService.saveUsuario(usuario);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody EsqueciSenhaDTO dto) {
        // Pega o token gerado pelo serviço
        String tokenGerado = authService.solicitarRedefinicaoSenha(dto.email());

        // Retorna no JSON: { "reset_token": "abc-123-..." }
        return ResponseEntity.ok(Map.of(
                "message", "Solicitação recebida. Use o token abaixo para resetar a senha.",
                "reset_token", tokenGerado));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody RedefinirSenhaDTO dto) {
        authService.redefinirSenha(dto.token(), dto.novaSenha());
        return ResponseEntity.ok("Senha alterada com sucesso. Agora você pode fazer login.");
    }
}
