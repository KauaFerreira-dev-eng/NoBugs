package br.uepb.nobugs.service;

import br.uepb.nobugs.dto.OcorrenciaRequest;
import br.uepb.nobugs.model.Ocorrencia;
import br.uepb.nobugs.repository.OcorrenciaRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regras do cadastro de ocorrências. */
@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;

    public OcorrenciaService(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    /** Salva a ocorrência e gera o número de protocolo (ex.: CO-2026-00001). */
    @Transactional
    public Ocorrencia registrar(OcorrenciaRequest request) {
        String endereco = request.endereco() == null || request.endereco().isBlank()
                ? null
                : request.endereco().trim();

        Ocorrencia ocorrencia = new Ocorrencia(
                request.descricao().trim(),
                request.categoria(),
                endereco,
                request.latitude(),
                request.longitude());

        ocorrencia = repository.save(ocorrencia);
        ocorrencia.setProtocolo(String.format("CO-%d-%05d", ocorrencia.getCriadoEm().getYear(), ocorrencia.getId()));
        return ocorrencia;
    }

    /** Lista com risco alto primeiro e, dentro do mesmo risco, as mais recentes primeiro (RF04). */
    @Transactional(readOnly = true)
    public List<Ocorrencia> listar() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Ocorrencia::getRisco).reversed()
                        .thenComparing(Ocorrencia::getCriadoEm, Comparator.reverseOrder()))
                .toList();
    }
}
