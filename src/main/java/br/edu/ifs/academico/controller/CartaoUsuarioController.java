package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.CartaoUsuarioDTO;
import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.service.CartaoUsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartao")
public class CartaoUsuarioController {

    private final CartaoUsuarioService cartaoService;

    public CartaoUsuarioController(CartaoUsuarioService cartaoService) {
        this.cartaoService = cartaoService;
    }

    // Buscar todos os cartões de um usuário
    @GetMapping
    public ResponseEntity<List<CartaoUsuario>> buscarMeusCartoes(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(cartaoService.buscarTodosCartoes(userDetails.getUsername()));
    }

    // Criar um novo cartão vinculado ao usuário
    @PostMapping("/criar")
    public ResponseEntity<String> criarCartao(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody CartaoUsuarioDTO cartaoDTO) {
        cartaoService.criarCartao(cartaoDTO, userDetails.getUsername());
        return ResponseEntity.ok("Operação realizada");
    }

    // Atualizar um cartão existente
    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarCartao(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable Long id, @RequestBody CartaoUsuarioDTO cartaoDTO) {
        cartaoService.atualizarCartao(cartaoDTO, id,userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // Apagar um cartão
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarCartao(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable Long id) {
        cartaoService.apagarCartao(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
