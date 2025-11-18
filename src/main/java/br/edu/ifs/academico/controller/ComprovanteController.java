package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.ComprovanteDTO;
import br.edu.ifs.academico.entity.Comprovante;
import br.edu.ifs.academico.service.ComprovanteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comprovantes")
public class ComprovanteController {

    private final ComprovanteService comprovanteService;

    public ComprovanteController(ComprovanteService comprovanteService) {
        this.comprovanteService = comprovanteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Comprovante>> buscarComprovantes(@PathVariable Long id) {
        return ResponseEntity.ok(comprovanteService.buscarComprovantePorId(id));
    }

    @PostMapping("/criar")
    public ResponseEntity<String> criarComprovante(@RequestBody ComprovanteDTO comprovanteDTO) {
        comprovanteService.criarComprovante(comprovanteDTO);
        return ResponseEntity.ok("Operação realizada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarComprovante(@PathVariable Long id, @RequestBody ComprovanteDTO comprovanteDTO) {
        comprovanteService.atualizarComprovante(comprovanteDTO, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarComprovante(@PathVariable Long id) {
        comprovanteService.apagarComprovante(id);
        return ResponseEntity.noContent().build();
    }
}