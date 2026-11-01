package br.uepb.nobugs.repository;

import br.uepb.nobugs.model.HistoricoReatribuicao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoReatribuicaoRepository extends JpaRepository<HistoricoReatribuicao, Long> {

    /** Troca mais recente primeiro. */
    List<HistoricoReatribuicao> findByOcorrenciaIdOrderByDataHoraDesc(Long ocorrenciaId);
}