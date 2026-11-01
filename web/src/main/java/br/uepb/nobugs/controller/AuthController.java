package br.uepb.nobugs.controller;

import br.uepb.nobugs.dto.LoginRequest;
import br.uepb.nobugs.dto.UsuarioLogado;
import br.uepb.nobugs.exception.NaoAutenticadoException;
import br.uepb.nobugs.service.AutenticacaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/auth")
public class AuthController {

    /** Nome com que o usuário logado fica guardado na sessão. */
    public static final String ATRIBUTO_SESSAO = "usuario";

    private final AutenticacaoService autenticacao;

    public AuthController(AutenticacaoService autenticacao) {
        this.autenticacao = autenticacao;
    }

    /** POST /auth/login: confere login e senha e abre a sessão. 401 se estiver errado. */
    @PostMapping("/login")
    public UsuarioLogado login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        UsuarioLogado usuario = autenticacao.autenticar(request.login(), request.senha());
        if (http.getSession(false) != null) {
            http.changeSessionId(); // evita reaproveitar um id de sessão anterior ao login
        }
        http.getSession(true).setAttribute(ATRIBUTO_SESSAO, usuario);
        return usuario;
    }

    /** POST /auth/logout: encerra a sessão. */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest http) {
        HttpSession sessao = http.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    /** GET /auth/eu: quem está logado. 401 se ninguém. As telas usam isso para saber o que mostrar. */
    @GetMapping("/eu")
    public UsuarioLogado eu(@SessionAttribute(name = ATRIBUTO_SESSAO, required = false) UsuarioLogado usuario) {
        if (usuario == null) {
            throw new NaoAutenticadoException("Ninguém está logado");
        }
        return usuario;
    }
}