package br.uepb.nobugs.dto;

import br.uepb.nobugs.model.HistoricoReatribuicao;
import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.model.PerfilUsuario;
import java.time.LocalDateTime;

/** Uma troca de órgão já realizada: de qual para qual, por quem e quando. */
public record HistoricoReatribuicaoResponse(
        Orgao orgaoAnterior,
        Orgao orgaoNovo,
        String autor,
        PerfilUsuario perfilAutor,
        LocalDateTime dataHora) {

    public static HistoricoReatribuicaoResponse from(HistoricoReatribuicao h) {
        return new HistoricoReatribuicaoResponse(h.getOrgaoAnterior(), h.getOrgaoNovo(),
                h.getAutor(), h.getPerfilAutor(), h.getDataHora());
    }
}