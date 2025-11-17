package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.CartaoUsuarioDTO;
import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.service.CartaoUsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartoes")
public class CartaoUsuarioController {

    private final CartaoUsuarioService cartaoService;

    public CartaoUsuarioController(CartaoUsuarioService cartaoService) {
        this.cartaoService = cartaoService;
    }

    // 🔹 Buscar todos os cartões de um usuário
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<CartaoUsuario>> buscarPorUsuario(@PathVariable Long id) {
        var cartoes = cartaoService.buscarPorUsuario(id);
        return ResponseEntity.ok(cartoes);
    }

    // 🔹 Criar um novo cartão vinculado ao usuário
    @PostMapping
    public ResponseEntity<Long> criarCartao(@RequestBody CartaoUsuarioDTO cartaoDTO) {
        var id = cartaoService.salvarCartao(cartaoDTO);
        return ResponseEntity.ok(id);
    }

    // 🔹 Atualizar um cartão existente
    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarCartao(@PathVariable Long id, @RequestBody CartaoUsuarioDTO cartaoDTO) {
        cartaoService.atualizarCartao(cartaoDTO, id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Apagar um cartão
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarCartao(@PathVariable Long id) {
        cartaoService.apagarCartao(id);
        return ResponseEntity.noContent().build();
    }
}
