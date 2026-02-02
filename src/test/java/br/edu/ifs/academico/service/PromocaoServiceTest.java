package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.promocao.request.PromocaoRequestDTO;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.repository.ProgramaFidelidadeRepository;
import br.edu.ifs.academico.repository.PromocaoRepository;
import br.edu.ifs.academico.mapper.PromocaoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromocaoServiceTest {

    @Mock
    private PromocaoRepository promocaoRepository;
    @Mock
    private ProgramaFidelidadeRepository programaRepository;
    @Mock
    private PromocaoMapper promocaoMapper;

    @InjectMocks
    private PromocaoService service;

    @Test
    @DisplayName("Deve impedir criação de promoção onde a data fim é anterior à data início")
    void salvar_DeveLancarErro_QuandoDataFimAntesDeInicio() {
        // Arrange
        LocalDate inicio = LocalDate.now().plusDays(2);
        LocalDate fim = LocalDate.now().plusDays(1); // Fim antes do inicio

        PromocaoRequestDTO dto = new PromocaoRequestDTO(
                "Promo", "Desc", inicio, fim, 1L, 1.0
        );

        when(programaRepository.findById(1L)).thenReturn(Optional.of(new ProgramaFidelidade()));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            service.criarPromocao(dto)
        );
        assertEquals("A data de fim não pode ser anterior à data de início", exception.getMessage());
    }
}
