package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.MovimentacaoPontosDTO;
import br.edu.ifs.academico.DTO.MovimentacaoResponseDTO;
import br.edu.ifs.academico.service.MovimentacaoPontosService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimentacao")
public class MovimentacaoPontosController {

    private final MovimentacaoPontosService movimentacaoService;

    public MovimentacaoPontosController(MovimentacaoPontosService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarMovimentacoes(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(movimentacaoService.buscarTodasMovimentacoes(userDetails.getUsername()));
    }

    @PostMapping("/criar")
    public ResponseEntity<String> criarMovimentacao(@RequestBody MovimentacaoPontosDTO movimentacaoDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        movimentacaoService.criarMovimentacao(movimentacaoDTO, userDetails.getUsername());
        return ResponseEntity.ok("Operação realizada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarMovimentacao(@RequestBody MovimentacaoPontosDTO movimentacaoDTO,
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        movimentacaoService.atualizarMovimentacao(movimentacaoDTO, id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarMovimentacao(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        movimentacaoService.apagarMovimentacao(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
