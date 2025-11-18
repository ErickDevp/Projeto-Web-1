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
    public List<Comprovante> buscarComprovantePorId(Long movimentacaoId) {
        var movimentacao = movimentacaoRepository.findById(movimentacaoId)
                .orElseThrow(() -> new RuntimeException("movimentacão não encontrado"));

        return comprovanteRepository.findByMovimentacaoId(movimentacao.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Long criarComprovante(ComprovanteDTO comprovanteDTO) {
        var movimentacao = movimentacaoRepository.findById(comprovanteDTO.movimentacaoId())
                .orElseThrow(() -> new RuntimeException("movimentacão não encontrada"));

        Comprovante entity = Comprovante.builder()
                .movimentacao(movimentacao)
                .caminho(comprovanteDTO.caminho())
                .tipo_arq(comprovanteDTO.tipo_arq())
                .tamanho_bytes(comprovanteDTO.tamanho_bytes())
                .build();

        return comprovanteRepository.save(entity).getId();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void atualizarComprovante(ComprovanteDTO comprovanteDTO, Long id) {
        var comprovante = comprovanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprovante não encontrado"));

        if(comprovanteDTO.caminho() != null ) { comprovante.setCaminho(comprovanteDTO.caminho()); }
        if(comprovanteDTO.tipo_arq() != null) { comprovante.setTipo_arq(comprovanteDTO.tipo_arq()); }
        if(comprovanteDTO.tamanho_bytes() != null) { comprovante.setTamanho_bytes(comprovanteDTO.tamanho_bytes()); }
        if(comprovanteDTO.movimentacaoId() != null && movimentacaoRepository.existsById(comprovanteDTO.movimentacaoId())) {
            var movimentacao = movimentacaoRepository.findById(comprovanteDTO.movimentacaoId())
                    .orElseThrow(() -> new RuntimeException("movimentacão não encontrada"));

            comprovante.setMovimentacao(movimentacao);
        }


        comprovanteRepository.save(comprovante);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void apagarComprovante(Long id) {
        if(!comprovanteRepository.existsById(id)) {
            throw new RuntimeException("Comprovante não encontrado");
        }
        comprovanteRepository.deleteById(id);
    }
}