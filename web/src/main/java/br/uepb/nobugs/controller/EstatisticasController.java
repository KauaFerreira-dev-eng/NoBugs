package br.uepb.nobugs.controller;

import br.uepb.nobugs.dto.EstatisticasResponse;
import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.service.EstatisticasService;
import br.uepb.nobugs.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/estatisticas")
public class EstatisticasController {

    private final EstatisticasService service;
    private final UsuarioService usuarioService;

    public EstatisticasController(EstatisticasService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    /**
     * GET /estatisticas: números para os gráficos. Exige login (401) e papel ORGAO ou ADMIN (403, em SecurityConfig).
     * O administrador pode filtrar com ?orgao=OBRAS; o funcionário de órgão sempre vê só o próprio órgão.
     */
    @GetMapping
    public EstatisticasResponse estatisticas(@RequestParam(required = false) Orgao orgao,
                                             Authentication autenticacao) {
        return service.gerar(usuarioService.buscarPorEmail(autenticacao.getName()), orgao);
    }
}
