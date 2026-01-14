package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.PromocaoDTO;
import br.edu.ifs.academico.entity.Promocao;
import br.edu.ifs.academico.repository.ProgramaFidelidadeRepository;
import br.edu.ifs.academico.repository.PromocaoRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("null")
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;
    private final ProgramaFidelidadeRepository programaRepository;

    public PromocaoService(PromocaoRepository promocaoRepository, ProgramaFidelidadeRepository programaRepository) {
        this.promocaoRepository = promocaoRepository;
        this.programaRepository = programaRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Promocao> buscarPromocoes() {
        return promocaoRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public Long criarPromocao(PromocaoDTO promoDTO) {
        var programa = programaRepository.findById(promoDTO.programaId())
                .orElseThrow(() -> new RuntimeException("programaFidelidade não encontrada"));

        Promocao entity = Promocao.builder()
                .programa(programa)
                .titulo(promoDTO.titulo())
                .descricao(promoDTO.titulo())
                .data_inicio(promoDTO.data_inicio())
                .data_fim(promoDTO.data_fim())
                .ativo(promoDTO.ativo())
                .build();

        return promocaoRepository.save(entity).getId();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void atualizarPromocao(PromocaoDTO promoDTO, Long id) {
        var promocao = promocaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoção não encontrado"));

        if (promoDTO.titulo() != null) {
            promocao.setTitulo(promoDTO.titulo());
        }
        if (promoDTO.descricao() != null) {
            promocao.setDescricao(promoDTO.descricao());
        }
        if (promoDTO.data_inicio() != null) {
            promocao.setData_inicio(promoDTO.data_inicio());
        }
        if (promoDTO.data_fim() != null) {
            promocao.setData_fim(promoDTO.data_fim());
        }
        if (promoDTO.ativo() != null) {
            promocao.setAtivo(promoDTO.ativo());
        }

        Long programaId = promoDTO.programaId();
        if (programaId != null && programaRepository.existsById(programaId)) {
            var programa = programaRepository.findById(programaId)
                    .orElseThrow(() -> new RuntimeException("programaFidelidade não encontrada"));

            promocao.setPrograma(programa);
        }

        promocaoRepository.save(promocao);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void apagarPromocao(Long id) {
        if (!promocaoRepository.existsById(id)) {
            throw new RuntimeException("promoção não encontrado");
        }
        promocaoRepository.deleteById(id);
    }

}
