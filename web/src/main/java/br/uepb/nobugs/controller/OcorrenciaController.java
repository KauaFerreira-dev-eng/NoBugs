package br.uepb.nobugs.controller;

import br.uepb.nobugs.dto.CategoriaResponse;
import br.uepb.nobugs.dto.HistoricoReatribuicaoResponse;
import br.uepb.nobugs.dto.OcorrenciaRequest;
import br.uepb.nobugs.dto.OcorrenciaResponse;
import br.uepb.nobugs.dto.OrgaoResponse;
import br.uepb.nobugs.dto.ReatribuicaoRequest;
import br.uepb.nobugs.dto.UsuarioLogado;
import br.uepb.nobugs.exception.NaoAutenticadoException;
import br.uepb.nobugs.model.Categoria;
import br.uepb.nobugs.model.Ocorrencia;
import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.service.OcorrenciaService;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService service;

    public OcorrenciaController(OcorrenciaService service) {
        this.service = service;
    }

    /** POST /ocorrencias: registra uma ocorrência. Devolve 201 com o protocolo, ou 400 se faltar algo. */
    @PostMapping
    public ResponseEntity<OcorrenciaResponse> registrar(@Valid @RequestBody OcorrenciaRequest request) {
        Ocorrencia ocorrencia = service.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OcorrenciaResponse.from(ocorrencia));
    }

    /** GET /ocorrencias: lista as ocorrências registradas. Com ?orgao=OBRAS, só as desse órgão. */
    @GetMapping
    public List<OcorrenciaResponse> listar(@RequestParam(required = false) Orgao orgao) {
        return service.listar(orgao).stream().map(OcorrenciaResponse::from).toList();
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
     * PATCH /ocorrencias/{id}/orgao: troca o órgão responsável. Quem troca vem da sessão (login).
     * Devolve 401 se não estiver logado, 403 se o perfil não pode trocar, 404 se a ocorrência não existe
     * e 400 se o órgão for inválido ou o mesmo que já está.
     */
    @PatchMapping("/{id}/orgao")
    public OcorrenciaResponse reatribuir(
            @PathVariable Long id,
            @Valid @RequestBody ReatribuicaoRequest request,
            @SessionAttribute(name = AuthController.ATRIBUTO_SESSAO, required = false) UsuarioLogado usuario) {
        if (usuario == null) {
            throw new NaoAutenticadoException("Faça login para trocar o órgão responsável");
        }
        Ocorrencia ocorrencia = service.reatribuirOrgao(id, request.orgao(), usuario.nome(), usuario.perfil());
        return OcorrenciaResponse.from(ocorrencia);
    }

    /** GET /ocorrencias/{id}/historico: quem trocou o órgão, de qual para qual e quando. */
    @GetMapping("/{id}/historico")
    public List<HistoricoReatribuicaoResponse> historico(@PathVariable Long id) {
        return service.historicoDeReatribuicoes(id).stream()
                .map(HistoricoReatribuicaoResponse::from)
                .toList();
    }
}