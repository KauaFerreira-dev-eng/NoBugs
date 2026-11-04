package br.uepb.nobugs.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Regras de senha: de 8 a 64 caracteres, com pelo menos uma letra e um número. */
class CadastroRequestTest {

    private static Validator validator;

    @BeforeAll
    static void criarValidador() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Set<ConstraintViolation<CadastroRequest>> validar(String senha) {
        return validator.validate(new CadastroRequest("Ana", "ana@exemplo.com", senha));
    }

    @Test
    void aceitaSenhaComOitoCaracteresLetraENumero() {
        assertTrue(validar("abcdefg1").isEmpty());
    }

    @Test
    void rejeitaSenhaComMenosDeOitoCaracteres() {
        assertEquals(1, validar("abc123").size());
    }

    @Test
    void rejeitaSenhaSemNumero() {
        assertEquals(1, validar("somenteLetras").size());
    }

    @Test
    void rejeitaSenhaSemLetra() {
        assertEquals(1, validar("12345678").size());
    }

    @Test
    void rejeitaSenhaComMaisDe64Caracteres() {
        assertEquals(1, validar("a1".repeat(33)).size());
    }
}
