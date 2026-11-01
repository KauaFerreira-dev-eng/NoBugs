package br.uepb.nobugs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Testa RN01 (órgão vem da categoria) e o estado inicial da ocorrência. */
class OcorrenciaTest {

    private Ocorrencia nova(Categoria categoria) {
        Usuario autor = new Usuario("Ana", "ana@exemplo.com", "hash", Papel.MORADOR);
        return new Ocorrencia("Descrição", categoria, "Rua A", null, null, autor);
    }

    @Test
    void novaOcorrenciaNasceRegistradaENaoClassificada() {
        var o = nova(Categoria.BURACO);
        assertEquals(Status.REGISTRADA, o.getStatus());
        assertEquals(Risco.NAO_CLASSIFICADO, o.getRisco());
    }

    @Test
    void orgaoVemDaCategoria() {
        assertEquals(Orgao.OBRAS, nova(Categoria.BURACO).getOrgao());
        assertEquals(Orgao.LIMPEZA_URBANA, nova(Categoria.LIXO).getOrgao());
        assertEquals(Orgao.ILUMINACAO_PUBLICA, nova(Categoria.ILUMINACAO).getOrgao());
    }

    @Test
    void ocorrenciaGuardaQuemRegistrou() {
        var o = nova(Categoria.LIXO);
        assertEquals("ana@exemplo.com", o.getAutor().getEmail());
    }
}
