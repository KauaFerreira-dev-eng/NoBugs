package br.uepb.nobugs.dto;

import br.uepb.nobugs.model.Orgao;
import br.uepb.nobugs.model.PerfilUsuario;
import java.io.Serializable;

/** Quem está logado. Fica guardado na sessão e também é o que o /auth/eu devolve (nunca inclui a senha). */
public record UsuarioLogado(Long id, String nome, PerfilUsuario perfil, Orgao orgao) implements Serializable {
}