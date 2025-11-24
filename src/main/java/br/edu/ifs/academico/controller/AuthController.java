package br.edu.ifs.academico.controller;

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

}

