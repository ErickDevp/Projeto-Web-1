package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.comprovante.response.ArquivoBytesResponseDTO;
import br.edu.ifs.academico.DTO.comprovante.response.ComprovanteResponseDTO;
import br.edu.ifs.academico.entity.Comprovante;
import br.edu.ifs.academico.mapper.ComprovanteMapper;
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
@SuppressWarnings("null")
public class ComprovanteService {

    private final ComprovanteRepository comprovanteRepository;
    private final MovimentacaoPontosRepository movimentacaoRepository;
    private final ComprovanteMapper comprovanteMapper;

    public ComprovanteService(ComprovanteRepository comprovanteRepository,
            MovimentacaoPontosRepository movimentacaoRepository, ComprovanteMapper comprovanteMapper) {
        this.comprovanteRepository = comprovanteRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.comprovanteMapper = comprovanteMapper;
    }

    @Value("${comprovante.storage.path:uploads/comprovantes}")
    private String storagePath;

    // busco todos os comprovantes de determinada movimentacao
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<ComprovanteResponseDTO> buscarComprovantePorId(Long movimentacaoId, String emailLogado) {
        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioEmail(movimentacaoId, emailLogado)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada para este usuário"));

        return comprovanteRepository.findByMovimentacaoId(movimentacao.getId())
                .stream()
                .map(comprovanteMapper::toResponseDTO)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ComprovanteResponseDTO criarComprovante(Long movimentacaoId, MultipartFile file, String emailLogado) {
        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioEmail(movimentacaoId, emailLogado)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada para este usuário"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Arquivo vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/png") || contentType.equals("image/jpeg")
                        || contentType.equals("application/pdf"))) {
            throw new RuntimeException("Tipo de arquivo não suportado. Aceito: png, jpg, pdf");
        }

        try {
            Path base = Paths.get(storagePath).toAbsolutePath().normalize();
            Files.createDirectories(base);

            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            } else if (contentType.equals("image/png"))
                ext = ".png";
            else if (contentType.equals("image/jpeg"))
                ext = ".jpg";
            else if (contentType.equals("application/pdf"))
                ext = ".pdf";

            String filename = UUID.randomUUID().toString() + ext;
            Path target = base.resolve(filename);

            Files.copy(file.getInputStream(), target);

            Comprovante entity = Comprovante.builder()
                    .movimentacao(movimentacao)
                    .caminho(target.toString())
                    .tipo_arq(contentType)
                    .tamanho_bytes(file.getSize())
                    .build();

            return comprovanteMapper.toResponseDTO(comprovanteRepository.save(entity));

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage(), e);
        }
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

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ArquivoBytesResponseDTO lerBytesComprovante(Long id, String emailLogado) {

        var comprovante = comprovanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprovante não encontrado"));

        if (!comprovante.getMovimentacao().getUsuario().getEmail().equals(emailLogado)) {
            throw new RuntimeException("Você não pode acessar comprovante de outro usuário");
        }

        try {
            Path path = Paths.get(comprovante.getCaminho());
            byte[] bytes = Files.readAllBytes(path);
            String contentType = comprovante.getTipo_arq();
            if (contentType == null) {
                contentType = Files.probeContentType(path);
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return new ArquivoBytesResponseDTO(bytes, contentType);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo: " + e.getMessage(), e);
        }
    }
}