package br.uepb.nobugs.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.uepb.nobugs.model.Categoria;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

//Testa os critérios de aceite de RF01, RF02, RF03 e RF09 sem precisar subir o sistema. 
class OcorrenciaRequestTest {

    private static Validator validator;

    @BeforeAll
    static void criarValidador() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Set<ConstraintViolation<OcorrenciaRequest>> validar(OcorrenciaRequest r) {
        return validator.validate(r);
    }

    @Test
    void aceitaOcorrenciaCompletaComEndereco() {
        var r = new OcorrenciaRequest("Buraco grande na rua", Categoria.BURACO, "Rua Eixo das Nações, 109", null, null);
        assertTrue(validar(r).isEmpty());
    }

    @Test
    void aceitaOcorrenciaSoComCoordenadas() {
        var r = new OcorrenciaRequest("Poste apagado", Categoria.ILUMINACAO, null, -7.23, -35.88);
        assertTrue(validar(r).isEmpty());
    }

    @Test
    void rejeitaDescricaoVazia() {
        var r = new OcorrenciaRequest("   ", Categoria.LIXO, "Rua A", null, null);
        assertEquals(1, validar(r).size());
    }

    @Test
    void rejeitaDescricaoComMaisDe500Caracteres() {
        var r = new OcorrenciaRequest("x".repeat(501), Categoria.LIXO, "Rua A", null, null);
        assertEquals(1, validar(r).size());
    }

    @Test
    void rejeitaSemCategoria() {
        var r = new OcorrenciaRequest("Lixo na calçada", null, "Rua A", null, null);
        assertEquals(1, validar(r).size());
    }

    @Test
    void rejeitaSemLocalizacao() {
        var r = new OcorrenciaRequest("Lixo na calçada", Categoria.LIXO, " ", null, null);
        assertEquals(1, validar(r).size());
    }
}
