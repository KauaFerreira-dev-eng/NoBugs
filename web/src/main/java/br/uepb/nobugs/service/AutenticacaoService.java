package br.uepb.nobugs.service;

import br.uepb.nobugs.dto.UsuarioLogado;
import br.uepb.nobugs.exception.NaoAutenticadoException;
import br.uepb.nobugs.model.Usuario;
import br.uepb.nobugs.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    /** Hash usado quando o login não existe, para demorar o mesmo tempo e não revelar quais logins existem. */
    private final String hashFalso;

    public AutenticacaoService(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
        this.hashFalso = encoder.encode("senha-que-ninguem-usa");
    }

    /** Confere login e senha. A mensagem de erro é a mesma nos dois casos (login errado ou senha errada). */
    @Transactional(readOnly = true)
    public UsuarioLogado autenticar(String login, String senha) {
        Usuario usuario = repository.findByLogin(login.trim().toLowerCase()).orElse(null);
        String hash = usuario != null ? usuario.getSenhaHash() : hashFalso;
        boolean senhaConfere = encoder.matches(senha, hash);
        if (usuario == null || !senhaConfere) {
            throw new NaoAutenticadoException("Login ou senha inválidos");
        }
        return new UsuarioLogado(usuario.getId(), usuario.getNome(), usuario.getPerfil(), usuario.getOrgao());
    }
}