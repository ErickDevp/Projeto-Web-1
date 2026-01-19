package br.edu.ifs.academico.integration;

import br.edu.ifs.academico.entity.CartaoUsuario;
import br.edu.ifs.academico.entity.MovimentacaoPontos;
import br.edu.ifs.academico.entity.ProgramaFidelidade;
import br.edu.ifs.academico.entity.SaldoUsuarioPrograma;
import br.edu.ifs.academico.entity.Usuario;
import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.Role;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class ComprovanteControllerIT extends IntegrationTestSupport {

    @Test
    void shouldCreateReadAndDeleteComprovante() throws Exception {
        Usuario user = createUser("user10@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        ProgramaFidelidade programa = Objects
                .requireNonNull(programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                        .nome("Programa D")
                        .descricao("Descricao")
                        .build()));

        CartaoUsuario cartao = Objects.requireNonNull(cartaoUsuarioRepository.save(CartaoUsuario.builder()
                .usuario(user)
                .nome("Cartao 4")
                .bandeira(Bandeira.ELO)
                .tipo(TipoCartao.DEBITO)
                .multiplicadorPontos(1.0)
                .programas(Set.of(programa))
                .build()));

        SaldoUsuarioPrograma saldo = Objects
                .requireNonNull(saldoUsuarioProgramaRepository.save(SaldoUsuarioPrograma.builder()
                        .usuario(user)
                        .programa(programa)
                        .pontos(0)
                        .build()));

        MovimentacaoPontos movimentacao = Objects
                .requireNonNull(movimentacaoPontosRepository.save(MovimentacaoPontos.builder()
                        .usuario(user)
                        .cartao(cartao)
                        .saldo(saldo)
                        .valor(new BigDecimal("20.00"))
                        .pontos_calculados(20)
                        .build()));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "comprovante.pdf",
                "application/pdf",
                "conteudo".getBytes());

        mockMvc.perform(multipart("/comprovante/criar")
                .file(file)
                .param("movimentacaoId", movimentacao.getId().toString())
                .with(bearerToken(token)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/comprovante/{id}", movimentacao.getId())
                .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        var comprovante = comprovanteRepository.findByMovimentacaoId(movimentacao.getId()).get(0);

        mockMvc.perform(get("/comprovante/{id}/arquivo", comprovante.getId())
                .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));

        mockMvc.perform(delete("/comprovante/{id}", comprovante.getId())
                .with(bearerToken(token)))
                .andExpect(status().isNoContent());
    }
}
