package br.uepb.nobugs.dto;

import br.uepb.nobugs.model.Categoria;
import br.uepb.nobugs.model.Ocorrencia;
import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.model.Risco;
import br.uepb.nobugs.model.Status;
import java.time.LocalDateTime;

/** Dados que o sistema devolve sobre uma ocorrência. */
public record OcorrenciaResponse(
        Long id,
        String protocolo,
        String descricao,
        Categoria categoria,
        Risco risco,
        Orgao orgao,
        String endereco,
        Double latitude,
        Double longitude,
        Status status,
        LocalDateTime criadoEm) {

    public static OcorrenciaResponse from(Ocorrencia o) {
        return new OcorrenciaResponse(o.getId(), o.getProtocolo(), o.getDescricao(), o.getCategoria(),
                o.getRisco(), o.getOrgao(), o.getEndereco(), o.getLatitude(), o.getLongitude(),
                o.getStatus(), o.getCriadoEm());
    }
}
