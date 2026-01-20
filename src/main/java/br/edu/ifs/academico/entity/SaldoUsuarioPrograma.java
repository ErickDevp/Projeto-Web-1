package br.edu.ifs.academico.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tb_saldos")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaldoUsuarioPrograma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_saldo")
    private Long id;

    private Integer pontos;

    @OneToMany(mappedBy = "saldo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimentacaoPontos> movimentacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_programa")
    private ProgramaFidelidade programa;
}
