package br.uepb.nobugs.exception;

/** Lançada pelo Service quando algo pedido não existe (vira HTTP 404). */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
