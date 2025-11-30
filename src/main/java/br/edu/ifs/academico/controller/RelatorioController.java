package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.relatorio.RelatorioResponseDTO;
import br.edu.ifs.academico.repository.UsuarioRepository;
import br.edu.ifs.academico.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping
    public ResponseEntity<RelatorioResponseDTO> gerar(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(relatorioService.gerarRelatorio(user.getUsername()));
    }

    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportarCsv(@AuthenticationPrincipal UserDetails user) {
        byte[] arquivo = relatorioService.gerarCsv(user.getUsername());

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=relatorio.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(arquivo);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> gerarPdf(@AuthenticationPrincipal UserDetails user) {
        byte[] pdf = relatorioService.gerarPdf(user.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio.pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

}



