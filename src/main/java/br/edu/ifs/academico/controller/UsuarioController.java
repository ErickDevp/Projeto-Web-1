package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @Valid @RequestBody UsuarioDTO usuarioDTO) {

        usuarioService.atualizarUsuario(usuarioDTO, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deletarMinhaConta(
            @AuthenticationPrincipal UserDetails userDetails) {
        usuarioService.apagarUsuario(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> salvarFoto(@RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        usuarioService.salvarFotoPerfil(file, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/foto")
    public ResponseEntity<byte[]> lerFoto(@AuthenticationPrincipal UserDetails userDetails) {
        var arquivo = usuarioService.lerFotoPerfil(userDetails.getUsername());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(arquivo.contentType()))
                .body(arquivo.bytes());
    }
}
