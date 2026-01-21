package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.cartao.request.CartaoRequestDTO;
import br.edu.ifs.academico.DTO.cartao.response.CartaoResponseDTO;
import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.mapper.CartaoMapper;
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
@SuppressWarnings("null")
public class CartaoUsuarioService {

    private final CartaoUsuarioRepository cartaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramaFidelidadeRepository programaRepository;
    private final CartaoMapper cartaoMapper;

    public CartaoUsuarioService(CartaoUsuarioRepository cartaoRepository, UsuarioRepository usuarioRepository,
                                ProgramaFidelidadeRepository programaRepository, CartaoMapper cartaoMapper) {
        this.cartaoRepository = cartaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.programaRepository = programaRepository;
        this.cartaoMapper = cartaoMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<CartaoResponseDTO> buscarTodosCartoes(String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return cartaoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(cartaoMapper::toResponseDTO)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CartaoResponseDTO criarCartao(CartaoRequestDTO cartaoRequestDTO, String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Set<ProgramaFidelidade> programas = cartaoRequestDTO.programaIds().stream()
                .map(id -> programaRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Programa Fidelidade não encontrado: " + id)))
                .collect(Collectors.toSet());

        var entity = CartaoUsuario.builder()
                .usuario(usuario)
                .programas(programas)
                .nome(cartaoRequestDTO.nome())
                .bandeira(cartaoRequestDTO.bandeira())
                .numero(cartaoRequestDTO.numero())
                .dataValidade(cartaoRequestDTO.dataValidade())
                .tipo(cartaoRequestDTO.tipo())
                .build();

        return cartaoMapper.toResponseDTO(cartaoRepository.save(entity));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CartaoResponseDTO atualizarCartao(CartaoRequestDTO cartaoRequestDTO, Long id, String username) {
        var cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if (!cartao.getUsuario().getEmail().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        if (cartaoRequestDTO.nome() != null) cartao.setNome(cartaoRequestDTO.nome());
        if (cartaoRequestDTO.bandeira() != null) cartao.setBandeira(cartaoRequestDTO.bandeira());
        if (cartaoRequestDTO.tipo() != null) cartao.setTipo(cartaoRequestDTO.tipo());
        if (cartaoRequestDTO.numero() != null) cartao.setNumero(cartaoRequestDTO.numero());
        if (cartaoRequestDTO.dataValidade() != null) cartao.setDataValidade(cartaoRequestDTO.dataValidade());
        if (cartaoRequestDTO.programaIds() != null) {
            Set<ProgramaFidelidade> programas = cartaoRequestDTO.programaIds().stream()
                    .map(idPrograma -> programaRepository.findById(idPrograma)
                            .orElseThrow(
                                    () -> new RuntimeException("Programa Fidelidade não encontrado: " + idPrograma)))
                    .collect(Collectors.toSet());

            cartao.setProgramas(programas);
        }

        return cartaoMapper.toResponseDTO(cartaoRepository.save(cartao));
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
