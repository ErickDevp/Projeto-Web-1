package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.comprovante.response.ComprovanteResponseDTO;
import br.edu.ifs.academico.entity.Comprovante;
import br.edu.ifs.academico.service.ComprovanteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/comprovante")
@SuppressWarnings("null")
public class ComprovanteController {

    private final ComprovanteService comprovanteService;

    public ComprovanteController(ComprovanteService comprovanteService) {
        this.comprovanteService = comprovanteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ComprovanteResponseDTO>> buscarComprovantes(@PathVariable Long id,
                                                                           @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(comprovanteService.buscarComprovantePorId(id, user.getUsername()));
    }

    @PostMapping(value = "/criar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComprovanteResponseDTO> criarComprovante(@RequestParam("movimentacaoId") Long movimentacaoId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comprovanteService.criarComprovante(movimentacaoId, file, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarComprovante(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        comprovanteService.apagarComprovante(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/arquivo")
    public ResponseEntity<byte[]> lerComprovante(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        var arquivo = comprovanteService.lerBytesComprovante(id, userDetails.getUsername());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(arquivo.contentType()))
                .body(arquivo.bytes());
    }
}