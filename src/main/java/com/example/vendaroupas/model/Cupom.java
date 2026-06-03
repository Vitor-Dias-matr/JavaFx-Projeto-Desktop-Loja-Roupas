package com.example.vendaroupas.model;

import java.time.LocalDateTime;

public class Cupom {
    private int id;
    private String codigo;
    private double porcentagemDesconto;
    private int quantidade;
    private LocalDateTime dataValidade;

    // Construtor Padrão
    public Cupom() {}

    // Construtor Completo
    public Cupom(int id, String codigo, double porcentagemDesconto, int quantidade, LocalDateTime dataValidade) {
        this.id = id;
        this.codigo = codigo;
        this.porcentagemDesconto = Math.min(porcentagemDesconto, 100.0); // Proteção para não passar de 100%
        this.quantidade = quantidade;
        this.dataValidade = dataValidade;
    }

    // Regra de Negócio: Verifica se o cupom pode ser usado
    public boolean isValid() {
        boolean temEstoque = this.quantidade > 0;
        boolean dentroDaValidade = (this.dataValidade == null || this.dataValidade.isAfter(LocalDateTime.now()));
        return temEstoque && dentroDaValidade;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public double getPorcentagemDesconto() { return porcentagemDesconto; }
    public void setPorcentagemDesconto(double desconto) { this.porcentagemDesconto = desconto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public LocalDateTime getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDateTime dataValidade) { this.dataValidade = dataValidade; }
}