package br.uepb.nobugs.security;

import br.uepb.nobugs.model.Usuario;
import br.uepb.nobugs.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Ensina o Spring Security a buscar o usuário (e o hash da senha) no nosso banco. */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = repository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .roles(usuario.getPapel().name())
                .build();
    }
}
