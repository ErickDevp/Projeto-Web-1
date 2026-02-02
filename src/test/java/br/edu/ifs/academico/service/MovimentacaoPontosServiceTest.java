package br.edu.ifs.academico.service;

import br.edu.ifs.academico.DTO.movimentacao.request.MovimentacaoRequestDTO;
import br.edu.ifs.academico.DTO.movimentacao.response.MovimentacaoResponseDTO;
import br.edu.ifs.academico.entity.*;
import br.edu.ifs.academico.entity.enums.Status;
import br.edu.ifs.academico.mapper.MovimentacaoMapper;
import br.edu.ifs.academico.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimentacaoPontosServiceTest {

    @Mock
    private MovimentacaoPontosRepository movimentacaoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private SaldoUsuarioProgramaRepository saldoRepository;
    @Mock
    private CartaoUsuarioRepository cartaoRepository;
    @Mock
    private MovimentacaoMapper movimentacaoMapper;
    @Mock
    private StatusMovimentacaoRepository statusRepository;

    @InjectMocks
    private MovimentacaoPontosService service;

    @Test
    @DisplayName("Deve calcular corretamente os pontos (ex: R$100 * 2.5 = 250)")
    void criarMovimentacao_DeveCalcularPontosCorretamente() {
        // Arrange
        String email = "test@user.com";
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail(email);

        CartaoUsuario cartao = new CartaoUsuario();
        cartao.setId(1L);
        cartao.setUsuario(usuario);
        cartao.setDataValidade(LocalDate.now().plusYears(1));

        ProgramaFidelidade programa = new ProgramaFidelidade();
        programa.setId(1L);
        
        Promocao promocao = new Promocao();
        promocao.setId(1L);
        promocao.setPontosPorReal(2.5);
        promocao.setDataFim(LocalDate.now().plusMonths(1));
        
        programa.setPromocoes(List.of(promocao));
        cartao.setProgramas(new HashSet<>(List.of(programa)));

        // Correção no construtor: cartaoId, programaId, promocaoId, valor, data
        MovimentacaoRequestDTO dto = new MovimentacaoRequestDTO(
                1L, 1L, 1L, BigDecimal.valueOf(100), LocalDate.now()
        );

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(cartaoRepository.findById(1L)).thenReturn(Optional.of(cartao));
        when(saldoRepository.findByUsuarioIdAndProgramaId(1L, 1L)).thenReturn(Optional.empty());
        when(saldoRepository.save(any(SaldoUsuarioPrograma.class))).thenAnswer(i -> i.getArgument(0));
        when(movimentacaoRepository.save(any(MovimentacaoPontos.class))).thenAnswer(i -> {
            MovimentacaoPontos m = i.getArgument(0);
            if(m.getStatus() != null) {
                m.getStatus().setId(1L); // Simulate ID generation
            }
            return m;
        });
        
        when(statusRepository.findById(any())).thenAnswer(i -> {
             StatusMovimentacao status = new StatusMovimentacao();
             status.setStatus(Status.PENDENTE);
             status.setMovimentacao(new MovimentacaoPontos());
             status.getMovimentacao().setSaldo(new SaldoUsuarioPrograma());
             status.getMovimentacao().getSaldo().setPontos(0);
             status.getMovimentacao().setPontos_calculados(250); // Avoid NPE
             return Optional.of(status);
        });
        
        // Mock response DTO
        when(movimentacaoMapper.toResponseDTO(any(MovimentacaoPontos.class))).thenReturn(new MovimentacaoResponseDTO(
            1L, BigDecimal.valueOf(100), 250, LocalDate.now(),
            1L, "Cartao Teste", 1L, "Programa Teste", null, Collections.emptyList()
        ));

        // Act
        service.criarMovimentacao(dto, email);

        // Assert
        verify(movimentacaoRepository, atLeastOnce()).save(argThat(mov -> 
            mov.getPontos_calculados() != null && mov.getPontos_calculados() == 250
        ));
    }

    @Test
    @DisplayName("Deve lançar erro se tentar criar movimentação com cartão vencido")
    void criarMovimentacao_DeveLancarErro_QuandoCartaoVencido() {
        // Arrange
        String email = "test@user.com";
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        CartaoUsuario cartao = new CartaoUsuario();
        cartao.setId(1L);
        cartao.setUsuario(usuario);
        cartao.setDataValidade(LocalDate.now().minusDays(1)); // Vencido

        MovimentacaoRequestDTO dto = new MovimentacaoRequestDTO(
                1L, 1L, 1L, BigDecimal.TEN, LocalDate.now()
        );

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(cartaoRepository.findById(1L)).thenReturn(Optional.of(cartao));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            service.criarMovimentacao(dto, email)
        );
        assertEquals("Cartão vencido", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve permitir movimentação em cartão de outro usuário")
    void criarMovimentacao_DeveLancarErro_QuandoCartaoDeOutroUsuario() {
         // Arrange
        String email = "test@user.com";
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);

        CartaoUsuario cartao = new CartaoUsuario();
        cartao.setId(1L);
        cartao.setUsuario(outroUsuario); // Outro usuário

        MovimentacaoRequestDTO dto = new MovimentacaoRequestDTO(
                1L, 1L, 1L, BigDecimal.TEN, LocalDate.now()
        );

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(cartaoRepository.findById(1L)).thenReturn(Optional.of(cartao));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> 
            service.criarMovimentacao(dto, email)
        );
    }
    
    @Test
    @DisplayName("Deve lançar erro quando a promoção estiver vencida")
    void criarMovimentacao_DeveLancarErro_QuandoPromocaoVencida() {
         // Arrange
        String email = "test@user.com";
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        
        CartaoUsuario cartao = new CartaoUsuario();
        cartao.setId(1L);
        cartao.setUsuario(usuario);
        cartao.setDataValidade(LocalDate.now().plusYears(1));

        ProgramaFidelidade programa = new ProgramaFidelidade();
        programa.setId(1L);
        
        Promocao promocao = new Promocao();
        promocao.setId(1L);
        promocao.setDataFim(LocalDate.now().minusDays(1)); // Vencida
        
        programa.setPromocoes(Collections.singletonList(promocao));
        cartao.setProgramas(new HashSet<>(Collections.singletonList(programa)));

        MovimentacaoRequestDTO dto = new MovimentacaoRequestDTO(
                1L, 1L, 1L, BigDecimal.TEN, LocalDate.now()
        );

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(cartaoRepository.findById(1L)).thenReturn(Optional.of(cartao));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            service.criarMovimentacao(dto, email)
        );
        assertEquals("Promoção vencida", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar saldo corretamente ao editar movimentação")
    void atualizarMovimentacao_DeveAtualizarSaldoCorretamente() {
        // Arrange
        String email = "test@user.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        SaldoUsuarioPrograma saldo = new SaldoUsuarioPrograma();
        saldo.setPontos(500); // 500 pontos iniciais
        
        MovimentacaoPontos movimentacao = new MovimentacaoPontos();
        movimentacao.setId(1L);
        movimentacao.setUsuario(usuario);
        movimentacao.setSaldo(saldo);
        movimentacao.setPontos_calculados(200); // Tinha 200 pontos
        movimentacao.setValor(BigDecimal.valueOf(100)); // Valor original

        // Cartao mock
        CartaoUsuario cartao = new CartaoUsuario();
        cartao.setId(1L);
        movimentacao.setCartao(cartao);

        when(movimentacaoRepository.findById(1L)).thenReturn(Optional.of(movimentacao));
        when(movimentacaoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(movimentacaoMapper.toResponseDTO(any())).thenReturn(new MovimentacaoResponseDTO(
            1L, BigDecimal.valueOf(300), 300, LocalDate.now(), 
            1L, "C", 1L, "P", null, List.of()
        ));

        // Novo valor: 300
        MovimentacaoRequestDTO updateDto = new MovimentacaoRequestDTO(
            null, null, null, BigDecimal.valueOf(300), null
        );

        // Act
        service.atualizarMovimentacao(updateDto, 1L, email);

        // Assert
        // Saldo esperado: 500 - 200 (antigo) + 300 (novo) = 600
        assertEquals(600, saldo.getPontos());
        assertEquals(300, movimentacao.getPontos_calculados());
    }

    @Test
    @DisplayName("Deve deduzir pontos do saldo ao apagar movimentação")
    void apagarMovimentacao_DeveDeduzirSaldo() {
        // Arrange
        String email = "test@user.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        SaldoUsuarioPrograma saldo = new SaldoUsuarioPrograma();
        saldo.setPontos(500); 

        MovimentacaoPontos movimentacao = new MovimentacaoPontos();
        movimentacao.setId(1L);
        movimentacao.setUsuario(usuario);
        movimentacao.setSaldo(saldo);
        movimentacao.setPontos_calculados(200); 

        when(movimentacaoRepository.findById(1L)).thenReturn(Optional.of(movimentacao));

        // Act
        service.apagarMovimentacao(1L, email);

        // Assert
        assertEquals(300, saldo.getPontos()); // 500 - 200 = 300
        verify(movimentacaoRepository).deleteById(1L);
    }
}
