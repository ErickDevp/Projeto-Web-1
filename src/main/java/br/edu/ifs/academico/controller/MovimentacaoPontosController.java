package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.movimentacao.request.MovimentacaoRequestDTO;
import br.edu.ifs.academico.DTO.movimentacao.response.MovimentacaoResponseDTO;
import br.edu.ifs.academico.service.MovimentacaoPontosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<MovimentacaoResponseDTO> criarMovimentacao(
            @Valid @RequestBody MovimentacaoRequestDTO movimentacaoRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacaoService.criarMovimentacao(movimentacaoRequestDTO, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimentacaoResponseDTO> atualizarMovimentacao(@Valid @RequestBody MovimentacaoRequestDTO movimentacaoRequestDTO,
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(movimentacaoService.atualizarMovimentacao(movimentacaoRequestDTO, id, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarMovimentacao(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        movimentacaoService.apagarMovimentacao(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
