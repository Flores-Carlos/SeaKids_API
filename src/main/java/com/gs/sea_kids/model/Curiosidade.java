package com.gs.sea_kids.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Entity
@Table(name = "curiosidades")
public class Curiosidade {

    @Column(name = "id_cs")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "titulo_cs", length = 100)
    private String titulo;
    @Column(name = "imagem_cs")
    @Lob
    private String imagem;
    @Column(name = "texto_cs")
    @Lob
    private String texto;

    @ManyToOne
    @JoinColumn(name = "app_id_app")
    private App app;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }
}
