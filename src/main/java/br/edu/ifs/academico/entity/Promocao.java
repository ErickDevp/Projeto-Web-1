package br.edu.ifs.academico.entity;

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
    private LocalDate data_inicio;
    private LocalDate data_fim;
    private Boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_programa", nullable = false)
    private ProgramaFidelidade programa;
}
