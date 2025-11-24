package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.HistoricoStatusMovimentacaoDTO;
import br.edu.ifs.academico.entity.Comprovante;
import br.edu.ifs.academico.entity.HistoricoStatusMovimentacao;
import br.edu.ifs.academico.repository.HistoricoStatusMovimentacaoRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public Optional<HistoricoStatusMovimentacao> buscarHistoricoPorId(Long movimentacaoId, String emailLogado) {

        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioEmail(movimentacaoId, emailLogado)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada ou não pertence ao usuário"));

        return historicoRepository.findByMovimentacaoId(movimentacao.getId());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Long criarHistorico(HistoricoStatusMovimentacaoDTO historicoDTO, String emailLogado) {

        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioEmail(historicoDTO.movimentacaoId(), emailLogado)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada ou não pertence ao usuário"));

        var entity = HistoricoStatusMovimentacao.builder()
                .movimentacao(movimentacao)
                .status(historicoDTO.status())
                .motivo(historicoDTO.motivo())
                .build();

        return historicoRepository.save(entity).getId();
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void atualizarHistorico(HistoricoStatusMovimentacaoDTO historicoDTO, Long id, String emailLogado) {

        var historico = historicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status não encontrado"));

        if (!historico.getMovimentacao().getUsuario().getEmail().equals(emailLogado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        if (historicoDTO.status() != null) historico.setStatus(historicoDTO.status());
        if (historicoDTO.motivo() != null) historico.setMotivo(historicoDTO.motivo());

        historicoRepository.save(historico);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void apagarHistorico(Long id, String emailLogado) {

        var historico = historicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Histórico não encontrado"));

        if (!historico.getMovimentacao().getUsuario().getEmail().equals(emailLogado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        historicoRepository.delete(historico);
    }

}
