package br.edu.ifs.academico.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb_movimentacao")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacaoPontos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimentacoes")
    private Long id;

    private BigDecimal valor;
    private Integer pontos_calculados;
    private LocalDate data_ocorrencia;

    @OneToMany(mappedBy = "movimentacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comprovante> comprovantes;

    @OneToOne(mappedBy = "movimentacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private StatusMovimentacao status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @JsonIgnore
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_saldo")
    @JsonIgnore
    private SaldoUsuarioPrograma saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cartao")
    @JsonIgnore
    private CartaoUsuario cartao;

}
