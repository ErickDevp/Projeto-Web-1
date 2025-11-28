package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.ComprovanteDTO;
import br.edu.ifs.academico.entity.Comprovante;
import br.edu.ifs.academico.service.ComprovanteService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comprovante")
public class ComprovanteController {

    private final ComprovanteService comprovanteService;

    public ComprovanteController(ComprovanteService comprovanteService) {
        this.comprovanteService = comprovanteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Comprovante>> buscarComprovantes(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(comprovanteService.buscarComprovantePorId(id, user.getUsername()));
    }

    @PostMapping("/criar")
    public ResponseEntity<String> criarComprovante(@RequestBody ComprovanteDTO comprovanteDTO, @AuthenticationPrincipal UserDetails userDetails) {
        comprovanteService.criarComprovante(comprovanteDTO, userDetails.getUsername());
        return ResponseEntity.ok("Operação realizada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarComprovante(@PathVariable Long id, @RequestBody ComprovanteDTO comprovanteDTO, @AuthenticationPrincipal UserDetails userDetails) {
        comprovanteService.atualizarComprovante(comprovanteDTO, id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarComprovante(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        comprovanteService.apagarComprovante(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}