package br.edu.ifs.academico.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_cartao")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartao_id")
    private Long cartaoId;

    private String nomeCartao;

    // Visa, MasterCard, Elo, etc.
    private String bandeira;

    // crédito, débito, etc.
    private String tipo;

}
