package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.relatorio.EvolucaoMensalDTO;
import br.edu.ifs.academico.DTO.relatorio.HistoricoMovimentacaoDTO;
import br.edu.ifs.academico.DTO.relatorio.PontosPorCartaoDTO;
import br.edu.ifs.academico.DTO.relatorio.RelatorioResponseDTO;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.repository.CartaoUsuarioRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// iText imports CORRETOS
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

@Service
@RequiredArgsConstructor
public class RelatorioService {

        private final MovimentacaoPontosRepository movRepo;
        private final CartaoUsuarioRepository cartaoRepo;
        private final UsuarioRepository usuarioRepository;

        public RelatorioResponseDTO gerarRelatorio(String emailLogado) {
                var usuario = usuarioRepository.findByEmail(emailLogado)
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

                // ------------------ Pontos por Cartão ------------------
                var cartoes = cartaoRepo.findByUsuarioId(usuario.getId());

                // ------------------ Histórico (buscar antes para reutilizar)
                // ------------------
                var movs = movRepo.findByUsuarioIdOrderByDataOcorrenciaDesc(usuario.getId());

                // ------------------ Saldo Global ------------------
                // CORREÇÃO DE INTEGRIDADE: Saldo calculado a partir das movimentações
                // CREDITADAS
                // Garante que saldo = soma(movimentações onde creditada == true)
                // Isso assegura rastreabilidade contábil (princípio da dupla entrada)
                long saldoGlobal = movs.stream()
                                .filter(MovimentacaoPontos::isCreditada)
                                .mapToLong(m -> m.getPontos_calculados() != null ? m.getPontos_calculados() : 0)
                                .sum();

                List<PontosPorCartaoDTO> pontosPorCartao = cartoes.stream().map(cartao -> {

                        // CORREÇÃO: Apenas movimentações CREDITADAS contam para o total por cartão
                        long total = movs.stream()
                                        .filter(m -> m.isCreditada())
                                        .filter(m -> m.getCartao().getId().equals(cartao.getId()))
                                        .mapToLong(m -> m.getPontos_calculados() != null ? m.getPontos_calculados() : 0)
                                        .sum();

                        return new PontosPorCartaoDTO(
                                        cartao.getId(),
                                        cartao.getNome(),
                                        total);

                }).toList();

                // ------------------ Histórico ------------------
                // Mostra TODAS as movimentações (PENDENTE, CREDITADO, CANCELADO) para
                // transparência
                List<HistoricoMovimentacaoDTO> historico = movs.stream()
                                .map(m -> new HistoricoMovimentacaoDTO(
                                                m.getId(),
                                                m.getSaldo().getPrograma().getNome(),
                                                m.getPontos_calculados(),
                                                m.getDataOcorrencia(),
                                                m.getStatus() != null ? m.getStatus().getStatus().name()
                                                                : "SEM_STATUS"))
                                .toList();

                // ------------------ Evolução Mensal ------------------
                // CORREÇÃO: Apenas movimentações CREDITADAS para consistência com saldoGlobal
                Map<YearMonth, Long> totalPorMes = movs.stream()
                                .filter(m -> m.isCreditada())
                                .filter(m -> m.getDataOcorrencia() != null)
                                .collect(Collectors.groupingBy(
                                                m -> YearMonth.from(m.getDataOcorrencia()),
                                                Collectors.summingLong(m -> m.getPontos_calculados() != null
                                                                ? m.getPontos_calculados()
                                                                : 0)));

                List<EvolucaoMensalDTO> evolucaoMensal = totalPorMes.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(e -> new EvolucaoMensalDTO(
                                                e.getKey().getYear(),
                                                e.getKey().getMonthValue(),
                                                e.getValue()))
                                .toList();

                return new RelatorioResponseDTO(
                                pontosPorCartao,
                                historico,
                                evolucaoMensal,
                                saldoGlobal,
                                0.0 // sem campo dataRecebimento
                );
        }

        // ============================================================
        // ======================== CSV ================================
        // ============================================================

        public byte[] gerarCsv(String emailLogado) {
                RelatorioResponseDTO rel = gerarRelatorio(emailLogado);

                StringBuilder sb = new StringBuilder();

                sb.append("=== PONTOS POR CARTÃO ===\n");
                sb.append("Cartão;Total Pontos\n");

                rel.pontosPorCartao().forEach(c -> {
                        sb.append(c.nomeCartao()).append(";")
                                        .append(c.totalPontos()).append("\n");
                });

                sb.append("\n=== HISTÓRICO ===\n");
                sb.append("ID;Programa;Pontos;Data;Status\n");

                rel.historico().forEach(h -> {
                        sb.append(h.movimentacaoId()).append(";")
                                        .append(h.programa()).append(";")
                                        .append(h.pontosCalculados()).append(";")
                                        .append(h.data()).append(";")
                                        .append(h.status()).append("\n");
                });

                sb.append("\n=== EVOLUÇÃO MENSAL ===\n");
                sb.append("Ano;Mes;Total Pontos\n");

                rel.evolucaoMensal().forEach(e -> {
                        sb.append(e.ano()).append(";")
                                        .append(e.mes()).append(";")
                                        .append(e.totalPontos()).append("\n");
                });

                sb.append("\n=== SALDO GLOBAL ===\n");
                sb.append("Total Pontos;\n");
                sb.append(rel.saldoGlobal()).append("\n");

                return sb.toString().getBytes();
        }

        // ============================================================
        // ========================= PDF ===============================
        // ============================================================

        public byte[] gerarPdf(String emailLogado) {
                try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PdfWriter writer = new PdfWriter(baos);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document doc = new Document(pdf);

                        RelatorioResponseDTO rel = gerarRelatorio(emailLogado);

                        // ------------------ Título ------------------
                        doc.add(new Paragraph("Relatório de Milhas")
                                        .setBold()
                                        .setFontSize(20)
                                        .setTextAlignment(TextAlignment.CENTER));

                        doc.add(new Paragraph("\n"));

                        // ------------------ PONTOS POR CARTÃO ------------------
                        doc.add(new Paragraph("Pontos por Cartão").setBold().setFontSize(14));

                        Table tabelaCartao = new Table(2);
                        tabelaCartao.addHeaderCell("Cartão");
                        tabelaCartao.addHeaderCell("Total Pontos");

                        rel.pontosPorCartao().forEach(c -> {
                                tabelaCartao.addCell(c.nomeCartao());
                                tabelaCartao.addCell(String.valueOf(c.totalPontos()));
                        });

                        doc.add(tabelaCartao);
                        doc.add(new Paragraph("\n"));

                        // ------------------ HISTÓRICO ------------------
                        doc.add(new Paragraph("Histórico de Movimentações").setBold().setFontSize(14));

                        Table tabelaHist = new Table(5);
                        tabelaHist.addHeaderCell("ID");
                        tabelaHist.addHeaderCell("Programa");
                        tabelaHist.addHeaderCell("Pontos");
                        tabelaHist.addHeaderCell("Data");
                        tabelaHist.addHeaderCell("Status");

                        rel.historico().forEach(h -> {
                                tabelaHist.addCell(String.valueOf(h.movimentacaoId()));
                                tabelaHist.addCell(h.programa());
                                tabelaHist.addCell(String.valueOf(h.pontosCalculados()));
                                tabelaHist.addCell(String.valueOf(h.data()));
                                tabelaHist.addCell(h.status());
                        });

                        doc.add(tabelaHist);

                        doc.add(new Paragraph("\n"));

                        // ------------------ EVOLUÇÃO MENSAL ------------------
                        doc.add(new Paragraph("Evolução Mensal de Pontos").setBold().setFontSize(14));

                        Table tabelaEvolucao = new Table(3);
                        tabelaEvolucao.addHeaderCell("Ano");
                        tabelaEvolucao.addHeaderCell("Mês");
                        tabelaEvolucao.addHeaderCell("Total Pontos");

                        rel.evolucaoMensal().forEach(e -> {
                                tabelaEvolucao.addCell(String.valueOf(e.ano()));
                                tabelaEvolucao.addCell(String.valueOf(e.mes()));
                                tabelaEvolucao.addCell(String.valueOf(e.totalPontos()));
                        });

                        doc.add(tabelaEvolucao);

                        doc.add(new Paragraph("\n"));

                        // ------------------ SALDO GLOBAL ------------------
                        doc.add(new Paragraph("Saldo Global").setBold().setFontSize(14));
                        doc.add(new Paragraph(String.valueOf(rel.saldoGlobal())));

                        doc.close();
                        return baos.toByteArray();

                } catch (Exception e) {
                        throw new RuntimeException("Erro ao gerar PDF", e);
                }
        }
}
