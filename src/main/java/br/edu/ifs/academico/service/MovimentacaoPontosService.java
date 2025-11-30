package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.MovimentacaoPontosDTO;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.repository.CartaoUsuarioRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import br.edu.ifs.academico.repository.SaldoUsuarioProgramaRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.util.spi.ToolProvider.findFirst;

@Service
public class MovimentacaoPontosService {

    private final MovimentacaoPontosRepository movimentacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SaldoUsuarioProgramaRepository saldoRepository;
    private final CartaoUsuarioRepository cartaoRepository;

    public MovimentacaoPontosService(MovimentacaoPontosRepository movimentacaoRepository, UsuarioRepository usuarioRepository, SaldoUsuarioProgramaRepository saldoRepository, CartaoUsuarioRepository cartaoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.saldoRepository = saldoRepository;
        this.cartaoRepository = cartaoRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<MovimentacaoPontos> buscarTodasMovimentacoes(String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return movimentacaoRepository.findByUsuarioId(usuario.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Long criarMovimentacao(MovimentacaoPontosDTO dto, String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var cartao = cartaoRepository.findById(dto.cartaoId())
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        // cartão pertence ao usuário logado?
        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não tem permissão para registrar movimentações neste cartão");
        }

        var programa = cartao.getProgramas()
                .stream()
                .filter(p -> p.getId().equals(dto.programaId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Programa não encontrado nesse cartão"));

        var saldo = saldoRepository.findByUsuarioIdAndProgramaId(usuario.getId(), programa.getId())
                .orElseGet(() -> {
                    var novoSaldo = SaldoUsuarioPrograma.builder()
                            .usuario(usuario)
                            .programa(programa)
                            .pontos(0)
                            .build();
                    return saldoRepository.save(novoSaldo);
                });

        int pontosCalculados = dto.valor()
                .multiply(BigDecimal.valueOf(cartao.getPontos()))
                .setScale(0, RoundingMode.DOWN) // ou HALF_UP
                .intValue();

        MovimentacaoPontos entity = MovimentacaoPontos.builder()
                .usuario(usuario)
                .saldo(saldo)
                .cartao(cartao)
                .valor(dto.valor())
                .pontos_calculados(pontosCalculados)
                .build();

        saldo.setPontos(saldo.getPontos() + entity.getPontos_calculados());
        saldoRepository.save(saldo);

        return movimentacaoRepository.save(entity).getId();
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void atualizarMovimentacao(MovimentacaoPontosDTO dto, Long id, String username) {

        var movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));

        if (!movimentacao.getUsuario().getEmail().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        var saldo = movimentacao.getSaldo();
        var cartao = movimentacao.getCartao();

        // Atualizar o valor da movimentação
        if (dto.valor() != null) {
            // REMOVER do saldo os pontos antigos
            saldo.setPontos(saldo.getPontos() - movimentacao.getPontos_calculados());

            movimentacao.setValor(dto.valor());

            // Recalcular pontos
            int novosPontos = dto.valor()
                    .multiply(BigDecimal.valueOf(cartao.getPontos()))
                    .setScale(0, RoundingMode.DOWN)
                    .intValue();

            movimentacao.setPontos_calculados(novosPontos);

            saldo.setPontos(saldo.getPontos() + novosPontos);
        }

        saldoRepository.save(saldo);
        movimentacaoRepository.save(movimentacao);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void apagarMovimentacao(Long id, String username) {
        var movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("movimentação não encontrado"));

        if (!movimentacao.getUsuario().getEmail().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
        }

        var saldo = movimentacao.getSaldo();
        saldo.setPontos(saldo.getPontos() - movimentacao.getPontos_calculados());

        movimentacaoRepository.deleteById(id);
    }

}
