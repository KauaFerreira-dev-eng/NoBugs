package br.uepb.nobugs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Quem tem acesso à área do órgão: administrador ou funcionário de um órgão. Vira a tabela "usuarios". */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String nome;

    /** Senha guardada só como hash BCrypt, nunca em texto puro. */
    @Column(nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil;

    /** Órgão do funcionário. Fica vazio para o administrador, que enxerga todos. */
    @Enumerated(EnumType.STRING)
    private Orgao orgao;

    /** Construtor vazio exigido pelo JPA. */
    protected Usuario() {
    }

    public Usuario(String login, String nome, String senhaHash, PerfilUsuario perfil, Orgao orgao) {
        this.login = login;
        this.nome = nome;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.orgao = orgao;
    }

    public Long getId() { return id; }
    public String getLogin() { return login; }
    public String getNome() { return nome; }
    public String getSenhaHash() { return senhaHash; }
    public PerfilUsuario getPerfil() { return perfil; }
    public Orgao getOrgao() { return orgao; }
}