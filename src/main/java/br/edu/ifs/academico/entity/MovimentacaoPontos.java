package br.edu.ifs.academico.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb_movimentacao")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacaoPontos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimentacoes")
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private boolean creditada = false;

    private BigDecimal valor;
    private Integer pontos_calculados;

    @Column(name = "data_ocorrencia")
    private LocalDate dataOcorrencia;

    @PrePersist
    public void prePersist() {
        if (this.dataOcorrencia == null) {
            this.dataOcorrencia = LocalDate.now();
        }
    }

    @OneToMany(mappedBy = "movimentacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comprovante> comprovantes;

    @OneToOne(mappedBy = "movimentacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private StatusMovimentacao status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_saldo")
    private SaldoUsuarioPrograma saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cartao")
    private CartaoUsuario cartao;
}
