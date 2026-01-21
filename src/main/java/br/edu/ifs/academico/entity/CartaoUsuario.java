package br.edu.ifs.academico.entity;

import br.edu.ifs.academico.entity.enums.Bandeira;
import br.edu.ifs.academico.entity.enums.TipoCartao;
import br.edu.ifs.academico.entity.enums.Valido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_cartoes")
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

    private String numero;
    private LocalDate dataValidade;

    @Transient
    public Valido getValido() {
        return dataValidade.isBefore(LocalDate.now())
                ? Valido.VENCIDO
                : Valido.ATIVO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CartaoUsuario))
            return false;
        CartaoUsuario other = (CartaoUsuario) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @OneToMany(mappedBy = "cartao", orphanRemoval = true)
    private List<MovimentacaoPontos> movimentacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToMany
    @JoinTable(name = "cartao_usuario_programa", joinColumns = @JoinColumn(name = "cartao_id"), inverseJoinColumns = @JoinColumn(name = "programa_id"))
    @Builder.Default
    private Set<ProgramaFidelidade> programas = new HashSet<>();
}
