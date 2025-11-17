package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.PromocaoDTO;
import br.edu.ifs.academico.entity.Promocao;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.repository.PromocaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;

    public PromocaoService(PromocaoRepository promocaoRepository) {
        this.promocaoRepository = promocaoRepository;
    }

    public List<Promocao> buscarPromocoes() {
        return promocaoRepository.findAll();
    }

    public Optional<Promocao> buscarPromocaoPorId(Long id) {
        return promocaoRepository.findById(id);
    }

    public Long salvarPromocao(PromocaoDTO promoDTO, Usuario usuarioLogado) {
        if (usuarioLogado.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente administradores podem criar notificações.");
        }

        Promocao entity = Promocao.builder()
                .titulo(promoDTO.titulo())
                .descricao(promoDTO.titulo())
                .data_inicio(promoDTO.data_inicio())
                .data_fim(promoDTO.data_fim())
                .ativo(promoDTO.ativo())
                .build();

        return promocaoRepository.save(entity).getId();
    }

    public void atualizarPromocao(PromocaoDTO promoDTO, Long id) {
        var promocao = promocaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoção não encontrado"));

        if(promoDTO.titulo() != null) { promocao.setTitulo(promoDTO.titulo()); }
        if(promoDTO.descricao() != null) { promocao.setDescricao(promoDTO.descricao()); }
        if(promoDTO.data_inicio() != null) { promocao.setData_inicio(promoDTO.data_inicio()); }
        if(promoDTO.data_fim() != null) { promocao.setData_fim(promoDTO.data_fim()); }
        if(promoDTO.ativo() != null) { promocao.setAtivo(promoDTO.ativo()); }

        promocaoRepository.save(promocao);
    }

    public void apagarPromocao(Long id) {
        if(!promocaoRepository.existsById(id)) {
            throw new RuntimeException("promoção não encontrado");
        }
        promocaoRepository.deleteById(id);
    }


}
