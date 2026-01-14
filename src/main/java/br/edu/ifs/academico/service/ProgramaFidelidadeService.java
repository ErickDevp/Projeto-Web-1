package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.ProgramaFidelidadeDTO;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
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


    public ProgramaFidelidadeService(ProgramaFidelidadeRepository programaRepository, CartaoUsuarioRepository cartaoRepository) {
        this.programaRepository = programaRepository;
        this.cartaoRepository = cartaoRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<ProgramaFidelidade> buscarProgramas() {
        return programaRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public Long criarPrograma(ProgramaFidelidadeDTO programaDTO) {
        ProgramaFidelidade entity = ProgramaFidelidade.builder()
                .nome(programaDTO.nome())
                .descricao(programaDTO.descricao())
                .build();

        return programaRepository.save(entity).getId();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void atualizarPrograma(ProgramaFidelidadeDTO programaDTO, Long id) {
        var programa = programaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa não encontrado"));

        if(programaDTO.nome() != null) { programa.setNome(programaDTO.nome()); }
        if(programaDTO.descricao() != null) { programa.setDescricao(programaDTO.descricao()); }

        programaRepository.save(programa);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void apagarPrograma(Long id) {
        if(!programaRepository.existsById(id)) {
            throw new RuntimeException("Programa não encontrado");
        }

        if (cartaoRepository.existsByProgramas_Id(id)) {
            throw new IllegalStateException("Não pode apagar: há cartões vinculados.");
        }

        programaRepository.deleteById(id);
    }
}
