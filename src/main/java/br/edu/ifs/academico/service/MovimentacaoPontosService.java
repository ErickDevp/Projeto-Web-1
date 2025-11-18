package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.MovimentacaoPontosDTO;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.repository.CartaoUsuarioRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import br.edu.ifs.academico.repository.SaldoUsuarioProgramaRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimentacaoPontosService {

    private final MovimentacaoPontosRepository movimentacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SaldoUsuarioProgramaRepository saldoRepository;
    private final CartaoUsuarioRepository cartaoRepository;

    public MovimentacaoPontosService(MovimentacaoPontosRepository movimentacaoRepository, UsuarioRepository usuarioRepository, SaldoUsuarioProgramaRepository saldoRepository, CartaoUsuarioRepository cartaoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.saldoRepository = saldoRepository;
        this.cartaoRepository = cartaoRepository;
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<MovimentacaoPontos> buscarTodasMovimentacoes(String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return movimentacaoRepository.findByUsuarioId(usuario.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Long criarMovimentacao(MovimentacaoPontosDTO movimentacaoDTO, String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var saldo = saldoRepository.findById(movimentacaoDTO.saldoId())
                .orElseThrow(() -> new RuntimeException("Saldo não encontrado"));

        var cartao = cartaoRepository.findById(movimentacaoDTO.cartaoId())
                .orElseThrow(() -> new RuntimeException("cartao não encontrado"));

        MovimentacaoPontos entity = MovimentacaoPontos.builder()
                .usuario(usuario)
                .saldo(saldo)
                .cartao(cartao)
                .valor(movimentacaoDTO.valor())
                .pontos_calculados(movimentacaoDTO.pontos_calculados())
                .data_ocorrencia(movimentacaoDTO.data_ocorrencia())
                .build();

        return movimentacaoRepository.save(entity).getId();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void atualizarMovimentacao(MovimentacaoPontosDTO movimentacaoDTO, Long id) {
        var movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrado"));

        if(movimentacaoDTO.valor() != null) { movimentacao.setValor(movimentacaoDTO.valor()); }
        if(movimentacaoDTO.pontos_calculados() != null) { movimentacao.setPontos_calculados(movimentacaoDTO.pontos_calculados()); }
        if(movimentacaoDTO.data_ocorrencia() != null) { movimentacao.setData_ocorrencia(movimentacaoDTO.data_ocorrencia()); }

        movimentacaoRepository.save(movimentacao);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void apagarMovimentacao(Long id) {
        if(!movimentacaoRepository.existsById(id)) {
            throw new RuntimeException("Movimentação não encontrado");
        }
        movimentacaoRepository.deleteById(id);
    }

}
