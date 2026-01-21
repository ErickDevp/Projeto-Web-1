package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.programa.request.ProgramaRequestDTO;
import br.edu.ifs.academico.DTO.programa.response.ProgramaComPromocoesResponseDTO;
import br.edu.ifs.academico.DTO.programa.response.ProgramaResponseDTO;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.mapper.ProgramaMapper;
import br.edu.ifs.academico.repository.CartaoUsuarioRepository;
import br.edu.ifs.academico.repository.ProgramaFidelidadeRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@SuppressWarnings("null")
public class ProgramaFidelidadeService {

    private final ProgramaFidelidadeRepository programaRepository;
    private final CartaoUsuarioRepository cartaoRepository;
    private final ProgramaMapper programaMapper;

    public ProgramaFidelidadeService(ProgramaFidelidadeRepository programaRepository,
                                     CartaoUsuarioRepository cartaoRepository, ProgramaMapper programaMapper) {
        this.programaRepository = programaRepository;
        this.cartaoRepository = cartaoRepository;
        this.programaMapper = programaMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<ProgramaComPromocoesResponseDTO> buscarProgramas() {

        return programaRepository.findAll()
                .stream()
                .map(programaMapper::toProgramaPromocaoResponseDTO)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public ProgramaResponseDTO criarPrograma(ProgramaRequestDTO programaRequestDTO) {
        ProgramaFidelidade entity = ProgramaFidelidade.builder()
                .nome(programaRequestDTO.nome())
                .descricao(programaRequestDTO.descricao())
                .build();

        return programaMapper.toResponseDTO(programaRepository.save(entity));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public ProgramaResponseDTO atualizarPrograma(ProgramaRequestDTO programaRequestDTO, Long id) {
        var programa = programaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa não encontrado"));

        if (programaRequestDTO.nome() != null) {
            programa.setNome(programaRequestDTO.nome());
        }
        if (programaRequestDTO.descricao() != null) {
            programa.setDescricao(programaRequestDTO.descricao());
        }

        return programaMapper.toResponseDTO(programaRepository.save(programa));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void apagarPrograma(Long id) {
        if (!programaRepository.existsById(id)) {
            throw new RuntimeException("Programa não encontrado");
        }

        if (cartaoRepository.existsByProgramas_Id(id)) {
            throw new IllegalStateException("Não pode apagar: há cartões vinculados.");
        }

        programaRepository.deleteById(id);
    }
}
