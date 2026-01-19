package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.StatusMovimentacaoDTO;
import br.edu.ifs.academico.entity.StatusMovimentacao;
import br.edu.ifs.academico.service.StatusMovimentacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/status")
public class StatusMovimentacaoController {

    private final StatusMovimentacaoService statusService;

    public StatusMovimentacaoController(StatusMovimentacaoService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<StatusMovimentacao>> buscarStatus(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(statusService.buscarStatusPorId(id, userDetails.getUsername()));
    }

    @PostMapping("/criar")
    public ResponseEntity<String> criarStatus(@RequestBody StatusMovimentacaoDTO statusDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        statusService.criarStatus(statusDTO, userDetails.getUsername());
        return ResponseEntity.ok("Operação realizada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody StatusMovimentacaoDTO statusDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        statusService.atualizarStatus(statusDTO, id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarStatus(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        statusService.apagarStatus(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}