package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.CartaoUsuarioDTO;
import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.repository.CartaoUsuarioRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CartaoUsuarioService {

    private final CartaoUsuarioRepository cartaoRepository;
    private final UsuarioRepository usuarioRepository;

    public CartaoUsuarioService(CartaoUsuarioRepository cartaoRepository, UsuarioRepository usuarioRepository) {
        this.cartaoRepository = cartaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Buscar todos os cartoes de determinado usuario
    public List<CartaoUsuario> buscarPorUsuario(Long usuarioId) {
        return cartaoRepository.findByUsuarioId(usuarioId);
    }

    public Long salvarCartao(CartaoUsuarioDTO cartaoDTO) {
        var usuario = usuarioRepository.findById(cartaoDTO.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var entity = CartaoUsuario.builder()
                .nome(cartaoDTO.nome())
                .bandeira(cartaoDTO.bandeira())
                .tipo(cartaoDTO.tipo())
                .pontos(cartaoDTO.pontos())
                .usuario(usuario)
                .build();

        return cartaoRepository.save(entity).getId();
    }

    public void atualizarCartao(CartaoUsuarioDTO cartaoDTO, Long id) {
        var cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if (cartaoDTO.nome() != null) cartao.setNome(cartaoDTO.nome());
        if (cartaoDTO.bandeira() != null) cartao.setBandeira(cartaoDTO.bandeira());
        if (cartaoDTO.tipo() != null) cartao.setTipo(cartaoDTO.tipo());
        if (cartaoDTO.pontos() != null) cartao.setPontos(cartaoDTO.pontos());

        cartaoRepository.save(cartao);
    }

    public void apagarCartao(Long id) {
        if (!cartaoRepository.existsById(id)) {
            throw new RuntimeException("Cartão não encontrado");
        }
        cartaoRepository.deleteById(id);
    }
}
