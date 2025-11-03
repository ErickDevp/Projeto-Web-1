package br.edu.ifs.academico.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_historico")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoStatusMovimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico")
    private Long id;

    private String status_antigo;
    private String status_novo;
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_movimentacao", nullable = false)
    private MovimentacaoPontos movimentacao;
}
