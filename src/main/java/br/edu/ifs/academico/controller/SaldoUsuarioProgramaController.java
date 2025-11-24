package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.SaldoUsuarioProgramaDTO;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
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
    public ResponseEntity<List<SaldoUsuarioPrograma>> buscarTodosSaldosUsuario(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(SaldoService.buscarTodosSaldosUsuario(userDetails.getUsername()));
    }

    /* Provavelmente nao terar como criar, acredito que ao criar um cartao com determinado programa o saldo é criado
    @PostMapping("/criar")
    public ResponseEntity<String> criarSaldoUsuario(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody SaldoUsuarioProgramaDTO saldoDTO) {
        SaldoService.criarSaldoUsuario(saldoDTO, userDetails.getUsername());
        return ResponseEntity.ok("Operação realizada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarSaldo(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable Long id, @RequestBody SaldoUsuarioProgramaDTO saldoDTO) {
        SaldoService.atualizarSaldo(saldoDTO, id,userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarSaldo(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable Long id) {
        SaldoService.apagarSaldo(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

     */
}
