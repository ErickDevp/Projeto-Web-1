package br.edu.ifs.academico.service;

import br.edu.ifs.academico.repository.MovimentacaoPontosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimentacaoPontos {

    private final MovimentacaoPontosRepository movimentacaoRepository;

    public MovimentacaoPontos(MovimentacaoPontosRepository movimentacaoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<MovimentacaoPontos> buscarMovimentacoes() {
        return movimentacaoRepository.findAll();
    }

}
