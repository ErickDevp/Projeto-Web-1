package br.edu.ifs.academico.service;

import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.repository.SaldoUsuarioProgramaRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaldoUsuarioProgramaService {

    private final SaldoUsuarioProgramaRepository saldoRepository;
    private final UsuarioRepository usuarioRepository;

    public SaldoUsuarioProgramaService(SaldoUsuarioProgramaRepository saldoRepository,
            UsuarioRepository usuarioRepository) {
        this.saldoRepository = saldoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<SaldoUsuarioPrograma> buscarTodosSaldosUsuario(String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return saldoRepository.findByUsuarioId(usuario.getId());
    }

}
