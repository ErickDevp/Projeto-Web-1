package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.ComprovanteDTO;
import br.edu.ifs.academico.entity.Comprovante;
import br.edu.ifs.academico.repository.ComprovanteRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComprovanteService {

    private final ComprovanteRepository comprovanteRepository;
    private final MovimentacaoPontosRepository movimentacaoRepository;

    public ComprovanteService(ComprovanteRepository comprovanteRepository, MovimentacaoPontosRepository movimentacaoRepository) {
        this.comprovanteRepository = comprovanteRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    // busco todos os comprovantes de determinada movimentacao
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Comprovante> buscarComprovantePorId(Long movimentacaoId, String emailLogado) {
        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioEmail(movimentacaoId, emailLogado)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada para este usuário"));

        return comprovanteRepository.findByMovimentacaoId(movimentacao.getId());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Long criarComprovante(ComprovanteDTO comprovanteDTO, String emailLogado) {
        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioEmail(comprovanteDTO.movimentacaoId(), emailLogado)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada para este usuário"));

        Comprovante entity = Comprovante.builder()
                .movimentacao(movimentacao)
                .caminho(comprovanteDTO.caminho())
                .tipo_arq(comprovanteDTO.tipo_arq())
                .tamanho_bytes(comprovanteDTO.tamanho_bytes())
                .build();

        return comprovanteRepository.save(entity).getId();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void atualizarComprovante(ComprovanteDTO dto, Long id, String emailLogado) {

        var comprovante = comprovanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprovante não encontrado"));

        if (!comprovante.getMovimentacao().getUsuario().getEmail().equals(emailLogado)) {
            throw new RuntimeException("Você não pode alterar comprovante de outro usuário");
        }

        if (dto.caminho() != null) comprovante.setCaminho(dto.caminho());
        if (dto.tipo_arq() != null) comprovante.setTipo_arq(dto.tipo_arq());
        if (dto.tamanho_bytes() != null) comprovante.setTamanho_bytes(dto.tamanho_bytes());

        // Se mover o comprovante para outra movimentação, validar também
        if (dto.movimentacaoId() != null) {
            var novaMov = movimentacaoRepository
                    .findByIdAndUsuarioEmail(dto.movimentacaoId(), emailLogado)
                    .orElseThrow(() -> new RuntimeException("Movimentação não pertence ao usuário"));

            comprovante.setMovimentacao(novaMov);
        }

        comprovanteRepository.save(comprovante);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void apagarComprovante(Long id, String emailLogado) {

        var comprovante = comprovanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprovante não encontrado"));

        if (!comprovante.getMovimentacao().getUsuario().getEmail().equals(emailLogado)) {
            throw new RuntimeException("Você não pode apagar comprovante de outro usuário");
        }

        comprovanteRepository.deleteById(id);
    }
}