package br.uepb.nobugs.exception;

import java.util.Map;

/**
 * Formato único de erro da API:
 * codigo (para a tela tratar), mensagem (curta, para quem usa) e campos (só em erro de validação).
 * Nunca vai stack trace, nome de tabela ou mensagem do banco aqui.
 */
public record ErroResponse(String codigo, String mensagem, Map<String, String> campos) {

    public static ErroResponse de(String codigo, String mensagem) {
        return new ErroResponse(codigo, mensagem, Map.of());
    }
}
