package br.uepb.nobugs.config;

import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.model.PerfilUsuario;
import br.uepb.nobugs.model.Usuario;
import br.uepb.nobugs.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ao ligar o sistema com a tabela de usuários vazia, cria um administrador ("admin") e um usuário por órgão
 * (login = nome do órgão em minúsculas, ex.: "obras"). A senha inicial vem de nobugs.senha-inicial.
 * Só serve para desenvolvimento: troque a senha antes de usar de verdade.
 */
@Component
public class UsuariosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuariosIniciais.class);

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final String senhaInicial;

    public UsuariosIniciais(UsuarioRepository repository, PasswordEncoder encoder,
                            @Value("${nobugs.senha-inicial:nobugs123}") String senhaInicial) {
        this.repository = repository;
        this.encoder = encoder;
        this.senhaInicial = senhaInicial;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        String hash = encoder.encode(senhaInicial);
        repository.save(new Usuario("admin", "Administrador", hash, PerfilUsuario.ADMINISTRADOR, null));
        for (Orgao orgao : Orgao.values()) {
            repository.save(new Usuario(orgao.name().toLowerCase(), "Órgão: " + orgao.getNome(),
                    hash, PerfilUsuario.ORGAO_PUBLICO, orgao));
        }
        log.info("Usuários iniciais criados (admin e um por órgão). Use a senha definida em nobugs.senha-inicial.");
    }
}