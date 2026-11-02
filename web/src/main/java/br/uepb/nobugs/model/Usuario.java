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

/** Pessoa que usa o sistema. A senha nunca é guardada: só o hash BCrypt dela. */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;

    /** Órgão do funcionário (papel ORGAO). Fica vazio para morador e administrador. */
    @Enumerated(EnumType.STRING)
    private Orgao orgao;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    /** Construtor vazio exigido pelo JPA. */
    protected Usuario() {
    }

    public Usuario(String nome, String email, String senhaHash, Papel papel) {
        this(nome, email, senhaHash, papel, null);
    }

    public Usuario(String nome, String email, String senhaHash, Papel papel, Orgao orgao) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.papel = papel;
        this.orgao = orgao;
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public Papel getPapel() { return papel; }
    public Orgao getOrgao() { return orgao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
