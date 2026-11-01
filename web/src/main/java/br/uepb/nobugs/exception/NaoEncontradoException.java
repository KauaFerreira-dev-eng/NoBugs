package br.uepb.nobugs.exception;

/** O registro pedido não existe (vira resposta 404). */
public class NaoEncontradoException extends RuntimeException {
    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}