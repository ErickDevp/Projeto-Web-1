package br.edu.ifs.academico.mapper;

import br.edu.ifs.academico.DTO.cartao.response.CartaoResponseDTO;
import br.edu.ifs.academico.DTO.programa.response.ProgramaResponseDTO;
import br.edu.ifs.academico.entity.CartaoUsuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartaoMapper {

        public CartaoResponseDTO toResponseDTO(CartaoUsuario cartao) {

                List<ProgramaResponseDTO> programas = cartao.getProgramas() == null
                                ? List.of()
                                : cartao.getProgramas().stream()
                                                .map(pg -> new ProgramaResponseDTO(
                                                                pg.getId(),
                                                                pg.getNome(),
                                                                pg.getDescricao(),
                                                                pg.getCategoria()))
                                                .toList();

                return new CartaoResponseDTO(
                                cartao.getId(),
                                cartao.getNome(),
                                cartao.getBandeira(),
                                cartao.getTipo(),
                                cartao.getNumero(),
                                cartao.getDataValidade(),
                                cartao.getValido(),
                                programas);
        }
}
