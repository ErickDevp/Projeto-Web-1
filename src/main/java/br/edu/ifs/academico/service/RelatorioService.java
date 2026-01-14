package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.relatorio.HistoricoMovimentacaoDTO;
import br.edu.ifs.academico.DTO.relatorio.PontosPorCartaoDTO;
import br.edu.ifs.academico.DTO.relatorio.RelatorioResponseDTO;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.repository.CartaoUsuarioRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import br.edu.ifs.academico.repository.SaldoUsuarioProgramaRepository;
import br.edu.ifs.academico.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

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
    private final SaldoUsuarioProgramaRepository saldoRepo;
    private final CartaoUsuarioRepository cartaoRepo;
    private final UsuarioRepository usuarioRepository;


    public RelatorioResponseDTO gerarRelatorio(String emailLogado) {
        var usuario = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // ------------------ Pontos por Cartão ------------------
        var cartoes = cartaoRepo.findByUsuarioId(usuario.getId());
        var saldos = saldoRepo.findByUsuarioId(usuario.getId());

        List<PontosPorCartaoDTO> pontosPorCartao = cartoes.stream().map(cartao -> {

            long total = saldos.stream()
                    .flatMap(s -> s.getMovimentacao().stream())
                    .filter(m -> m.getCartao().getId().equals(cartao.getId()))
                    .mapToLong(MovimentacaoPontos::getPontos_calculados)
                    .sum();

            return new PontosPorCartaoDTO(
                    cartao.getId(),
                    cartao.getNome(),
                    total
            );

        }).toList();


        // ------------------ Histórico ------------------
        var movs = movRepo.findByUsuarioIdOrderByDataOcorrenciaDesc(usuario.getId());

        List<HistoricoMovimentacaoDTO> historico = movs.stream()
                .map(m -> new HistoricoMovimentacaoDTO(
                        m.getId(),
                        m.getSaldo().getPrograma().getNome(),
                        m.getPontos_calculados(),
                        m.getDataOcorrencia(),
                        m.getStatus() != null ? m.getStatus().getStatus().name() : "SEM_STATUS"
                ))
                .toList();

        return new RelatorioResponseDTO(
                pontosPorCartao,
                historico,
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
                    .setTextAlignment(TextAlignment.CENTER)
            );

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

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
}

