package br.edu.ifs.academico.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_programas")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramaFidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_programa")
    private Long id;

    private String nome;
    private String descricao;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProgramaFidelidade)) return false;
        ProgramaFidelidade other = (ProgramaFidelidade) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @OneToMany(mappedBy = "programa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Promocao> promocoes;

    @OneToMany(mappedBy = "programa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaldoUsuarioPrograma> saldo;

    @ManyToMany(mappedBy = "programas", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<CartaoUsuario> cartoes = new HashSet<>();
}
