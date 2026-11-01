package br.uepb.nobugs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** Registro de cada troca de órgão responsável: quem trocou, de qual para qual e quando. Vira uma tabela no banco. */
@Entity
@Table(name = "historico_reatribuicoes")
public class HistoricoReatribuicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ocorrenciaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Orgao orgaoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Orgao orgaoNovo;

    @Column(nullable = false)
    private String autor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfilAutor;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    /** Construtor vazio exigido pelo JPA. */
    protected HistoricoReatribuicao() {
    }

    public HistoricoReatribuicao(Long ocorrenciaId, Orgao orgaoAnterior, Orgao orgaoNovo,
                                 String autor, PerfilUsuario perfilAutor) {
        this.ocorrenciaId = ocorrenciaId;
        this.orgaoAnterior = orgaoAnterior;
        this.orgaoNovo = orgaoNovo;
        this.autor = autor;
        this.perfilAutor = perfilAutor;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getOcorrenciaId() { return ocorrenciaId; }
    public Orgao getOrgaoAnterior() { return orgaoAnterior; }
    public Orgao getOrgaoNovo() { return orgaoNovo; }
    public String getAutor() { return autor; }
    public PerfilUsuario getPerfilAutor() { return perfilAutor; }
    public LocalDateTime getDataHora() { return dataHora; }
}