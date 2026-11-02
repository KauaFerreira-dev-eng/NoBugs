package br.uepb.nobugs.service;

import br.uepb.nobugs.dto.OcorrenciaRequest;
import br.uepb.nobugs.exception.AcessoNegadoException;
import br.uepb.nobugs.exception.RecursoNaoEncontradoException;
import br.uepb.nobugs.exception.RegraDeNegocioException;
import br.uepb.nobugs.model.HistoricoReatribuicao;
import br.uepb.nobugs.model.Ocorrencia;
import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.model.Papel;
import br.uepb.nobugs.model.Usuario;
import br.uepb.nobugs.repository.HistoricoReatribuicaoRepository;
import br.uepb.nobugs.repository.OcorrenciaRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regras do cadastro, da consulta e da reatribuição de órgão das ocorrências. */
@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final HistoricoReatribuicaoRepository historicoRepository;
    private final UsuarioService usuarioService;

    public OcorrenciaService(OcorrenciaRepository repository,
                             HistoricoReatribuicaoRepository historicoRepository,
                             UsuarioService usuarioService) {
        this.repository = repository;
        this.historicoRepository = historicoRepository;
        this.usuarioService = usuarioService;
    }

    /** Salva a ocorrência do usuário logado e gera o número de protocolo (ex.: CO-2026-00001). */
    @Transactional
    public Ocorrencia registrar(OcorrenciaRequest request, String emailDoAutor) {
        Usuario autor = usuarioService.buscarPorEmail(emailDoAutor);
        String endereco = request.endereco() == null || request.endereco().isBlank()
                ? null
                : request.endereco().trim();
        Ocorrencia ocorrencia = new Ocorrencia(
                request.descricao().trim(),
                request.categoria(),
                endereco,
                request.latitude(),
                request.longitude(),
                autor);
        ocorrencia = repository.save(ocorrencia);
        ocorrencia.setProtocolo(String.format("CO-%d-%05d", ocorrencia.getCriadoEm().getYear(), ocorrencia.getId()));
        return ocorrencia;
    }

    /** "Minhas ocorrências": só as do usuário logado. */
    @Transactional(readOnly = true)
    public List<Ocorrencia> listarDoAutor(String emailDoAutor) {
        Usuario autor = usuarioService.buscarPorEmail(emailDoAutor);
        return ordenar(repository.findByAutorId(autor.getId()));
    }

    /**
     * Lista para a equipe. Funcionário de órgão vê só as ocorrências do próprio órgão (o filtro pedido é
     * ignorado); o administrador vê todas ou filtra por órgão.
     * Depois de uma reatribuição, a ocorrência deixa de aparecer para o órgão antigo.
     */
    @Transactional(readOnly = true)
    public List<Ocorrencia> listar(Orgao filtroPedido, String emailDoUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailDoUsuario);
        Orgao filtro = filtroPedido;
        if (usuario.getPapel() == Papel.ORGAO) {
            if (usuario.getOrgao() == null) {
                throw new AcessoNegadoException("Seu usuário não está vinculado a nenhum órgão.");
            }
            filtro = usuario.getOrgao();
        }
        final Orgao orgaoFiltrado = filtro;
        return ordenar(repository.findAll().stream()
                .filter(o -> orgaoFiltrado == null || o.getOrgao() == orgaoFiltrado)
                .toList());
    }

    /**
     * Troca o órgão responsável pela ocorrência e registra no histórico quem fez e quando.
     * Só ORGAO e ADMIN podem trocar (o SecurityConfig já barra antes; aqui é a segunda trava).
     */
    @Transactional
    public Ocorrencia reatribuirOrgao(Long id, Orgao novoOrgao, String emailDoAutor) {
        Usuario autor = usuarioService.buscarPorEmail(emailDoAutor);
        if (!autor.getPapel().podeReatribuirOrgao()) {
            throw new AcessoNegadoException("Só o órgão público ou o administrador pode trocar o órgão responsável.");
        }
        Ocorrencia ocorrencia = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrência não encontrada."));
        if (autor.getPapel() == Papel.ORGAO && autor.getOrgao() != ocorrencia.getOrgao()) {
            throw new AcessoNegadoException("Esta ocorrência pertence a outro órgão.");
        }
        Orgao anterior = ocorrencia.getOrgao();
        if (anterior == novoOrgao) {
            throw new RegraDeNegocioException("A ocorrência já está com esse órgão.");
        }
        ocorrencia.reatribuirOrgao(novoOrgao);
        historicoRepository.save(new HistoricoReatribuicao(id, anterior, novoOrgao, autor.getNome(), autor.getPapel()));
        return ocorrencia;
    }

    /** Histórico de trocas de órgão da ocorrência, da mais recente para a mais antiga. */
    @Transactional(readOnly = true)
    public List<HistoricoReatribuicao> historicoDeReatribuicoes(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Ocorrência não encontrada.");
        }
        return historicoRepository.findByOcorrenciaIdOrderByDataHoraDesc(id);
    }

    /** Risco alto primeiro e, dentro do mesmo risco, as mais recentes primeiro (RF04). */
    private List<Ocorrencia> ordenar(List<Ocorrencia> ocorrencias) {
        return ocorrencias.stream()
                .sorted(Comparator.comparing(Ocorrencia::getRisco).reversed()
                        .thenComparing(Ocorrencia::getCriadoEm, Comparator.reverseOrder()))
                .toList();
    }
}
