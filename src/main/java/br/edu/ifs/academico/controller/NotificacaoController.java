package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.notificacao.request.NotificacaoRequestDTO;
import br.edu.ifs.academico.DTO.notificacao.response.NotificacaoResponseDTO;
import br.edu.ifs.academico.service.NotificacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacao")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    public ResponseEntity<List<NotificacaoResponseDTO>> buscarTodasNotificacoes(Authentication authentication) {
        return ResponseEntity.ok(notificacaoService.buscarNotificacoes(authentication.getName()));
    }

    @GetMapping("/publicas")
    public ResponseEntity<List<NotificacaoResponseDTO>> buscarNotificacoesPublicas() {
        return ResponseEntity.ok(notificacaoService.buscarNotificacoesPublicas());
    }

    @PostMapping("/criar")
    public ResponseEntity<NotificacaoResponseDTO> criarNotificacao(
            @Valid @RequestBody NotificacaoRequestDTO notificacaoRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificacaoService.criarNotificacao(notificacaoRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificacaoResponseDTO> atualizarNotificacao(@PathVariable Long id,
            @Valid @RequestBody NotificacaoRequestDTO notificacaoRequestDTO) {
        return ResponseEntity.ok(notificacaoService.atualizarNotificacao(notificacaoRequestDTO, id));
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
