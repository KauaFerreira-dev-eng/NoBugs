package br.uepb.nobugs.model;

/** Órgão público responsável por tratar a ocorrência (RN01). Os nomes são provisórios: ajustem com a equipe. */
public enum Orgao {
    OBRAS("Obras"),
    LIMPEZA_URBANA("Limpeza urbana"),
    ILUMINACAO_PUBLICA("Iluminação pública"),
    SANEAMENTO("Saneamento"),
    TRIAGEM("Triagem (órgão a definir)");

    private final String nome;

    Orgao(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
