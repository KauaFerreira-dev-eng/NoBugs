package br.uepb.nobugs.service;

import br.uepb.nobugs.dto.EstatisticasResponse;
import br.uepb.nobugs.dto.UsuarioLogado;
import br.uepb.nobugs.exception.AcessoNegadoException;
import br.uepb.nobugs.model.Categoria;
import br.uepb.nobugs.model.Ocorrencia;
import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.model.PerfilUsuario;
import br.uepb.nobugs.model.Risco;
import br.uepb.nobugs.model.Status;
import br.uepb.nobugs.repository.OcorrenciaRepository;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstatisticasService {

    private static final int DIAS_NO_GRAFICO = 14;

    private final OcorrenciaRepository repository;

    public EstatisticasService(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    /**
     * Monta os números do painel. Funcionário de órgão vê só o próprio órgão (o filtro pedido é ignorado);
     * administrador vê tudo ou filtra por órgão; morador não tem acesso.
     */
    @Transactional(readOnly = true)
    public EstatisticasResponse gerar(UsuarioLogado usuario, Orgao filtroPedido) {
        Orgao filtro = filtroPedido;
        if (usuario.perfil() == PerfilUsuario.ORGAO_PUBLICO) {
            if (usuario.orgao() == null) {
                throw new AcessoNegadoException("Seu usuário não está vinculado a nenhum órgão");
            }
            filtro = usuario.orgao();
        } else if (usuario.perfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new AcessoNegadoException("Só o órgão público ou o administrador vê o painel");
        }

        final Orgao orgaoFiltrado = filtro;
        List<Ocorrencia> ocorrencias = repository.findAll().stream()
                .filter(o -> orgaoFiltrado == null || o.getOrgao() == orgaoFiltrado)
                .toList();

        long resolvidas = ocorrencias.stream().filter(o -> o.getStatus() == Status.RESOLVIDA).count();

        return new EstatisticasResponse(
                ocorrencias.size(),
                resolvidas,
                contar(ocorrencias, Status.values(), Ocorrencia::getStatus),
                contar(ocorrencias, Categoria.values(), Ocorrencia::getCategoria),
                contar(ocorrencias, Orgao.values(), Ocorrencia::getOrgao),
                contar(ocorrencias, Risco.values(), Ocorrencia::getRisco),
                contarPorDia(ocorrencias));
    }

    /** Conta por valor do enum, já incluindo os valores sem nenhuma ocorrência (com zero). */
    private static <E extends Enum<E>> Map<String, Long> contar(List<Ocorrencia> ocorrencias, E[] valores,
                                                               Function<Ocorrencia, E> campo) {
        Map<String, Long> contagem = new LinkedHashMap<>();
        for (E valor : valores) {
            contagem.put(valor.name(), 0L);
        }
        for (Ocorrencia o : ocorrencias) {
            contagem.merge(campo.apply(o).name(), 1L, Long::sum);
        }
        return contagem;
    }

    /** Ocorrências registradas por dia, nos últimos 14 dias (do mais antigo ao de hoje). */
    private static Map<String, Long> contarPorDia(List<Ocorrencia> ocorrencias) {
        LocalDate hoje = LocalDate.now();
        Map<String, Long> porDia = new LinkedHashMap<>();
        for (int i = DIAS_NO_GRAFICO - 1; i >= 0; i--) {
            porDia.put(hoje.minusDays(i).toString(), 0L);
        }
        for (Ocorrencia o : ocorrencias) {
            porDia.computeIfPresent(o.getCriadoEm().toLocalDate().toString(), (dia, qtd) -> qtd + 1);
        }
        return porDia;
    }
}