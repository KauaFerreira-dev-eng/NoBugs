package br.uepb.nobugs.dto;

import br.uepb.nobugs.model.Papel;
import br.uepb.nobugs.model.Usuario;

/** O que a API devolve sobre um usuário. Repare: não existe campo de senha, nem hash. */
public record UsuarioResponse(Long id, String nome, String email, Papel papel) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPapel());
    }
}
