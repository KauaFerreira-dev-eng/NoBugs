package br.uepb.nobugs.service;

import br.uepb.nobugs.dto.OcorrenciaRequest;
import br.uepb.nobugs.exception.AcessoNegadoException;
import br.uepb.nobugs.exception.NaoEncontradoException;
import br.uepb.nobugs.exception.RegraDeNegocioException;
import br.uepb.nobugs.model.HistoricoReatribuicao;
import br.uepb.nobugs.model.Ocorrencia;
import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.model.PerfilUsuario;
import br.uepb.nobugs.repository.HistoricoReatribuicaoRepository;
import br.uepb.nobugs.repository.OcorrenciaRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regras do cadastro de ocorrências. */
@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final HistoricoReatribuicaoRepository historicoRepository;

    public OcorrenciaService(OcorrenciaRepository repository,
                             HistoricoReatribuicaoRepository historicoRepository) {
        this.repository = repository;
        this.historicoRepository = historicoRepository;
    }

    /** Salva a ocorrência e gera o número de protocolo (ex.: CO-2026-00001). */
    @Transactional
    public Ocorrencia registrar(OcorrenciaRequest request) {
        String endereco = request.endereco() == null || request.endereco().isBlank()
                ? null
                : request.endereco().trim();

        Ocorrencia ocorrencia = new Ocorrencia(
                request.descricao().trim(),
                request.categoria(),
                endereco,
                request.latitude(),
                request.longitude());

        ocorrencia = repository.save(ocorrencia);
        ocorrencia.setProtocolo(String.format("CO-%d-%05d", ocorrencia.getCriadoEm().getYear(), ocorrencia.getId()));
        return ocorrencia;
    }

    /** Lista com risco alto primeiro e, dentro do mesmo risco, as mais recentes primeiro (RF04). */
    @Transactional(readOnly = true)
    public List<Ocorrencia> listar() {
        return listar(null);
    }

    /**
     * Igual a listar(), mas se vier um órgão mostra só as ocorrências dele.
     * Depois de uma reatribuição, a ocorrência deixa de aparecer para o órgão antigo.
     */
    @Transactional(readOnly = true)
    public List<Ocorrencia> listar(Orgao orgao) {
        return repository.findAll().stream()
                .filter(o -> orgao == null || o.getOrgao() == orgao)
                .sorted(Comparator.comparing(Ocorrencia::getRisco).reversed()
                        .thenComparing(Ocorrencia::getCriadoEm, Comparator.reverseOrder()))
                .toList();
    }

    /**
     * Troca o órgão responsável pela ocorrência e registra no histórico quem fez e quando.
     * Só órgão público e administrador podem trocar; o autor precisa estar identificado.
     */
    @Transactional
    public Ocorrencia reatribuirOrgao(Long id, Orgao novoOrgao, String autor, PerfilUsuario perfil) {
        if (perfil == null || !perfil.podeReatribuirOrgao()) {
            throw new AcessoNegadoException("Só o órgão público ou o administrador pode trocar o órgão responsável");
        }
        if (autor == null || autor.isBlank()) {
            throw new AcessoNegadoException("Identifique quem está fazendo a troca");
        }

        Ocorrencia ocorrencia = repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Ocorrência não encontrada"));

        Orgao anterior = ocorrencia.getOrgao();
        if (anterior == novoOrgao) {
            throw new RegraDeNegocioException("A ocorrência já está com esse órgão");
        }

        ocorrencia.reatribuirOrgao(novoOrgao);
        historicoRepository.save(new HistoricoReatribuicao(id, anterior, novoOrgao, autor.trim(), perfil));
        return ocorrencia;
    }

    /** Histórico de trocas de órgão da ocorrência, da mais recente para a mais antiga. */
    @Transactional(readOnly = true)
    public List<HistoricoReatribuicao> historicoDeReatribuicoes(Long id) {
        if (!repository.existsById(id)) {
            throw new NaoEncontradoException("Ocorrência não encontrada");
        }
        return historicoRepository.findByOcorrenciaIdOrderByDataHoraDesc(id);
    }
}