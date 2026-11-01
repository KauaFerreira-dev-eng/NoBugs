package br.uepb.nobugs.controller;

import br.uepb.nobugs.dto.EstatisticasResponse;
import br.uepb.nobugs.dto.UsuarioLogado;
import br.uepb.nobugs.exception.NaoAutenticadoException;
import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.service.EstatisticasService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/estatisticas")
public class EstatisticasController {

    private final EstatisticasService service;

    public EstatisticasController(EstatisticasService service) {
        this.service = service;
    }

    /**
     * GET /estatisticas: números para os gráficos. Exige login (401) e perfil de órgão ou administrador (403).
     * O administrador pode filtrar com ?orgao=OBRAS; o funcionário de órgão sempre vê só o próprio órgão.
     */
    @GetMapping
    public EstatisticasResponse estatisticas(
            @RequestParam(required = false) Orgao orgao,
            @SessionAttribute(name = AuthController.ATRIBUTO_SESSAO, required = false) UsuarioLogado usuario) {
        if (usuario == null) {
            throw new NaoAutenticadoException("Faça login para ver o painel");
        }
        return service.gerar(usuario, orgao);
    }
}