package br.uepb.nobugs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Testa RN01 (órgão vem da categoria) e o estado inicial da ocorrência. */
class OcorrenciaTest {

    @Test
    void novaOcorrenciaNasceRegistradaENaoClassificada() {
        var o = new Ocorrencia("Buraco na rua", Categoria.BURACO, "Rua A", null, null);
        assertEquals(Status.REGISTRADA, o.getStatus());
        assertEquals(Risco.NAO_CLASSIFICADO, o.getRisco());
    }

    @Test
    void orgaoVemDaCategoria() {
        assertEquals(Orgao.OBRAS, new Ocorrencia("x", Categoria.BURACO, "Rua A", null, null).getOrgao());
        assertEquals(Orgao.LIMPEZA_URBANA, new Ocorrencia("x", Categoria.LIXO, "Rua A", null, null).getOrgao());
        assertEquals(Orgao.ILUMINACAO_PUBLICA, new Ocorrencia("x", Categoria.ILUMINACAO, "Rua A", null, null).getOrgao());
    }
}
