package br.edu.ifs.academico.entity;

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
    private List<CartaoUsuario> cartao;

    @OneToMany(mappedBy = "saldo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimentacaoPontos> movimentacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_programa", nullable = false)
    private ProgramaFidelidade programa;
}
