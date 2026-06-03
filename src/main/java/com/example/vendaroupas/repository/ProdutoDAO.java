package com.example.vendaroupas.repository;

import com.example.vendaroupas.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    // 1. CREATE (Salvar)
    public void salvar(Produto produto) {
        String sql = "INSERT INTO produto (nome, categoria, tamanho, cor, preco,quantidade) VALUES (?, ?, ?, ?, ?,?)";

        try (Connection conn = FabricaDeConexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // CORREÇÃO: Vincula cada atributo do produto ao seu respectivo ponto de interrogação (?)
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getCategoria().getCodigo()); // Salva o número (Ex: 1, 2, 3...)
            stmt.setString(3, produto.getTamanho());
            stmt.setString(4, produto.getCor());
            stmt.setDouble(5, produto.getPreco());
            stmt.setDouble(6, produto.getQuantidade());

            stmt.executeUpdate();

            // Recupera o ID gerado automaticamente
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produto.setId(generatedKeys.getInt(1));
                }
            }
            System.out.println("Produto cadastrado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
        }
    }

    // 2. READ ALL (Listar todos)
    public List<Produto> listarTodos() {
        String sql = "SELECT * FROM produto";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = FabricaDeConexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));

                // CORREÇÃO: Lê o número inteiro do banco e usa o método do seu Enum para reconstruí-lo
                int codigoCategoria = rs.getInt("categoria");
                produto.setCategoria(Categoria.fromCodigo(codigoCategoria));

                produto.setTamanho(rs.getString("tamanho"));
                produto.setCor(rs.getString("cor"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setQuantidade(rs.getInt("quantidade"));

                Timestamp cadastro = rs.getTimestamp("data_cadastro");
                if (cadastro != null) produto.setDataCadastro(cadastro.toLocalDateTime());

                Timestamp alteracao = rs.getTimestamp("data_alteracao");
                if (alteracao != null) produto.setDataAlteracao(alteracao.toLocalDateTime());

                produtos.add(produto);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        }
        return produtos;
    }

    // 3. UPDATE (Atualizar)
    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, categoria = ?, tamanho = ?, cor = ?, preco = ? , quantidade = ? WHERE id = ?";

        try (Connection conn = FabricaDeConexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // CORREÇÃO: Vincula os dados corretos nas interrogações do UPDATE
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, produto.getCategoria().getCodigo()); // Atualiza com o número inteiro
            stmt.setString(3, produto.getTamanho());
            stmt.setString(4, produto.getCor());
            stmt.setDouble(5, produto.getPreco());
            stmt.setDouble(6, produto.getQuantidade());
            stmt.setInt(7, produto.getId()); // Onde id = ?


            stmt.executeUpdate();
            System.out.println("Produto atualizado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    // 4. DELETE (Excluir)
    public void deletar(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";

        try (Connection conn = FabricaDeConexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Produto removido com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao deletar produto: " + e.getMessage());
        }
    }

    public void finalizarVenda(Venda venda, List<ItemVenda> itens, Cupom cupomAplicado) {
        String sqlVenda = "INSERT INTO venda (subtotal, desconto, total, forma_pagamento, cupom_id) VALUES (?, ?, ?, ?, ?)";
        String sqlItem = "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
        String sqlAtualizaEstoque = "UPDATE produto SET quantidade = quantidade - ? WHERE id = ?";
        String sqlAtualizaCupom = "UPDATE cupom SET quantidade = quantidade - 1 WHERE id = ?";

        Connection conn = null;
        try {
            conn = FabricaDeConexao.obterConexao();
            conn.setAutoCommit(false); // Inicia transação ACID para garantir consistência

            // 1. Salva Cabeçalho da Venda
            try (PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                stmtVenda.setDouble(1, venda.getSubtotal());
                stmtVenda.setDouble(2, venda.getDesconto());
                stmtVenda.setDouble(3, venda.getTotal());
                stmtVenda.setString(4, venda.getFormaPagamento());
                if (cupomAplicado != null) {
                    stmtVenda.setInt(5, cupomAplicado.getId());
                } else {
                    stmtVenda.setNull(5, java.sql.Types.INTEGER);
                }
                stmtVenda.executeUpdate();

                try (ResultSet rs = stmtVenda.getGeneratedKeys()) {
                    if (rs.next()) { venda.setId(rs.getInt(1)); }
                }
            }

            // 2. Salva Itens e Atualiza o Estoque de cada produto
            try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem);
                 PreparedStatement stmtEstoque = conn.prepareStatement(sqlAtualizaEstoque)) {

                for (ItemVenda item : itens) {
                    // Insere item da venda
                    stmtItem.setInt(1, venda.getId());
                    stmtItem.setInt(2, item.getProdutoId());
                    stmtItem.setInt(3, item.getQuantidade());
                    stmtItem.setDouble(4, item.getPrecoUnitario());
                    stmtItem.addBatch();

                    // Diminui quantidade do estoque
                    stmtEstoque.setInt(1, item.getQuantidade());
                    stmtEstoque.setInt(2, item.getProdutoId());
                    stmtEstoque.addBatch();
                }
                stmtItem.executeBatch();
                stmtEstoque.executeBatch();
            }

            // 3. Atualiza quantidade disponível do Cupom (se houver)
            if (cupomAplicado != null) {
                try (PreparedStatement stmtCupom = conn.prepareStatement(sqlAtualizaCupom)) {
                    stmtCupom.setInt(1, cupomAplicado.getId());
                    stmtCupom.executeUpdate();
                }
            }

            conn.commit(); // Confirma todas as operações juntas no banco
            System.out.println("Venda finalizada com sucesso e estoque atualizado!");
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("Erro na transação de venda: " + e.getMessage());
        }
    }
}