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

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
class RelatorioControllerIT extends IntegrationTestSupport {

    @Test
    void shouldGenerateRelatorioCsvAndPdf() throws Exception {
        Usuario user = createUser("user12@teste.com", Role.USER, DEFAULT_PASSWORD);
        String token = loginAndGetToken(user.getEmail(), DEFAULT_PASSWORD);

        ProgramaFidelidade programa = programaFidelidadeRepository.save(ProgramaFidelidade.builder()
                .nome("Programa Rel")
                .descricao("Descricao")
                .build());

        CartaoUsuario cartao = cartaoUsuarioRepository.save(CartaoUsuario.builder()
                .usuario(user)
                .nome("Cartao Rel")
                .bandeira(Bandeira.VISA)
                .tipo(TipoCartao.CREDITO)
                //.multiplicadorPontos(2.0)
                .programas(Set.of(programa))
                .build());

        SaldoUsuarioPrograma saldo = saldoUsuarioProgramaRepository.save(SaldoUsuarioPrograma.builder()
                .usuario(user)
                .programa(programa)
                .pontos(0)
                .build());

        movimentacaoPontosRepository.save(MovimentacaoPontos.builder()
                .usuario(user)
                .cartao(cartao)
                .saldo(saldo)
                .valor(new BigDecimal("100.00"))
                .pontos_calculados(200)
                .build());

        mockMvc.perform(get("/relatorios")
                        .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoGlobal").value(0));

        mockMvc.perform(get("/relatorios/csv")
                        .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv; charset=UTF-8"));

        mockMvc.perform(get("/relatorios/pdf")
                        .with(bearerToken(token)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
    }
}