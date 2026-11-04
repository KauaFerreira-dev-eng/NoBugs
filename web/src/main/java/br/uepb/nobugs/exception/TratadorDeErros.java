package br.uepb.nobugs.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Único lugar que traduz exceções em respostas HTTP. Nenhum try-catch espalhado pelos endpoints. */
@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(erro -> campos.put(erro.getField(), erro.getDefaultMessage()));
        return resposta(HttpStatus.BAD_REQUEST, new ErroResponse("VALIDACAO", "Dados inválidos", campos));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> jsonInvalido(HttpMessageNotReadableException ex) {
        return resposta(HttpStatus.BAD_REQUEST,
                ErroResponse.de("CORPO_INVALIDO", "Corpo da requisição inválido. Confira o JSON e a categoria."));
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> regraDeNegocio(RegraDeNegocioException ex) {
        return resposta(HttpStatus.CONFLICT, ErroResponse.de("REGRA_DE_NEGOCIO", ex.getMessage()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException ex) {
        return resposta(HttpStatus.NOT_FOUND, ErroResponse.de("NAO_ENCONTRADO", ex.getMessage()));
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroResponse> acessoNegado(AcessoNegadoException ex) {
        return resposta(HttpStatus.FORBIDDEN, ErroResponse.de("SEM_PERMISSAO", ex.getMessage()));
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResponse> credenciais(CredenciaisInvalidasException ex) {
        return resposta(HttpStatus.UNAUTHORIZED, ErroResponse.de("CREDENCIAIS_INVALIDAS", ex.getMessage()));
    }

    /** Conflito no banco (ex.: dois cadastros com o mesmo e-mail ao mesmo tempo). A mensagem do banco NÃO é devolvida. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> integridade(DataIntegrityViolationException ex) {
        return resposta(HttpStatus.CONFLICT,
                ErroResponse.de("CONFLITO", "Os dados enviados entram em conflito com algo que já existe."));
    }

    private ResponseEntity<ErroResponse> resposta(HttpStatus status, ErroResponse corpo) {
        return ResponseEntity.status(status).body(corpo);
    }
}
