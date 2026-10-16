package br.uepb.nobugs.dto;

import br.uepb.nobugs.model.Categoria;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dados que o morador envia para registrar uma ocorrência (RF01, RF02, RF03, RF09). O risco não vem do morador: o órgão classifica depois (RF04). */
public record OcorrenciaRequest(
        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 500, message = "A descrição pode ter no máximo 500 caracteres")
        String descricao,

        @NotNull(message = "A categoria é obrigatória")
        Categoria categoria,

        String endereco,
        Double latitude,
        Double longitude) {

    /** RF02: precisa ter o endereço digitado OU as coordenadas do GPS. */
    @AssertTrue(message = "Informe a localização: permita o acesso ao GPS ou digite o endereço")
    public boolean isLocalizacaoInformada() {
        boolean temEndereco = endereco != null && !endereco.isBlank();
        boolean temCoordenadas = latitude != null && longitude != null;
        return temEndereco || temCoordenadas;
    }
}
