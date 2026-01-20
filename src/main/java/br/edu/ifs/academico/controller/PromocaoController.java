package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.promocao.request.PromocaoRequestDTO;
import br.edu.ifs.academico.DTO.promocao.response.PromocaoResponseDTO;
import br.edu.ifs.academico.entity.Promocao;
import br.edu.ifs.academico.service.PromocaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promocao")
public class PromocaoController {

    private final PromocaoService promocaoService;

    public PromocaoController(PromocaoService promocaoService) {
        this.promocaoService = promocaoService;
    }

    @GetMapping
    public ResponseEntity<List<PromocaoResponseDTO>> buscartodasPromocoes() {
        return ResponseEntity.ok(promocaoService.buscarPromocoes());
    }

    @PostMapping("/criar")
    public ResponseEntity<PromocaoResponseDTO> criarPromocao(@Valid @RequestBody PromocaoRequestDTO promocaoRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promocaoService.criarPromocao(promocaoRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromocaoResponseDTO> atualizarPromocao(@PathVariable Long id,
                                                  @Valid @RequestBody PromocaoRequestDTO promocaoRequestDTO) {
        return ResponseEntity.ok(promocaoService.atualizarPromocao(promocaoRequestDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarPromocao(@PathVariable Long id) {
        promocaoService.apagarPromocao(id);
        return ResponseEntity.noContent().build();
    }

}
