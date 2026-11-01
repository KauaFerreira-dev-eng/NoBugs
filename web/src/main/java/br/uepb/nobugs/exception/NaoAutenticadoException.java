package br.uepb.nobugs.exception;

/** A pessoa não está logada ou errou login/senha (vira resposta 401). */
public class NaoAutenticadoException extends RuntimeException {
    public NaoAutenticadoException(String mensagem) {
        super(mensagem);
    }
}