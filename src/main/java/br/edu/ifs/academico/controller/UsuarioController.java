package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.UsuarioDTO;
import br.edu.ifs.academico.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@SuppressWarnings("null")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> pegarUsuarioAtual(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(usuarioService.buscarUsuario(email));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioDTO> atualizarMeuPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UsuarioDTO usuarioDTO) {

        usuarioService.atualizarUsuario(usuarioDTO, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deletarMinhaConta(
            @AuthenticationPrincipal UserDetails userDetails) {
        usuarioService.apagarUsuario(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
