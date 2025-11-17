package br.edu.ifs.academico.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_notificacao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacoes")
    private Long id;

    private String titulo;
    private String mensagem;
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
