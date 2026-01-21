package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.programa.request.ProgramaRequestDTO;
import br.edu.ifs.academico.DTO.programa.response.ProgramaComPromocoesResponseDTO;
import br.edu.ifs.academico.DTO.programa.response.ProgramaResponseDTO;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.service.ProgramaFidelidadeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<ProgramaComPromocoesResponseDTO>> buscarProgramasFidelidade() {
        return ResponseEntity.ok(programaService.buscarProgramas());
    }

    @PostMapping("/criar")
    public ResponseEntity<ProgramaResponseDTO> criarPrograma(@Valid @RequestBody ProgramaRequestDTO programaRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(programaService.criarPrograma(programaRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramaResponseDTO> atualizarPrograma(@PathVariable Long id,
                                                  @Valid @RequestBody ProgramaRequestDTO programaRequestDTO) {
        return ResponseEntity.ok(programaService.atualizarPrograma(programaRequestDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarPrograma(@PathVariable Long id) {
        programaService.apagarPrograma(id);
        return ResponseEntity.noContent().build();
    }
}
