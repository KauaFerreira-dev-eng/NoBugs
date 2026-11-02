package br.uepb.nobugs.controller;

import br.uepb.nobugs.dto.CadastroRequest;
import br.uepb.nobugs.dto.LoginRequest;
import br.uepb.nobugs.dto.UsuarioResponse;
import br.uepb.nobugs.exception.CredenciaisInvalidasException;
import br.uepb.nobugs.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Cadastro, login, logout e "quem sou eu". O controller só traduz HTTP: a regra mora no Service. */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contextoNaSessao = new HttpSessionSecurityContextRepository();

    public AuthController(UsuarioService usuarioService, AuthenticationManager authenticationManager) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroRequest dados) {
        var usuario = usuarioService.cadastrar(dados.nome(), dados.email(), dados.senha());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(usuario));
    }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest dados,
                                 HttpServletRequest request, HttpServletResponse response) {
        Authentication autenticacao;
        try {
            autenticacao = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            dados.login().trim().toLowerCase(), dados.senha()));
        } catch (AuthenticationException ex) {
            // mesma resposta para login inexistente e senha errada
            throw new CredenciaisInvalidasException();
        }

        // se já havia sessão, troca o identificador dela (evita fixação de sessão)
        if (request.getSession(false) != null) {
            request.changeSessionId();
        }
        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(autenticacao);
        SecurityContextHolder.setContext(contexto);
        contextoNaSessao.saveContext(contexto, request, response);

        return UsuarioResponse.from(usuarioService.buscarPorEmail(autenticacao.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        var sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    /** Quem está logado agora. Sem login, o Spring Security já devolve 401. */
    @GetMapping("/eu")
    public UsuarioResponse eu(Authentication autenticacao) {
        return UsuarioResponse.from(usuarioService.buscarPorEmail(autenticacao.getName()));
    }
}
