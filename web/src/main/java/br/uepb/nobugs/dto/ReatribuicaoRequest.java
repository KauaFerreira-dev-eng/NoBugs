package br.uepb.nobugs.dto;

import br.uepb.nobugs.model.Orgao;
import jakarta.validation.constraints.NotNull;

/** Dados para trocar o órgão responsável. O órgão é um valor da lista cadastrada (enum Orgao), nunca texto livre. */
public record ReatribuicaoRequest(
        @NotNull(message = "O novo órgão é obrigatório")
        Orgao orgao) {
}