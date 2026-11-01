package br.uepb.nobugs.model;

/**
 * Perfil de quem está usando o sistema. Ainda não existe login: por enquanto o perfil chega no
 * cabeçalho X-Perfil da requisição. Quando o login for implementado, o perfil passa a vir do usuário logado.
 */
public enum PerfilUsuario {
    MORADOR,
    ORGAO_PUBLICO,
    ADMINISTRADOR;

    /** Converte o texto do cabeçalho em perfil. Devolve null se vier vazio ou inválido. */
    public static PerfilUsuario deTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** Quem pode reatribuir o órgão responsável de uma ocorrência. */
    public boolean podeReatribuirOrgao() {
        return this == ORGAO_PUBLICO || this == ADMINISTRADOR;
    }
}