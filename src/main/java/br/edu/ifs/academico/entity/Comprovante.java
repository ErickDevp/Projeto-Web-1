package br.edu.ifs.academico.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_comprovante")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comprovante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprovate")
    private Long id;

    private String caminho;
    private String tipo_arq;
    private Long tamanho_bytes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Indica um relacionamento muitos para um (muitos pedidos para um cliente)
    @JoinColumn(name = "id_movimentacao", nullable = false) // Especifica o nome da coluna da chave estrangeira no banco de dados
    private MovimentacaoPontos movimentacao; // Objeto que representa a entidade relacionada
}
