package br.uepb.nobugs.dto;

import jakarta.validation.constraints.NotBlank;

/** O morador entra com o e-mail; a equipe (administrador e órgãos) entra com o login curto (ex.: admin, obras). */
public record LoginRequest(
        @NotBlank(message = "Informe o e-mail ou o login") String login,
        @NotBlank(message = "A senha é obrigatória") String senha) {
}
