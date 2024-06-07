package com.gs.sea_kids.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Entity
public class Cliente {

    @Column(name = "cl_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nm_cl", length = 100)
    private String nome;
    @Column(name = "email_cl", length = 100)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Formato de email inválido")
    private String email;

    @ManyToOne
    @JoinColumn(name = "app_id_app")
    private App app;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Cadastro> cadastros;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Login> logins;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public List<Cadastro> getCadastros() {
        return cadastros;
    }

    public void setCadastros(List<Cadastro> cadastros) {
        this.cadastros = cadastros;
    }

    public List<Login> getLogins() {
        return logins;
    }

    public void setLogins(List<Login> logins) {
        this.logins = logins;
    }
}
