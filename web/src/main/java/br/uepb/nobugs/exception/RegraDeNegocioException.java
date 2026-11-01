package br.uepb.nobugs.exception;

/** A ação viola uma regra do sistema (vira resposta 400). */
public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}