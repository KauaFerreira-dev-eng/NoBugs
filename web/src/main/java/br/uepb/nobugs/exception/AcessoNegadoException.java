package br.uepb.nobugs.exception;

/** Quem fez a requisição não tem permissão para a ação (vira resposta 403). */
public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}