package br.uepb.nobugs.config;

import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.model.Papel;
import br.uepb.nobugs.model.Usuario;
import br.uepb.nobugs.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ao ligar o sistema, garante o administrador (login "admin") e um usuário por órgão (login = nome do
 * órgão em minúsculas, ex.: "obras"). Quem já existe não é alterado.
 * A senha inicial vem da variável de ambiente NOBUGS_SENHA_INICIAL: ela não fica escrita no código
 * (o repositório é público). Sem a variável, esses usuários simplesmente não são criados.
 * O login curto fica na coluna "email"; moradores sempre usam e-mail (com @), então nunca colidem.
 */
@Component
public class UsuariosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuariosIniciais.class);

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final String senhaInicial;

    public UsuariosIniciais(UsuarioRepository repository, PasswordEncoder encoder,
                            @Value("${nobugs.senha-inicial:}") String senhaInicial) {
        this.repository = repository;
        this.encoder = encoder;
        this.senhaInicial = senhaInicial;
    }

    @Override
    public void run(String... args) {
        if (senhaInicial == null || senhaInicial.isBlank()) {
            log.warn("NOBUGS_SENHA_INICIAL não definida: o administrador e os usuários de órgão NÃO foram criados.");
            return;
        }
        if (senhaInicial.length() < 8 || senhaInicial.getBytes(StandardCharsets.UTF_8).length > 72) {
            log.error("NOBUGS_SENHA_INICIAL precisa ter no mínimo 8 caracteres e no máximo 72 bytes: usuários não criados.");
            return;
        }
        String hash = encoder.encode(senhaInicial);
        criarSeNaoExiste("admin", "Administrador", hash, Papel.ADMIN, null);
        for (Orgao orgao : Orgao.values()) {
            criarSeNaoExiste(orgao.name().toLowerCase(), "Órgão: " + orgao.getNome(), hash, Papel.ORGAO, orgao);
        }
    }

    private void criarSeNaoExiste(String login, String nome, String hash, Papel papel, Orgao orgao) {
        if (repository.existsByEmail(login)) {
            return;
        }
        repository.save(new Usuario(nome, login, hash, papel, orgao));
        log.info("Usuário inicial criado: {}", login);
    }
}
