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

/** Uma ocorrência de problema urbano registrada por um morador. Vira uma tabela no banco. */
@Entity
@Table(name = "ocorrencias")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String protocolo;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Risco risco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Orgao orgao;

    private String endereco;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    /** Construtor vazio exigido pelo JPA. */
    protected Ocorrencia() {
    }

    public Ocorrencia(String descricao, Categoria categoria,
                      String endereco, Double latitude, Double longitude) {
        this.descricao = descricao;
        this.categoria = categoria;
        this.orgao = categoria.getOrgao();   // RN01: o órgão vem da categoria
        this.risco = Risco.NAO_CLASSIFICADO; // RF04: quem classifica é o órgão, depois
        this.endereco = endereco;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = Status.REGISTRADA;
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getProtocolo() { return protocolo; }
    public void setProtocolo(String protocolo) { this.protocolo = protocolo; }
    public String getDescricao() { return descricao; }
    public Categoria getCategoria() { return categoria; }
    public Risco getRisco() { return risco; }
    public Orgao getOrgao() { return orgao; }
    public String getEndereco() { return endereco; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Status getStatus() { return status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
