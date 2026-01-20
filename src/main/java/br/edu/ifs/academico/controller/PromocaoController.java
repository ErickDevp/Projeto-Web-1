package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.entity.Promocao;
import br.edu.ifs.academico.service.PromocaoService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<Promocao>> buscartodasPromocoes() {
        return ResponseEntity.ok(promocaoService.buscarPromocoes());
    }

    @PostMapping("/criar")
    public ResponseEntity<String> criarPromocao(@Valid @RequestBody PromocaoDTO promocaoDTO) {
        promocaoService.criarPromocao(promocaoDTO);
        return ResponseEntity.ok("Operação realizada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarPromocao(@PathVariable Long id,
                                                  @Valid @RequestBody PromocaoDTO promocaoDTO) {
        promocaoService.atualizarPromocao(promocaoDTO, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarPromocao(@PathVariable Long id) {
        promocaoService.apagarPromocao(id);
        return ResponseEntity.noContent().build();
    }

}
