package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.HistoricoStatusMovimentacaoDTO;
import br.edu.ifs.academico.entity.HistoricoStatusMovimentacao;
import br.edu.ifs.academico.service.HistoricoStatusMovimentacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/historicos")
public class HistoricoStatusMovimentacaoController {

    private final HistoricoStatusMovimentacaoService historicoService;

    public HistoricoStatusMovimentacaoController(HistoricoStatusMovimentacaoService historicoService) {
        this.historicoService = historicoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<HistoricoStatusMovimentacao>> buscarHistorico(@PathVariable Long id) {
        return ResponseEntity.ok(historicoService.buscarHistoricoPorId(id));
    }

    @PostMapping("/criar")
    public ResponseEntity<String> criarHistorico(@RequestBody HistoricoStatusMovimentacaoDTO historicoDTO) {
        historicoService.criarHistorico(historicoDTO);
        return ResponseEntity.ok("Operação realizada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarHistorico(@PathVariable Long id, @RequestBody HistoricoStatusMovimentacaoDTO historicoDTO) {
        historicoService.atualizarHistorico(historicoDTO, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarHistorico(@PathVariable Long id) {
        historicoService.apagarHistorico(id);
        return ResponseEntity.noContent().build();
    }
}