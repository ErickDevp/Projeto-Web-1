package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.movimentacao.request.MovimentacaoRequestDTO;
import br.edu.ifs.academico.DTO.movimentacao.response.MovimentacaoResponseDTO;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.entity.StatusMovimentacao;
import br.edu.ifs.academico.entity.enums.Status;
import br.edu.ifs.academico.mapper.MovimentacaoMapper;
import br.edu.ifs.academico.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@SuppressWarnings("null")
public class MovimentacaoPontosService {

        private final MovimentacaoPontosRepository movimentacaoRepository;
        private final UsuarioRepository usuarioRepository;
        private final SaldoUsuarioProgramaRepository saldoRepository;
        private final CartaoUsuarioRepository cartaoRepository;
        private final MovimentacaoMapper movimentacaoMapper;
        private final StatusMovimentacaoRepository statusRepository;

        public MovimentacaoPontosService(MovimentacaoPontosRepository movimentacaoRepository,
                                         UsuarioRepository usuarioRepository, SaldoUsuarioProgramaRepository saldoRepository,
                                         CartaoUsuarioRepository cartaoRepository, MovimentacaoMapper movimentacaoMapper, StatusMovimentacaoRepository statusRepository) {
                this.movimentacaoRepository = movimentacaoRepository;
                this.usuarioRepository = usuarioRepository;
                this.saldoRepository = saldoRepository;
                this.cartaoRepository = cartaoRepository;
            this.movimentacaoMapper = movimentacaoMapper;
            this.statusRepository = statusRepository;
        }

        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public List<MovimentacaoResponseDTO> buscarTodasMovimentacoes(String emailLogado) {
                var usuario = usuarioRepository.findByEmail(emailLogado)
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

                return movimentacaoRepository.findByUsuarioIdWithDetails(usuario.getId())
                                .stream()
                                .map(movimentacaoMapper::toResponseDTO)
                                .toList();
        }

        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public MovimentacaoResponseDTO atualizarMovimentacao(MovimentacaoRequestDTO movimentacaoRequestDTO, Long id, String username) {

                var movimentacao = movimentacaoRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));

                if (!movimentacao.getUsuario().getEmail().equals(username)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não autorizado");
                }

                var saldo = movimentacao.getSaldo();
                var cartao = movimentacao.getCartao();

                // Atualizar o valor da movimentação
                if (movimentacaoRequestDTO.valor() != null) {
                        // REMOVER do saldo os pontos antigos
                        saldo.setPontos(saldo.getPontos() - movimentacao.getPontos_calculados());

                        movimentacao.setValor(movimentacaoRequestDTO.valor());

                        // Recalcular pontos
                        int novosPontos = movimentacaoRequestDTO.valor()
                                        //.multiply(BigDecimal.valueOf(cartao.getMultiplicadorPontos()))
                                        .setScale(0, RoundingMode.DOWN)
                                        .intValue();

                        movimentacao.setPontos_calculados(novosPontos);

                        saldo.setPontos(saldo.getPontos() + novosPontos);
                }

                saldoRepository.save(saldo);
                return movimentacaoMapper.toResponseDTO(movimentacaoRepository.save(movimentacao));
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

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public MovimentacaoResponseDTO criarMovimentacao(MovimentacaoRequestDTO movimentacaoRequestDTO, String emailLogado) {

        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var cartao = cartaoRepository.findById(movimentacaoRequestDTO.cartaoId())
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para registrar movimentações neste cartão"
            );
        }

        if (cartao.getDataValidade().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cartão vencido");
        }

        var programa = cartao.getProgramas()
                .stream()
                .filter(p -> p.getId().equals(movimentacaoRequestDTO.programaId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Programa não encontrado nesse cartão"));

        var promocao = programa.getPromocoes()
                .stream()
                .filter(p -> p.getId().equals(movimentacaoRequestDTO.promocaoId()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Promoção não encontrada para este programa")
                );

        if (promocao.getDataFim().isBefore(LocalDate.now())) {
            throw new RuntimeException("Promoção vencida");
        }

        var saldo = saldoRepository.findByUsuarioIdAndProgramaId(usuario.getId(), programa.getId())
                .orElseGet(() -> saldoRepository.save(
                        SaldoUsuarioPrograma.builder()
                                .usuario(usuario)
                                .programa(programa)
                                .pontos(0)
                                .build()
                ));

        int pontosCalculados = movimentacaoRequestDTO.valor()
                .multiply(BigDecimal.valueOf(promocao.getPontosPorReal()))
                .setScale(0, RoundingMode.DOWN)
                .intValue();

        StatusMovimentacao status = StatusMovimentacao.builder()
                .status(Status.PENDENTE)
                .motivo(Status.PENDENTE.motivoAleatorio())
                .build();

        MovimentacaoPontos movimentacao = MovimentacaoPontos.builder()
                .usuario(usuario)
                .saldo(saldo)
                .cartao(cartao)
                .valor(movimentacaoRequestDTO.valor())
                .pontos_calculados(pontosCalculados)
                .status(status)
                .dataOcorrencia(movimentacaoRequestDTO.data())
                .build();

        status.setMovimentacao(movimentacao);

        movimentacaoRepository.save(movimentacao);

        processar(status.getId());

        return movimentacaoMapper.toResponseDTO(movimentacao);
    }

    @Async
    @Transactional
    public void processar(Long statusId) {

        var statusEntity = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status não encontrado"));

        if (statusEntity.getStatus() != Status.PENDENTE) {
            return;
        }

        try {
            int tempo = 10000 + new Random().nextInt(36000);
            Thread.sleep(tempo);

            Status resultado = decidirStatusFinal();

            statusEntity.setStatus(resultado);
            statusEntity.setMotivo(resultado.motivoAleatorio());

            if (resultado == Status.CREDITADO) {
                creditarSaldo(statusEntity.getMovimentacao());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Status decidirStatusFinal() {
        return new Random().nextInt(100) < 70
                ? Status.CREDITADO
                : Status.CANCELADO;
    }

    private void creditarSaldo(MovimentacaoPontos movimentacao) {

        if (movimentacao.isCreditada()) {
            return;
        }

        var saldo = movimentacao.getSaldo();
        saldo.setPontos(saldo.getPontos() + movimentacao.getPontos_calculados());

        movimentacao.setCreditada(true);

        saldoRepository.save(saldo);
        movimentacaoRepository.save(movimentacao);
    }

}
