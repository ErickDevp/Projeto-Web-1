package br.edu.ifs.academico.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tb_cartao")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartaoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cartao")
    private Long id;

    private String nome;

    // Visa, MasterCard, Elo, etc. provavel sera enum
    private String bandeira;

    // crédito, débito, etc. provavel sera enum
    private String tipo;

    private Double pontos;

    @OneToMany(mappedBy = "cartao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimentacaoPontos> movimentacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_saldo", nullable = false)
    private SaldoUsuarioPrograma saldo;

}
