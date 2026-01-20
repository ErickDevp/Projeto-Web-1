package br.edu.ifs.academico.controller;

import br.edu.ifs.academico.DTO.cartao.request.CartaoRequestDTO;
import br.edu.ifs.academico.DTO.cartao.response.CartaoResponseDTO;
import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.service.CartaoUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cartao")
public class CartaoUsuarioController {

    private final CartaoUsuarioService cartaoService;

    public CartaoUsuarioController(CartaoUsuarioService cartaoService) {
        this.cartaoService = cartaoService;
    }

    @GetMapping
    public ResponseEntity<List<CartaoResponseDTO>> buscarMeusCartoes(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartaoService.buscarTodosCartoes(userDetails.getUsername()));
    }

    @PostMapping("/criar")
    public ResponseEntity<CartaoResponseDTO> criarCartao(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CartaoRequestDTO cartaoRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartaoService.criarCartao(cartaoRequestDTO, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartaoResponseDTO> atualizarCartao(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id, @Valid @RequestBody CartaoRequestDTO cartaoRequestDTO) {
        return ResponseEntity.ok(cartaoService.atualizarCartao(cartaoRequestDTO, id, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarCartao(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        cartaoService.apagarCartao(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
