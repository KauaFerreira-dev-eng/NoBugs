package br.uepb.nobugs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.uepb.nobugs.exception.AcessoNegadoException;
import br.uepb.nobugs.exception.RecursoNaoEncontradoException;
import br.uepb.nobugs.exception.RegraDeNegocioException;
import br.uepb.nobugs.model.Categoria;
import br.uepb.nobugs.model.HistoricoReatribuicao;
import br.uepb.nobugs.model.Ocorrencia;
import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.model.Papel;
import br.uepb.nobugs.model.Usuario;
import br.uepb.nobugs.repository.HistoricoReatribuicaoRepository;
import br.uepb.nobugs.repository.OcorrenciaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Regras de reatribuição de órgão (RN03) e de listagem por órgão. Sem subir o sistema. */
class OcorrenciaServiceTest {

    private OcorrenciaRepository repository;
    private HistoricoReatribuicaoRepository historico;
    private UsuarioService usuarios;
    private OcorrenciaService service;
    private Ocorrencia buraco;   // órgão: OBRAS

    @BeforeEach
    void preparar() {
        repository = mock(OcorrenciaRepository.class);
        historico = mock(HistoricoReatribuicaoRepository.class);
        usuarios = mock(UsuarioService.class);
        service = new OcorrenciaService(repository, historico, usuarios);

        Usuario morador = new Usuario("Ana", "ana@exemplo.com", "hash", Papel.MORADOR);
        buraco = new Ocorrencia("Buraco na rua", Categoria.BURACO, "Rua A", null, null, morador);
        when(repository.findById(1L)).thenReturn(Optional.of(buraco));
    }

    private void logadoComo(String login, Papel papel, Orgao orgao) {
        Usuario u = new Usuario("Funcionário", login, "hash", papel, orgao);
        when(usuarios.buscarPorEmail(login)).thenReturn(u);
    }

    @Test
    void administradorReatribuiEGravaHistorico() {
        logadoComo("admin", Papel.ADMIN, null);
        Ocorrencia r = service.reatribuirOrgao(1L, Orgao.LIMPEZA_URBANA, "admin");
        assertEquals(Orgao.LIMPEZA_URBANA, r.getOrgao());
        verify(historico).save(any(HistoricoReatribuicao.class));
    }

    @Test
    void orgaoReatribuiOcorrenciaDoProprioOrgao() {
        logadoComo("obras", Papel.ORGAO, Orgao.OBRAS);
        Ocorrencia r = service.reatribuirOrgao(1L, Orgao.SANEAMENTO, "obras");
        assertEquals(Orgao.SANEAMENTO, r.getOrgao());
    }

    @Test
    void orgaoNaoReatribuiOcorrenciaDeOutroOrgao() {
        logadoComo("saneamento", Papel.ORGAO, Orgao.SANEAMENTO);
        assertThrows(AcessoNegadoException.class,
                () -> service.reatribuirOrgao(1L, Orgao.LIMPEZA_URBANA, "saneamento"));
        verify(historico, never()).save(any(HistoricoReatribuicao.class));
    }

    @Test
    void moradorNaoReatribui() {
        logadoComo("ana@exemplo.com", Papel.MORADOR, null);
        assertThrows(AcessoNegadoException.class,
                () -> service.reatribuirOrgao(1L, Orgao.SANEAMENTO, "ana@exemplo.com"));
    }

    @Test
    void recusaReatribuirParaOMesmoOrgao() {
        logadoComo("admin", Papel.ADMIN, null);
        assertThrows(RegraDeNegocioException.class,
                () -> service.reatribuirOrgao(1L, Orgao.OBRAS, "admin"));
    }

    @Test
    void recusaOcorrenciaInexistente() {
        logadoComo("admin", Papel.ADMIN, null);
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.reatribuirOrgao(99L, Orgao.OBRAS, "admin"));
    }

    @Test
    void orgaoListaSoAsOcorrenciasDoProprioOrgao() {
        Usuario morador = new Usuario("Ana", "ana@exemplo.com", "hash", Papel.MORADOR);
        Ocorrencia lixo = new Ocorrencia("Lixo", Categoria.LIXO, "Rua B", null, null, morador);
        when(repository.findAll()).thenReturn(List.of(buraco, lixo));
        logadoComo("obras", Papel.ORGAO, Orgao.OBRAS);

        List<Ocorrencia> lista = service.listar(null, "obras");

        assertEquals(1, lista.size());
        assertEquals(Orgao.OBRAS, lista.get(0).getOrgao());
    }

    @Test
    void administradorListaTodasEPodeFiltrarPorOrgao() {
        Usuario morador = new Usuario("Ana", "ana@exemplo.com", "hash", Papel.MORADOR);
        Ocorrencia lixo = new Ocorrencia("Lixo", Categoria.LIXO, "Rua B", null, null, morador);
        when(repository.findAll()).thenReturn(List.of(buraco, lixo));
        logadoComo("admin", Papel.ADMIN, null);

        assertEquals(2, service.listar(null, "admin").size());
        assertEquals(1, service.listar(Orgao.LIMPEZA_URBANA, "admin").size());
    }
}
