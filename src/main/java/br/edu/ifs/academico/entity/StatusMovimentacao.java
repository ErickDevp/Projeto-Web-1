package br.edu.ifs.academico.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_status")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusMovimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status")
    private Long id;

    private br.edu.ifs.academico.entity.enums.StatusMovimentacao status;
    private String motivo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_movimentacao")
    @JsonIgnore
    private MovimentacaoPontos movimentacao;
}
