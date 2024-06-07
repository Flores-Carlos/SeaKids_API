package com.gs.sea_kids.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Entity
public class Cadastro {

    @Column(name = "id_cd")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nome_cd", length = 100)
    private String nome;
    @Column(name = "email_cd", length = 100)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Formato de email inválido")
    private String email;
    @Column(name = "senha_cd", length = 100)
    private String senha;

    @ManyToOne
    @JoinColumn(name = "app_id_app")
    private App app;

    @ManyToOne
    @JoinColumn(name = "cliente_cl_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "cadastro", cascade = CascadeType.ALL)
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Login> getLogins() {
        return logins;
    }

    public void setLogins(List<Login> logins) {
        this.logins = logins;
    }
}
