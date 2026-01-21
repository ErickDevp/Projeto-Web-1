package br.edu.ifs.academico.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_notificacao_usuario", uniqueConstraints = @UniqueConstraint(columnNames = { "id_usuario",
        "id_notificacao" }))
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao_usuario")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_notificacao", nullable = false)
    private Notificacao notificacao;

    @Column(nullable = false)
    private boolean lida;

    @Column(nullable = false)
    private boolean oculta;
}