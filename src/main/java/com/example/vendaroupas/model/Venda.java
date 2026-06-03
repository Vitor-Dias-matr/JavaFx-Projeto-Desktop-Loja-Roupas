package com.example.vendaroupas.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venda {
    private int id;
    private double subtotal;
    private double desconto;
    private double total;
    private String formaPagamento;
    private Integer cupomId; // Pode ser null se não usar cupom
    private LocalDateTime dataVenda;

    // Lista auxiliar para manipular os itens em memória na tela do Caixa
    private List<ItemVenda> itens = new ArrayList<>();

    public Venda() {
        this.subtotal = 0.0;
        this.desconto = 0.0;
        this.total = 0.0;
    }

    // Regra de Negócio: Recalcula os valores de forma segura baseado nos itens adicionados
    public void calcularTotais(Cupom cupom) {
        this.subtotal = 0.0;
        for (ItemVenda item : itens) {
            this.subtotal += item.getTotalItem();
        }

        if (cupom != null && cupom.isValid()) {
            this.cupomId = cupom.getId();
            this.desconto = this.subtotal * (cupom.getPorcentagemDesconto() / 100.0);
        } else {
            this.cupomId = null;
            this.desconto = 0.0;
        }

        this.total = this.subtotal - this.desconto;
    }

    public void adicionarItem(ItemVenda item) {
        this.itens.add(item);
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public Integer getCupomId() { return cupomId; }
    public void setCupomId(Integer cupomId) { this.cupomId = cupomId; }

    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }

    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; }
}