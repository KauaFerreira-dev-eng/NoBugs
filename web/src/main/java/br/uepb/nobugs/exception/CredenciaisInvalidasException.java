package br.uepb.nobugs.exception;

/** Login falhou. A mensagem é sempre a mesma, sem dizer se o erro foi no e-mail ou na senha (vira HTTP 401). */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos");
    }
}
