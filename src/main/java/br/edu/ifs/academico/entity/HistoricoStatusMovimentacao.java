package br.edu.ifs.academico.entity;

import br.edu.ifs.academico.entity.enums.StatusMovimentacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_historicos")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoStatusMovimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico")
    private Long id;

    private StatusMovimentacao status;
    private String motivo;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_movimentacao", nullable = false)
    private MovimentacaoPontos movimentacao;
}
