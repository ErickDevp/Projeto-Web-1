package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.saldo.response.SaldoResponseDTO;
import br.edu.ifs.academico.service.SaldoUsuarioProgramaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saldo")
public class SaldoUsuarioProgramaController {

    private final SaldoUsuarioProgramaService SaldoService;

    public SaldoUsuarioProgramaController(SaldoUsuarioProgramaService saldoService) {
        SaldoService = saldoService;
    }

    @GetMapping
    public ResponseEntity<List<SaldoResponseDTO>> buscarTodosSaldosUsuario(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(SaldoService.buscarTodosSaldosUsuario(userDetails.getUsername()));
    }
}
