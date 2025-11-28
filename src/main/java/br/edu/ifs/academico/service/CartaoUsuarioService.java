package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.CartaoUsuarioDTO;
import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.repository.CartaoUsuarioRepository;
import br.edu.ifs.academico.repository.ProgramaFidelidadeRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartaoUsuarioService {

    private final CartaoUsuarioRepository cartaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramaFidelidadeRepository programaRepository;

    public CartaoUsuarioService(CartaoUsuarioRepository cartaoRepository, UsuarioRepository usuarioRepository, ProgramaFidelidadeRepository programaRepository) {
        this.cartaoRepository = cartaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.programaRepository = programaRepository;
    }

    // Buscar todos os cartoes de determinado usuario
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<CartaoUsuario> buscarTodosCartoes(String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return cartaoRepository.findByUsuarioId(usuario.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Long criarCartao(CartaoUsuarioDTO cartaoDTO, String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Buscar todos os programas pelo IDs recebidos no DTO
        Set<ProgramaFidelidade> programas = cartaoDTO.programaIds().stream()
                .map(id -> programaRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Programa Fidelidade não encontrado: " + id)))
                .collect(Collectors.toSet());

        var entity = CartaoUsuario.builder()
                .usuario(usuario)
                .programas(programas)
                .nome(cartaoDTO.nome())
                .bandeira(cartaoDTO.bandeira())
                .tipo(cartaoDTO.tipo())
                .pontos(cartaoDTO.pontos())
                .build();

        return cartaoRepository.save(entity).getId();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void atualizarCartao(CartaoUsuarioDTO cartaoDTO, Long id, String username) {
        var cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if (!cartao.getUsuario().getEmail().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        if (cartaoDTO.nome() != null) cartao.setNome(cartaoDTO.nome());
        if (cartaoDTO.bandeira() != null) cartao.setBandeira(cartaoDTO.bandeira());
        if (cartaoDTO.tipo() != null) cartao.setTipo(cartaoDTO.tipo());
        if (cartaoDTO.pontos() != null) cartao.setPontos(cartaoDTO.pontos());
        if (cartaoDTO.programaIds() != null) {
            Set<ProgramaFidelidade> programas = cartaoDTO.programaIds().stream()
                    .map(idPrograma -> programaRepository.findById(idPrograma)
                            .orElseThrow(() -> new RuntimeException("Programa Fidelidade não encontrado: " + idPrograma)))
                    .collect(Collectors.toSet());

            cartao.setProgramas(programas);
        }

        cartaoRepository.save(cartao);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void apagarCartao(Long id, String username) {
        var cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if (!cartao.getUsuario().getEmail().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        cartaoRepository.deleteById(id);
    }
}
