package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.ComprovanteDTO;
import br.edu.ifs.academico.entity.Comprovante;
import br.edu.ifs.academico.repository.ComprovanteRepository;
import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ComprovanteService {

    private final ComprovanteRepository comprovanteRepository;
    private final MovimentacaoPontosRepository movimentacaoRepository;

    public ComprovanteService(ComprovanteRepository comprovanteRepository, MovimentacaoPontosRepository movimentacaoRepository) {
        this.comprovanteRepository = comprovanteRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Value("${comprovante.storage.path:uploads/comprovantes}")
    private String storagePath;

    // busco todos os comprovantes de determinada movimentacao
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Comprovante> buscarComprovantePorId(Long movimentacaoId, String emailLogado) {
        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioEmail(movimentacaoId, emailLogado)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada para este usuário"));

        return comprovanteRepository.findByMovimentacaoId(movimentacao.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Long criarComprovante(Long movimentacaoId, MultipartFile file, String emailLogado) {
        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioEmail(movimentacaoId, emailLogado)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada para este usuário"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Arquivo vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/png") || contentType.equals("image/jpeg") || contentType.equals("application/pdf"))) {
            throw new RuntimeException("Tipo de arquivo não suportado. Aceito: png, jpg, pdf");
        }

        try {
            Path base = Paths.get(storagePath).toAbsolutePath().normalize();
            Files.createDirectories(base);

            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            } else if (contentType.equals("image/png")) ext = ".png";
            else if (contentType.equals("image/jpeg")) ext = ".jpg";
            else if (contentType.equals("application/pdf")) ext = ".pdf";

            String filename = UUID.randomUUID().toString() + ext;
            Path target = base.resolve(filename);

            Files.copy(file.getInputStream(), target);

            Comprovante entity = Comprovante.builder()
                    .movimentacao(movimentacao)
                    .caminho(target.toString())
                    .tipo_arq(contentType)
                    .tamanho_bytes(file.getSize())
                    .build();

            return comprovanteRepository.save(entity).getId();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage(), e);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void atualizarComprovante(ComprovanteDTO dto, Long id, String emailLogado) {

        var comprovante = comprovanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprovante não encontrado"));

        if (!comprovante.getMovimentacao().getUsuario().getEmail().equals(emailLogado)) {
            throw new RuntimeException("Você não pode alterar comprovante de outro usuário");
        }

        if (dto.caminho() != null) comprovante.setCaminho(dto.caminho());
        if (dto.tipo_arq() != null) comprovante.setTipo_arq(dto.tipo_arq());
        if (dto.tamanho_bytes() != null) comprovante.setTamanho_bytes(dto.tamanho_bytes());

        // Se mover o comprovante para outra movimentação, validar também
        if (dto.movimentacaoId() != null) {
            var novaMov = movimentacaoRepository
                    .findByIdAndUsuarioEmail(dto.movimentacaoId(), emailLogado)
                    .orElseThrow(() -> new RuntimeException("Movimentação não pertence ao usuário"));

            comprovante.setMovimentacao(novaMov);
        }

        comprovanteRepository.save(comprovante);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void apagarComprovante(Long id, String emailLogado) {

        var comprovante = comprovanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprovante não encontrado"));

        if (!comprovante.getMovimentacao().getUsuario().getEmail().equals(emailLogado)) {
            throw new RuntimeException("Você não pode apagar comprovante de outro usuário");
        }

        comprovanteRepository.deleteById(id);
    }
}