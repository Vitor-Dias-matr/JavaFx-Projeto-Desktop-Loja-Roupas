package com.example.vendaroupas.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Acesso aos dados de cupons, pertencente ao Model do MVC. */
public class CupomDAO {

    public void salvar(Cupom cupom) throws SQLException {
        String sql = "INSERT INTO cupom (codigo, porcentagem_desconto, data_validade, situacao) VALUES (?, ?, ?, ?)";
        try (Connection conexao = FabricaDeConexao.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherComando(comando, cupom);
            comando.executeUpdate();
            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    cupom.setId(chaves.getInt(1));
                }
            }
        }
    }

    public List<Cupom> listarTodos() throws SQLException {
        String sql = "SELECT * FROM cupom ORDER BY codigo";
        List<Cupom> cupons = new ArrayList<>();
        try (Connection conexao = FabricaDeConexao.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                cupons.add(mapear(resultado));
            }
        }
        return cupons;
    }

    public Cupom buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM cupom WHERE UPPER(codigo) = UPPER(?) LIMIT 1";
        try (Connection conexao = FabricaDeConexao.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, codigo.trim());
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? mapear(resultado) : null;
            }
        }
    }

    public void atualizar(Cupom cupom) throws SQLException {
        String sql = "UPDATE cupom SET codigo = ?, porcentagem_desconto = ?, data_validade = ?, situacao = ? WHERE id = ?";
        try (Connection conexao = FabricaDeConexao.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencherComando(comando, cupom);
            comando.setInt(5, cupom.getId());
            comando.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM cupom WHERE id = ?";
        try (Connection conexao = FabricaDeConexao.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }

    private void preencherComando(PreparedStatement comando, Cupom cupom) throws SQLException {
        comando.setString(1, cupom.getCodigo().trim().toUpperCase());
        comando.setDouble(2, cupom.getPorcentagemDesconto());
        if (cupom.getDataValidade() == null) {
            comando.setNull(3, java.sql.Types.TIMESTAMP);
        } else {
            comando.setTimestamp(3, Timestamp.valueOf(cupom.getDataValidade()));
        }
        comando.setBoolean(4, cupom.isAtivo());
    }

    private Cupom mapear(ResultSet resultado) throws SQLException {
        Cupom cupom = new Cupom();
        cupom.setId(resultado.getInt("id"));
        cupom.setCodigo(resultado.getString("codigo"));
        cupom.setPorcentagemDesconto(resultado.getDouble("porcentagem_desconto"));
        cupom.setQuantidade(resultado.getInt("quantidade"));
        Timestamp validade = resultado.getTimestamp("data_validade");
        cupom.setDataValidade(validade == null ? null : validade.toLocalDateTime());
        cupom.setAtivo(resultado.getBoolean("situacao"));
        return cupom;
    }
}
