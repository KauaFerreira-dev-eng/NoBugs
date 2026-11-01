package br.uepb.nobugs.service;

import br.uepb.nobugs.dto.OcorrenciaRequest;
import br.uepb.nobugs.model.Ocorrencia;
import br.uepb.nobugs.model.Usuario;
import br.uepb.nobugs.repository.OcorrenciaRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regras do cadastro e da consulta de ocorrências. */
@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final UsuarioService usuarioService;

    public OcorrenciaService(OcorrenciaRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    /** Salva a ocorrência do usuário logado e gera o número de protocolo (ex.: CO-2026-00001). */
    @Transactional
    public Ocorrencia registrar(OcorrenciaRequest request, String emailDoAutor) {
        Usuario autor = usuarioService.buscarPorEmail(emailDoAutor);
        String endereco = request.endereco() == null || request.endereco().isBlank()
                ? null
                : request.endereco().trim();

        Ocorrencia ocorrencia = new Ocorrencia(
                request.descricao().trim(),
                request.categoria(),
                endereco,
                request.latitude(),
                request.longitude(),
                autor);

        ocorrencia = repository.save(ocorrencia);
        ocorrencia.setProtocolo(String.format("CO-%d-%05d", ocorrencia.getCriadoEm().getYear(), ocorrencia.getId()));
        return ocorrencia;
    }

    /** "Minhas ocorrências": só as do usuário logado. */
    @Transactional(readOnly = true)
    public List<Ocorrencia> listarDoAutor(String emailDoAutor) {
        Usuario autor = usuarioService.buscarPorEmail(emailDoAutor);
        return ordenar(repository.findByAutorId(autor.getId()));
    }

    /** Todas as ocorrências (uso do órgão e do administrador). */
    @Transactional(readOnly = true)
    public List<Ocorrencia> listarTodas() {
        return ordenar(repository.findAll());
    }

    /** Risco alto primeiro e, dentro do mesmo risco, as mais recentes primeiro (RF04). */
    private List<Ocorrencia> ordenar(List<Ocorrencia> ocorrencias) {
        return ocorrencias.stream()
                .sorted(Comparator.comparing(Ocorrencia::getRisco).reversed()
                        .thenComparing(Ocorrencia::getCriadoEm, Comparator.reverseOrder()))
                .toList();
    }
}
