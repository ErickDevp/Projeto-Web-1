package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.HistoricoStatusMovimentacaoDTO;
import br.edu.ifs.academico.entity.Comprovante;
import br.edu.ifs.academico.entity.HistoricoStatusMovimentacao;
import br.edu.ifs.academico.repository.HistoricoStatusMovimentacaoRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Optional<HistoricoStatusMovimentacao> buscarHistoricoPorId(Long movimentacaoId) {
        var movimentacao = movimentacaoRepository.findById(movimentacaoId)
                .orElseThrow(() -> new RuntimeException("movimentacão não encontrado"));

        return historicoRepository.findByMovimentacaoId(movimentacao.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Long criarHistorico(HistoricoStatusMovimentacaoDTO historicoDTO) {
        var movimentacao = movimentacaoRepository.findById(historicoDTO.movimentacaoId())
                .orElseThrow(() -> new RuntimeException("movimentacão não encontrada"));

        HistoricoStatusMovimentacao entity = HistoricoStatusMovimentacao.builder()
                .movimentacao(movimentacao)
                .status(historicoDTO.status())
                .motivo(historicoDTO.motivo())
                .build();

        return historicoRepository.save(entity).getId();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void atualizarHistorico(HistoricoStatusMovimentacaoDTO historicoDTO, Long id) {
        var historico = historicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historico não encontrado"));

        if(historicoDTO.status() != null) { historico.setStatus(historicoDTO.status()); }
        if(historicoDTO.motivo() != null) { historico.setMotivo(historicoDTO.motivo()); }
        if(historicoDTO.movimentacaoId() != null && movimentacaoRepository.existsById(historicoDTO.movimentacaoId())) {
            var movimentacao = movimentacaoRepository.findById(historicoDTO.movimentacaoId())
                    .orElseThrow(() -> new RuntimeException("movimentacão não encontrada"));

            historico.setMovimentacao(movimentacao);
        }

        historicoRepository.save(historico);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void apagarHistorico(Long id) {
        if(!historicoRepository.existsById(id)) {
            throw new RuntimeException("Historico não encontrado");
        }
        historicoRepository.deleteById(id);
    }
}
