package br.uepb.nobugs.repository;

import br.uepb.nobugs.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

/** O Spring Data cria sozinho save(), findAll(), findById() etc. Não precisa escrever SQL. */
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
}
