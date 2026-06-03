package com.example.vendaroupas.model;

import java.time.LocalDateTime;

public class Produto {
    private Integer id;
    private String nome;
    private Categoria categoria;
    private String tamanho;
    private String cor;
    private Double preco;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAlteracao;
    private Integer quantidade;

    // Construtor padrão
    public Produto() {}

    // Construtor auxiliar para inserção (sem ID e sem Datas)
    public Produto(String nome, Categoria categoria, String tamanho, String cor, Double preco, Integer quantidade) {
        this.nome = nome;
        this.categoria = categoria;
        this.tamanho = tamanho;
        this.cor = cor;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public LocalDateTime getDataAlteracao() { return dataAlteracao; }
    public void setDataAlteracao(LocalDateTime dataAlteracao) { this.dataAlteracao = dataAlteracao; }

    public Integer getQuantidade() {return quantidade;}
    public void setQuantidade(Integer quantidade) {this.quantidade = quantidade;}
}