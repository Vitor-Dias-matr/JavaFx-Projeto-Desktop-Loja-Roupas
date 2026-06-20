package com.example.vendaroupas.view;

import com.example.vendaroupas.model.Categoria;
import com.example.vendaroupas.model.Produto;
import com.example.vendaroupas.controller.NavegacaoController;
import com.example.vendaroupas.model.ProdutoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ProdutoViewController implements Initializable {

    @FXML private TextField txtNome;
    @FXML private ComboBox<Categoria> cbCategoria;
    @FXML private TextField txtTamanho;
    @FXML private TextField txtCor;
    @FXML private TextField txtPreco;
    @FXML private TextField txtQuantidade;

    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, Integer> colId;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, Categoria> colCategoria;
    @FXML private TableColumn<Produto, String> colTamanho;
    @FXML private TableColumn<Produto, String> colCor;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, Integer> colQuantidade;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final NavegacaoController navegacaoController = new NavegacaoController();
    private ObservableList<Produto> listaControle;
    private Produto produtoSelecionado = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializa o ComboBox com os valores do seu Enum
        cbCategoria.setItems(FXCollections.observableArrayList(Categoria.values()));

        // Configura as colunas da tabela mapeando com os atributos da sua classe Produto
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colTamanho.setCellValueFactory(new PropertyValueFactory<>("tamanho"));
        colCor.setCellValueFactory(new PropertyValueFactory<>("cor"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        atualizarTabela();
    }

    private void atualizarTabela() {
        listaControle = FXCollections.observableArrayList(produtoDAO.listarTodos());
        tabelaProdutos.setItems(listaControle);
    }

    @FXML
    void aoSalvar(ActionEvent event) {
        if (txtNome.getText().isEmpty() || cbCategoria.getValue() == null ||
                txtTamanho.getText().isEmpty() || txtCor.getText().isEmpty() || txtPreco.getText().isEmpty()) {

            exibirAlerta("Erro de Validação", "Por favor, preencha todos os campos obrigatórios.", Alert.AlertType.WARNING);
            return;
        }

        try {
            double preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
            int quantidade = Integer.parseInt(txtQuantidade.getText());

            if (produtoSelecionado == null) {
                // Inserir Novo Produto
                Produto novo = new Produto(txtNome.getText(), cbCategoria.getValue(), txtTamanho.getText(), txtCor.getText(), preco, quantidade);
                produtoDAO.salvar(novo);
            } else {
                // Atualizar Existente
                produtoSelecionado.setNome(txtNome.getText());
                produtoSelecionado.setCategoria(cbCategoria.getValue());
                produtoSelecionado.setTamanho(txtTamanho.getText());
                produtoSelecionado.setCor(txtCor.getText());
                produtoSelecionado.setPreco(preco);
                produtoSelecionado.setQuantidade(quantidade);

                produtoDAO.atualizar(produtoSelecionado);
                produtoSelecionado = null;
            }

            limparFormulario();
            atualizarTabela();

        } catch (NumberFormatException e) {
            exibirAlerta("Erro no Preço", "Digite um valor numérico válido para o preço.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void aoEditar(ActionEvent event) {
        produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (produtoSelecionado != null) {
            txtNome.setText(produtoSelecionado.getNome());
            cbCategoria.setValue(produtoSelecionado.getCategoria());
            txtTamanho.setText(produtoSelecionado.getTamanho());
            txtCor.setText(produtoSelecionado.getCor());
            txtPreco.setText(String.valueOf(produtoSelecionado.getPreco()));
            txtQuantidade.setText(String.valueOf(produtoSelecionado.getQuantidade()));
        } else {
            exibirAlerta("Nenhum produto selecionado", "Selecione um produto na tabela para editar.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    void aoExcluir(ActionEvent event) {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            produtoDAO.deletar(selecionado.getId());
            atualizarTabela();
            limparFormulario();
        } else {
            exibirAlerta("Nenhum produto selecionado", "Selecione um produto na tabela para excluir.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    void aoLimpar(ActionEvent event) {
        limparFormulario();
    }

    private void limparFormulario() {
        txtNome.clear();
        cbCategoria.setValue(null);
        txtTamanho.clear();
        txtCor.clear();
        txtPreco.clear();
        txtQuantidade.clear();
        produtoSelecionado = null;
    }

    private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    @FXML
    public void irParaVendas() {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) txtNome.getScene().getWindow();
            navegacaoController.exibirTelaVendas(stage);
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erro de Navegação");
            alert.setContentText("Não foi possível voltar para a tela de vendas: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
