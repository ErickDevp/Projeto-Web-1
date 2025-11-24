package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.SaldoUsuarioProgramaDTO;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.repository.ProgramaFidelidadeRepository;
import br.edu.ifs.academico.repository.SaldoUsuarioProgramaRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SaldoUsuarioProgramaService {

    private final SaldoUsuarioProgramaRepository saldoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramaFidelidadeRepository programaRepository;

    public SaldoUsuarioProgramaService(SaldoUsuarioProgramaRepository saldoRepository, UsuarioRepository usuarioRepository, ProgramaFidelidadeRepository programaRepository) {
        this.saldoRepository = saldoRepository;
        this.usuarioRepository = usuarioRepository;
        this.programaRepository = programaRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<SaldoUsuarioPrograma> buscarTodosSaldosUsuario(String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return saldoRepository.findByUsuarioId(usuario.getId());
    }

    /*
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void atualizarSaldo(SaldoUsuarioProgramaDTO saldoDTO, Long id, String username) {
        var saldo = saldoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saldo Programa não encontrado"));

        if (!saldo.getUsuario().getEmail().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        if(saldoDTO.pontos() != null) { saldo.setPontos(saldoDTO.pontos()); }
        if(saldoDTO.programaId() != null && programaRepository.existsById(saldoDTO.programaId())) {
            var programa = programaRepository.findById(saldoDTO.programaId())
                    .orElseThrow(() -> new RuntimeException("programaFidelidade não encontrada"));

            saldo.setPrograma(programa);
        }

        saldoRepository.save(saldo);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void apagarSaldo(Long id, String username) {
        var saldo = saldoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if (!saldo.getUsuario().getEmail().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        saldoRepository.deleteById(id);
    }
     */

}
