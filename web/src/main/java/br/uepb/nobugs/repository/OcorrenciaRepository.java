package br.uepb.nobugs.repository;

import br.uepb.nobugs.model.Ocorrencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** O Spring Data cria sozinho save(), findAll() e também findByAutorId(...) só pelo nome do método. */
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    List<Ocorrencia> findByAutorId(Long autorId);
}
