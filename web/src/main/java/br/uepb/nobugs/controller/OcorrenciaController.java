package br.uepb.nobugs.controller;

import br.uepb.nobugs.dto.CategoriaResponse;
import br.uepb.nobugs.dto.HistoricoReatribuicaoResponse;
import br.uepb.nobugs.dto.OcorrenciaRequest;
import br.uepb.nobugs.dto.OcorrenciaResponse;
import br.uepb.nobugs.dto.OrgaoResponse;
import br.uepb.nobugs.dto.ReatribuicaoRequest;
import br.uepb.nobugs.model.Categoria;
import br.uepb.nobugs.model.Ocorrencia;
import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.service.OcorrenciaService;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService service;

    public OcorrenciaController(OcorrenciaService service) {
        this.service = service;
    }

    /** POST /ocorrencias: registra uma ocorrência do usuário logado. Devolve 201 com o protocolo. */
    @PostMapping
    public ResponseEntity<OcorrenciaResponse> registrar(@Valid @RequestBody OcorrenciaRequest request,
                                                        Authentication autenticacao) {
        Ocorrencia ocorrencia = service.registrar(request, autenticacao.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(OcorrenciaResponse.from(ocorrencia));
    }

    /** GET /ocorrencias/minhas: as ocorrências do usuário logado ("Minhas ocorrências"). */
    @GetMapping("/minhas")
    public List<OcorrenciaResponse> minhas(Authentication autenticacao) {
        return service.listarDoAutor(autenticacao.getName()).stream().map(OcorrenciaResponse::from).toList();
    }

    /**
     * GET /ocorrencias: lista para a equipe (só ORGAO e ADMIN, regra em SecurityConfig).
     * Com ?orgao=OBRAS, só as desse órgão. O funcionário de órgão sempre vê só as do próprio órgão.
     */
    @GetMapping
    public List<OcorrenciaResponse> listar(@RequestParam(required = false) Orgao orgao,
                                           Authentication autenticacao) {
        return service.listar(orgao, autenticacao.getName()).stream().map(OcorrenciaResponse::from).toList();
    }

    /** GET /ocorrencias/categorias: alimenta o seletor de categorias da tela. */
    @GetMapping("/categorias")
    public List<CategoriaResponse> categorias() {
        return Arrays.stream(Categoria.values())
                .map(c -> new CategoriaResponse(c.name(), c.getNome()))
                .toList();
    }

    /** GET /ocorrencias/orgaos: lista cadastrada de órgãos, alimenta o seletor de reatribuição. */
    @GetMapping("/orgaos")
    public List<OrgaoResponse> orgaos() {
        return Arrays.stream(Orgao.values())
                .map(o -> new OrgaoResponse(o.name(), o.getNome()))
                .toList();
    }

    /**
     * PATCH /ocorrencias/{id}/orgao: troca o órgão responsável. Quem troca é o usuário logado.
     * Devolve 401 sem login, 403 se o papel não pode trocar ou a ocorrência é de outro órgão, 404 se a ocorrência não existe,
     * 400 se o órgão for inválido e 409 se for o mesmo órgão que já está.
     */
    @PatchMapping("/{id}/orgao")
    public OcorrenciaResponse reatribuir(@PathVariable Long id,
                                         @Valid @RequestBody ReatribuicaoRequest request,
                                         Authentication autenticacao) {
        Ocorrencia ocorrencia = service.reatribuirOrgao(id, request.orgao(), autenticacao.getName());
        return OcorrenciaResponse.from(ocorrencia);
    }

    /** GET /ocorrencias/{id}/historico: quem trocou o órgão, de qual para qual e quando. ORGAO e ADMIN. */
    @GetMapping("/{id}/historico")
    public List<HistoricoReatribuicaoResponse> historico(@PathVariable Long id) {
        return service.historicoDeReatribuicoes(id).stream()
                .map(HistoricoReatribuicaoResponse::from)
                .toList();
    }
}
