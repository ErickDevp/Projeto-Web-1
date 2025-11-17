package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.HistoricoStatusMovimentacaoDTO;
import br.edu.ifs.academico.entity.Comprovante;
import br.edu.ifs.academico.entity.HistoricoStatusMovimentacao;
import br.edu.ifs.academico.repository.HistoricoStatusMovimentacaoRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistoricoStatusMovimentacaoService {

    private final HistoricoStatusMovimentacaoRepository historicoRepository;
    private final MovimentacaoPontosRepository movimentacaoRepository;

    public HistoricoStatusMovimentacaoService(HistoricoStatusMovimentacaoRepository historicoRepository, MovimentacaoPontosRepository movimentacaoRepository) {
        this.historicoRepository = historicoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    // busco o historico movimentacao
    public Optional<HistoricoStatusMovimentacao> buscarHistoricoPorId(Long movimentacaoId) {
        return historicoRepository.findByMovimentacaoId(movimentacaoId);
    }

    public Long salvarHistorico(HistoricoStatusMovimentacaoDTO historicoDTO) {
        var movimentacao = movimentacaoRepository.findById(historicoDTO.movimentacaoId())
                .orElseThrow(() -> new RuntimeException("movimentacão não encontrada"));

        HistoricoStatusMovimentacao entity = HistoricoStatusMovimentacao.builder()
                .status(historicoDTO.status())
                .motivo(historicoDTO.motivo())
                .movimentacao(movimentacao)
                .build();

        return historicoRepository.save(entity).getId();
    }

    public void atualizarHistorico(HistoricoStatusMovimentacaoDTO historicoDTO, Long id) {
        var historico = historicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historico não encontrado"));

        if(historicoDTO.status() != null) { historico.setStatus(historicoDTO.status()); }
        if(historicoDTO.motivo() != null) { historico.setMotivo(historicoDTO.motivo()); }

        historicoRepository.save(historico);
    }

    public void apagarHistorico(Long id) {
        if(!historicoRepository.existsById(id)) {
            throw new RuntimeException("Historico não encontrado");
        }
        historicoRepository.deleteById(id);
    }
}
