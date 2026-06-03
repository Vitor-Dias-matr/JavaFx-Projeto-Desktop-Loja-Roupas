package com.example.vendaroupas.model;

public class ItemVenda {
    private int id;
    private int vendaId;
    private int produtoId;
    private String nomeProduto; // Útil para exibir texto direto na Tabela do JavaFX
    private int quantidade;
    private double precoUnitario;

    public ItemVenda() {}

    public ItemVenda(int produtoId, String nomeProduto, int quantidade, double precoUnitario) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    // Regra de Negócio: Calcula automaticamente o total deste item (Preço * Qtd)
    public double getTotalItem() {
        return this.precoUnitario * this.quantidade;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVendaId() { return vendaId; }
    public void setVendaId(int vendaId) { this.vendaId = vendaId; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }
}