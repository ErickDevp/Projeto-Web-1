package br.edu.ifs.academico.entity;

import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tb_cartao")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartaoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cartoes")
    private Long id;

    private String nome;

    // Visa, MasterCard, Elo, etc.
    @Enumerated(EnumType.STRING)
    private Bandeira bandeira;

    // crédito, débito, etc.
    @Enumerated(EnumType.STRING)
    private TipoCartao tipo;

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
