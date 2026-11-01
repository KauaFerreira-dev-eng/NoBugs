package br.uepb.nobugs.service;

import br.uepb.nobugs.exception.RecursoNaoEncontradoException;
import br.uepb.nobugs.exception.RegraDeNegocioException;
import br.uepb.nobugs.model.Papel;
import br.uepb.nobugs.model.Usuario;
import br.uepb.nobugs.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regras de cadastro de usuário. Aqui a senha vira hash antes de ir para o banco. */
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Cadastro público: sempre cria um MORADOR. Órgão e administrador não se cadastram sozinhos. */
    @Transactional
    public Usuario cadastrar(String nome, String email, String senha) {
        String emailNormalizado = normalizar(email);
        if (repository.existsByEmail(emailNormalizado)) {
            throw new RegraDeNegocioException("Já existe um cadastro com este e-mail.");
        }
        String hash = passwordEncoder.encode(senha);
        return repository.save(new Usuario(nome.trim(), emailNormalizado, hash, Papel.MORADOR));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return repository.findByEmail(normalizar(email))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase();
    }
}
