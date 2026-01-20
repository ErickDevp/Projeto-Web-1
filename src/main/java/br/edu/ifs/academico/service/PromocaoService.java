package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.promocao.request.PromocaoRequestDTO;
import br.edu.ifs.academico.DTO.promocao.response.PromocaoResponseDTO;
import br.edu.ifs.academico.entity.Promocao;
import br.edu.ifs.academico.mapper.PromocaoMapper;
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
    private final PromocaoMapper promocaoMapper;

    public PromocaoService(PromocaoRepository promocaoRepository, ProgramaFidelidadeRepository programaRepository, PromocaoMapper promocaoMapper) {
        this.promocaoRepository = promocaoRepository;
        this.programaRepository = programaRepository;
        this.promocaoMapper = promocaoMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<PromocaoResponseDTO> buscarPromocoes() {

        return promocaoRepository.findAll()
                .stream()
                .map(promocaoMapper::toResponseDTO)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public PromocaoResponseDTO criarPromocao(PromocaoRequestDTO promocaoRequestDTO) {
        var programa = programaRepository.findById(promocaoRequestDTO.programaId())
                .orElseThrow(() -> new RuntimeException("programaFidelidade não encontrada"));

        if (promocaoRequestDTO.dataFim().isBefore(promocaoRequestDTO.dataInicio())) {
            throw new IllegalArgumentException(
                    "A data de fim não pode ser anterior à data de início"
            );
        }

        Promocao entity = Promocao.builder()
                .programa(programa)
                .titulo(promocaoRequestDTO.titulo())
                .descricao(promocaoRequestDTO.titulo())
                .dataInicio(promocaoRequestDTO.dataInicio())
                .dataFim(promocaoRequestDTO.dataFim())
                .build();

        return promocaoMapper.toResponseDTO(promocaoRepository.save(entity));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public PromocaoResponseDTO atualizarPromocao(PromocaoRequestDTO promocaoRequestDTO, Long id) {
        var promocao = promocaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoção não encontrado"));

        if (promocaoRequestDTO.dataFim().isBefore(promocaoRequestDTO.dataInicio())) {
            throw new IllegalArgumentException(
                    "A data de fim não pode ser anterior à data de início"
            );
        }

        if (promocaoRequestDTO.titulo() != null) {
            promocao.setTitulo(promocaoRequestDTO.titulo());
        }
        if (promocaoRequestDTO.descricao() != null) {
            promocao.setDescricao(promocaoRequestDTO.descricao());
        }
        if (promocaoRequestDTO.dataInicio() != null) {
            promocao.setDataInicio(promocaoRequestDTO.dataInicio());
        }
        if (promocaoRequestDTO.dataFim() != null) {
            promocao.setDataFim(promocaoRequestDTO.dataFim());
        }

        Long programaId = promocaoRequestDTO.programaId();
        if (programaId != null && programaRepository.existsById(programaId)) {
            var programa = programaRepository.findById(programaId)
                    .orElseThrow(() -> new RuntimeException("programaFidelidade não encontrada"));

            promocao.setPrograma(programa);
        }

        return promocaoMapper.toResponseDTO(promocaoRepository.save(promocao));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void apagarPromocao(Long id) {
        if (!promocaoRepository.existsById(id)) {
            throw new RuntimeException("promoção não encontrado");
        }
        promocaoRepository.deleteById(id);
    }

}
