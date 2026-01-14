package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.StatusMovimentacaoDTO;
import br.edu.ifs.academico.entity.StatusMovimentacao;
import br.edu.ifs.academico.repository.StatusMovimentacaoRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@SuppressWarnings("null")
public class StatusMovimentacaoService {

    private final StatusMovimentacaoRepository statusRepository;
    private final MovimentacaoPontosRepository movimentacaoRepository;

    public StatusMovimentacaoService(StatusMovimentacaoRepository statusRepository,
            MovimentacaoPontosRepository movimentacaoRepository) {
        this.statusRepository = statusRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    // busco o Status da movimentacao
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Optional<StatusMovimentacao> buscarStatusPorId(Long movimentacaoId, String emailLogado) {

        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioEmail(movimentacaoId, emailLogado)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada ou não pertence ao usuário"));

        return statusRepository.findByMovimentacaoId(movimentacao.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Long criarStatus(StatusMovimentacaoDTO statusDTO, String emailLogado) {

        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioEmail(statusDTO.movimentacaoId(), emailLogado)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada ou não pertence ao usuário"));

        var entity = StatusMovimentacao.builder()
                .movimentacao(movimentacao)
                .status(statusDTO.status())
                .motivo(statusDTO.motivo())
                .build();

        return statusRepository.save(entity).getId();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void atualizarStatus(StatusMovimentacaoDTO statusDTO, Long id, String emailLogado) {

        var status = statusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status não encontrado"));

        if (!status.getMovimentacao().getUsuario().getEmail().equals(emailLogado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        if (statusDTO.status() != null)
            status.setStatus(statusDTO.status());
        if (statusDTO.motivo() != null)
            status.setMotivo(statusDTO.motivo());

        statusRepository.save(status);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void apagarStatus(Long id, String emailLogado) {
        var status = statusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("status não encontrado"));

        if (!status.getMovimentacao().getUsuario().getEmail().equals(emailLogado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        var movimentacao = status.getMovimentacao();
        movimentacao.setStatus(null);

        movimentacaoRepository.save(movimentacao);
        statusRepository.delete(status);
    }

}
