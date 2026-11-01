package br.uepb.nobugs.exception;

/** Lançada pelo Service quando uma regra de negócio é violada (vira HTTP 409). */
public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
