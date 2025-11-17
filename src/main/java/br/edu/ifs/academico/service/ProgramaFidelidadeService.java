package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.ProgramaFidelidadeDTO;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.repository.ProgramaFidelidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ProgramaFidelidadeService {

    private final ProgramaFidelidadeRepository programaRepository;

    public ProgramaFidelidadeService(ProgramaFidelidadeRepository programaRepository) {
        this.programaRepository = programaRepository;
    }

    public List<ProgramaFidelidade> buscarProgramas() {
        return programaRepository.findAll();
    }

    public Optional<ProgramaFidelidade> buscarProgramaPorId(Long id) {
        return programaRepository.findById(id);
    }

    public Long salvarPrograma(ProgramaFidelidadeDTO programaDTO, Usuario usuarioLogado) {
        if (usuarioLogado.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente administradores podem criar notificações.");
        }

        ProgramaFidelidade entity = ProgramaFidelidade.builder()
                .nome(programaDTO.nome())
                .descricao(programaDTO.descricao())
                .build();

        return programaRepository.save(entity).getId();
    }

    public void atualizarPrograma(ProgramaFidelidadeDTO programaDTO, Long id) {
        var programa = programaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa não encontrado"));

        if(programaDTO.nome() != null) { programa.setNome(programaDTO.nome()); }
        if(programaDTO.descricao() != null) { programa.setDescricao(programaDTO.descricao()); }

        programaRepository.save(programa);
    }

    public void apagarPrograma(Long id) {
        if(!programaRepository.existsById(id)) {
            throw new RuntimeException("Programa não encontrado");
        }
        programaRepository.deleteById(id);
    }
}
