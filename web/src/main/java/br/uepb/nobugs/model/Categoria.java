package br.uepb.nobugs.model;

/**
 * Lista fixa de categorias de problema urbano (card "Classificação por categoria").
 * Cada categoria já sabe qual órgão cuida dela (RN01).
 */
public enum Categoria {
    BURACO("Buraco na via", Orgao.OBRAS),
    ILUMINACAO("Iluminação pública", Orgao.ILUMINACAO_PUBLICA),
    LIXO("Acúmulo de lixo", Orgao.LIMPEZA_URBANA),
    SANEAMENTO("Saneamento", Orgao.SANEAMENTO),
    CALCADA("Calçada danificada", Orgao.OBRAS),
    PAVIMENTACAO("Falta de pavimentação", Orgao.OBRAS),
    OUTROS("Outros", Orgao.TRIAGEM);

    private final String nome;
    private final Orgao orgao;

    Categoria(String nome, Orgao orgao) {
        this.nome = nome;
        this.orgao = orgao;
    }

    public String getNome() {
        return nome;
    }

    public Orgao getOrgao() {
        return orgao;
    }
}
