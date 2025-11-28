package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.NotificacaoDTO;
import br.edu.ifs.academico.entity.Notificacao;
import br.edu.ifs.academico.service.NotificacaoService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Notificacao>> buscarTodasNotificacoes() {
        return ResponseEntity.ok(notificacaoService.buscarNotificacoes());
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarNotificacao(@PathVariable Long id) {
        notificacaoService.apagarNotificacao(id);
        return ResponseEntity.noContent().build();
    }


}
