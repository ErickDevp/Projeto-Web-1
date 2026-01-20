package br.edu.ifs.academico.entity;

import br.edu.ifs.academico.entity.enums.Valido;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tb_promocoes")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promocao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promocao")
    private Long id;

    private String titulo;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Double pontosPorReal;

    @Transient
    public Valido getValido() {
        return dataFim.isBefore(LocalDate.now())
                ? Valido.VENCIDO
                : Valido.ATIVO;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_programa")
    private ProgramaFidelidade programa;
}
