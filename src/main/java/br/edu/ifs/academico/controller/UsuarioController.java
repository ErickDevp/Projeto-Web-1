package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.UsuarioDTO;
import br.edu.ifs.academico.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")

public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> pegarUsuarioAtual(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(usuarioService.buscarUsuario(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        usuarioService.atualizarUsuario(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usuarioService.apagarUsuario(id);
        return ResponseEntity.noContent().build();
    }

}
