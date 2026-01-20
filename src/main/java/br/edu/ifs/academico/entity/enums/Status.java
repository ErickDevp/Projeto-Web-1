package br.edu.ifs.academico.entity.enums;

import java.util.List;
import java.util.Random;

public enum Status {

    PENDENTE(List.of(
            "Aguardando processamento",
            "Em análise pelo sistema",
            "Processamento iniciado"
    )),

    CREDITADO(List.of(
            "Crédito realizado com sucesso",
            "Pontos creditados corretamente",
            "Processamento concluído"
    )),

    CANCELADO(List.of(
            "Movimentação cancelada automaticamente",
            "Erro identificado no processamento",
            "Cancelado por regra do sistema"
    ));

    private final List<String> motivos;
    private static final Random RANDOM = new Random();

    Status(List<String> motivos) {
        this.motivos = motivos;
    }

    public String motivoAleatorio() {
        return motivos.get(RANDOM.nextInt(motivos.size()));
    }
}
