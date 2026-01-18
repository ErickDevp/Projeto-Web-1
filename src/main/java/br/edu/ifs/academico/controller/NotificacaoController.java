package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.NotificacaoDTO;
import br.edu.ifs.academico.DTO.NotificacaoResponseDTO;
import br.edu.ifs.academico.entity.Notificacao;
import br.edu.ifs.academico.service.NotificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacao")
@SuppressWarnings("null")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    public ResponseEntity<List<NotificacaoResponseDTO>> buscarTodasNotificacoes(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(notificacaoService.buscarNotificacoes(email));
    }

    @GetMapping("/publicas")
    public ResponseEntity<List<Notificacao>> buscarNotificacoesPublicas() {
        return ResponseEntity.ok(notificacaoService.buscarNotificacoesPublicas());
    }

    @PostMapping("/criar")
    public ResponseEntity<String> criarNotificacao(@RequestBody NotificacaoDTO notificacaoDTO) {
        notificacaoService.criarNotificacao(notificacaoDTO);
        return ResponseEntity.ok("Operação realizada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarNotificacao(@PathVariable Long id,
            @RequestBody NotificacaoDTO notificacaoDTO) {
        notificacaoService.atualizarNotificacao(notificacaoDTO, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        notificacaoService.marcarComoLida(id, email);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> dismissForMe(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        notificacaoService.dismissForUser(id, email);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteForAll(@PathVariable Long id) {
        notificacaoService.deleteForAll(id);
        return ResponseEntity.noContent().build();
    }

}
