package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.ProgramaFidelidadeDTO;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.repository.ProgramaFidelidadeRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProgramaFidelidadeService {

    private final ProgramaFidelidadeRepository programaRepository;
    private final UsuarioRepository usuarioRepository;

    public ProgramaFidelidadeService(ProgramaFidelidadeRepository programaRepository, UsuarioRepository usuarioRepository) {
        this.programaRepository = programaRepository;
        this.usuarioRepository = usuarioRepository;
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

    @PreAuthorize("hasAuthority('ADMIN')")
    public void apagarPrograma(Long id) {
        if(!programaRepository.existsById(id)) {
            throw new RuntimeException("Programa não encontrado");
        }
        programaRepository.deleteById(id);
    }
}
