package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.service.ProgramaFidelidadeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/programa")
public class ProgramaFidelidadeController {

    private final ProgramaFidelidadeService programaService;

    public ProgramaFidelidadeController(ProgramaFidelidadeService programaService) {
        this.programaService = programaService;
    }

    @GetMapping
    public ResponseEntity<List<ProgramaFidelidade>> buscarProgramasFidelidade() {
        return ResponseEntity.ok(programaService.buscarProgramas());
    }

    @PostMapping("/criar")
    public ResponseEntity<String> criarPrograma(@Valid @RequestBody ProgramaFidelidadeDTO programaDTO) {
        programaService.criarPrograma(programaDTO);
        return ResponseEntity.ok("Operação realizada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarPrograma(@PathVariable Long id,
                                                  @Valid @RequestBody ProgramaFidelidadeDTO programaDTO) {
        programaService.atualizarPrograma(programaDTO, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarPrograma(@PathVariable Long id) {
        programaService.apagarPrograma(id);
        return ResponseEntity.noContent().build();
    }
}
