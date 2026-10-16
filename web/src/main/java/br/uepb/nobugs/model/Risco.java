package br.uepb.nobugs.model;

/**
 * Nível de risco, definido pelo órgão responsável (RF04). Toda ocorrência nasce NAO_CLASSIFICADO.
 * A ordem importa: quanto maior a posição, maior o risco (usado na ordenação da lista).
 */
public enum Risco {
    NAO_CLASSIFICADO,
    BAIXO,
    MEDIO,
    ALTO
}
