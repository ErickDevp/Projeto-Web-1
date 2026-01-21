package br.edu.ifs.academico.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_comprovantes")
@Builder
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

    @ManyToOne(fetch = FetchType.LAZY) // Indica um relacionamento muitos para um (muitos pedidos para um cliente)
    @JoinColumn(name = "id_movimentacao") // Especifica o nome da coluna da chave estrangeira no banco de dados
    private MovimentacaoPontos movimentacao; // Objeto que representa a entidade relacionada
}
