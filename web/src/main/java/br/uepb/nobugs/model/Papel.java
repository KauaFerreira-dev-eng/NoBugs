package br.uepb.nobugs.model;

/** Papel do usuário no sistema (autorização: o que cada um pode fazer). */
public enum Papel {
    MORADOR,
    ORGAO,
    ADMIN;

    /** Quem pode reatribuir o órgão responsável de uma ocorrência. */
    public boolean podeReatribuirOrgao() {
        return this == ORGAO || this == ADMIN;
    }
}
