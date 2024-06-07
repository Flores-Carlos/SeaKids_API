package com.gs.sea_kids.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity
public class App {

    @Column(name = "id_app")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nm_app", length = 50)
    private String nome;
    @Column(name = "versao_app", length = 50)
    private String versao;

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL)
    private List<Cliente> clientes;

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL)
    private List<Curiosidade> curiosidades;

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL)
    private List<Cadastro> cadastros;

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL)
    private List<Video> videos;

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

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versão) {
        this.versao = versao;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public List<Curiosidade> getCuriosidades() {
        return curiosidades;
    }

    public void setCuriosidades(List<Curiosidade> curiosidades) {
        this.curiosidades = curiosidades;
    }

    public List<Cadastro> getCadastros() {
        return cadastros;
    }

    public void setCadastros(List<Cadastro> cadastros) {
        this.cadastros = cadastros;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
