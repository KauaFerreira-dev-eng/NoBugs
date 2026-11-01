package br.uepb.nobugs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.uepb.nobugs.exception.RegraDeNegocioException;
import br.uepb.nobugs.model.Papel;
import br.uepb.nobugs.model.Usuario;
import br.uepb.nobugs.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Testa o cadastro: senha em hash, e-mail normalizado e e-mail repetido recusado. Sem subir o sistema. */
class UsuarioServiceTest {

    private UsuarioRepository repository;
    private PasswordEncoder encoder;
    private UsuarioService service;

    @BeforeEach
    void preparar() {
        repository = mock(UsuarioRepository.class);
        encoder = new BCryptPasswordEncoder();
        service = new UsuarioService(repository, encoder);
        when(repository.save(any(Usuario.class))).thenAnswer(chamada -> chamada.getArgument(0));
    }

    @Test
    void senhaNuncaEhGuardadaEmTextoPuro() {
        Usuario u = service.cadastrar("Ana", "ana@exemplo.com", "senhaSegura123");
        assertNotEquals("senhaSegura123", u.getSenhaHash());
        assertTrue(encoder.matches("senhaSegura123", u.getSenhaHash()));
    }

    @Test
    void emailEhGuardadoEmMinusculasESemEspacos() {
        Usuario u = service.cadastrar("Ana", "  ANA@Exemplo.com ", "senhaSegura123");
        assertEquals("ana@exemplo.com", u.getEmail());
        assertEquals(Papel.MORADOR, u.getPapel());
    }

    @Test
    void recusaEmailJaCadastrado() {
        when(repository.existsByEmail("ana@exemplo.com")).thenReturn(true);
        assertThrows(RegraDeNegocioException.class,
                () -> service.cadastrar("Ana", "ana@exemplo.com", "senhaSegura123"));
    }
}
