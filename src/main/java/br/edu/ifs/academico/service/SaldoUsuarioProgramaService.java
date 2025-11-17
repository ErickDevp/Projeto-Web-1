package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.SaldoUsuarioProgramaDTO;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.repository.SaldoUsuarioProgramaRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SaldoUsuarioProgramaService {

    private final SaldoUsuarioProgramaRepository saldoRepository;
    private final UsuarioRepository usuarioRepository;

    public SaldoUsuarioProgramaService(SaldoUsuarioProgramaRepository saldoRepository, UsuarioRepository usuarioRepository) {
        this.saldoRepository = saldoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<SaldoUsuarioPrograma> buscarSaldosUsuario() {
        return saldoRepository.findAll();
    }

    public Optional<SaldoUsuarioPrograma> buscarSaldoPorId(Long id) {
        return saldoRepository.findById(id);
    }

    public Long salvarSaldoUsuario(SaldoUsuarioProgramaDTO saldoDTO) {
        var usuario = usuarioRepository.findById(saldoDTO.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        SaldoUsuarioPrograma entity = SaldoUsuarioPrograma.builder()
                .pontos(saldoDTO.pontos())
                .usuario(usuario)
                .build();

        var saldoSalvo = saldoRepository.save(entity);

        return saldoSalvo.getId();
    }

    public void atualizarSaldo(SaldoUsuarioProgramaDTO saldoDTO, Long id) {
        var saldo = saldoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saldo Programa não encontrado"));

        if(saldoDTO.pontos() != null) { saldo.setPontos(saldoDTO.pontos()); }

        saldoRepository.save(saldo);
    }

    public void apagarSaldo(Long id) {
        if(!saldoRepository.existsById(id)) {
            throw new RuntimeException("Saldo Programa não encontrado");
        }
        saldoRepository.deleteById(id);

    }


}
