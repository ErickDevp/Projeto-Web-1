package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.MovimentacaoPontosDTO;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovimentacaoPontosService {

    private final MovimentacaoPontosRepository movimentacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimentacaoPontosService(MovimentacaoPontosRepository movimentacaoRepository, UsuarioRepository usuarioRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<MovimentacaoPontos> buscarMovimentacoesPorId(Long usuarioId) {
        return movimentacaoRepository.findByUsuarioId(usuarioId);
    }

    public Long salvarMovimentacao(MovimentacaoPontosDTO movimentacaoDTO) {
        var usuario = usuarioRepository.findById(movimentacaoDTO.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        MovimentacaoPontos entity = MovimentacaoPontos.builder()
                .valor(movimentacaoDTO.valor())
                .pontos_calculados(movimentacaoDTO.pontos_calculados())
                .status(movimentacaoDTO.status())
                .build();

        return movimentacaoRepository.save(entity).getId();
    }

    public void atualizarMovimentacao(MovimentacaoPontosDTO movimentacaoDTO, Long id) {
        var movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrado"));

        if(movimentacaoDTO.valor() != null) { movimentacao.setValor(movimentacaoDTO.valor()); }
        if(movimentacaoDTO.pontos_calculados() != null) { movimentacao.setPontos_calculados(movimentacaoDTO.pontos_calculados()); }
        if(movimentacaoDTO.status() != null) { movimentacao.setStatus(movimentacaoDTO.status()); }

        movimentacaoRepository.save(movimentacao);
    }

    public void apagarMovimentacao(Long id) {
        if(!movimentacaoRepository.existsById(id)) {
            throw new RuntimeException("Movimentação não encontrado");
        }
        movimentacaoRepository.deleteById(id);
    }

}
