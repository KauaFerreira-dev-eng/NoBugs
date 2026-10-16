package br.uepb.nobugs.controller;

import br.uepb.nobugs.dto.CategoriaResponse;
import br.uepb.nobugs.dto.OcorrenciaRequest;
import br.uepb.nobugs.dto.OcorrenciaResponse;
import br.uepb.nobugs.model.Categoria;
import br.uepb.nobugs.model.Ocorrencia;
import br.uepb.nobugs.service.OcorrenciaService;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService service;

    public OcorrenciaController(OcorrenciaService service) {
        this.service = service;
    }

    /** POST /ocorrencias: registra uma ocorrência. Devolve 201 com o protocolo, ou 400 se faltar algo. */
    @PostMapping
    public ResponseEntity<OcorrenciaResponse> registrar(@Valid @RequestBody OcorrenciaRequest request) {
        Ocorrencia ocorrencia = service.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OcorrenciaResponse.from(ocorrencia));
    }

    /** GET /ocorrencias: lista as ocorrências registradas. */
    @GetMapping
    public List<OcorrenciaResponse> listar() {
        return service.listar().stream().map(OcorrenciaResponse::from).toList();
    }

    /** GET /ocorrencias/categorias: alimenta o seletor de categorias da tela. */
    @GetMapping("/categorias")
    public List<CategoriaResponse> categorias() {
        return Arrays.stream(Categoria.values())
                .map(c -> new CategoriaResponse(c.name(), c.getNome()))
                .toList();
    }
}
