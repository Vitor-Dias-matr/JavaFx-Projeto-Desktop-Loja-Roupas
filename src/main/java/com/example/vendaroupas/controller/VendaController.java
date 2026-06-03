package com.example.vendaroupas.controller;

import com.example.vendaroupas.model.Cupom;
import com.example.vendaroupas.model.ItemVenda;
import com.example.vendaroupas.model.Produto;
import com.example.vendaroupas.model.Venda;
import com.example.vendaroupas.repository.ProdutoDAO;

// IMPORTS QUE ESTAVAM FALTANDO:
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class VendaController implements Initializable { // CORREÇÃO: Adicionado implements Initializable

    private ProdutoDAO produtoDAO = new ProdutoDAO();

    // Componentes injetados do arquivo FXML
    @FXML private TableView<ItemVenda> tabelaCarrinho;

    // CORREÇÃO: Adicionadas as declarações das colunas da tabela que faltavam no Java
    @FXML private TableColumn<ItemVenda, String> colItemProduto;
    @FXML private TableColumn<ItemVenda, Double> colItemPrecoUnitario;
    @FXML private TableColumn<ItemVenda, Integer> colItemQuantidade;
    @FXML private TableColumn<ItemVenda, Double> colItemTotal;

    @FXML private ComboBox<String> cbFormaPagamento;

    @FXML private ComboBox<Produto> cbProduto;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtCupom;

    @FXML private Label lblSubtotal;
    @FXML private Label lblDesconto;
    @FXML private Label lblTotal;

    private Cupom cupomAtual = null;
    private double subtotalVenda = 0.0;
    private double descontoVenda = 0.0;
    private double totalVenda = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Configura as colunas da tabela do Carrinho (Mapeia com os atributos de ItemVenda)
        colItemProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
        colItemPrecoUnitario.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
        colItemQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colItemTotal.setCellValueFactory(new PropertyValueFactory<>("totalItem"));

        // 2. Alimenta o ComboBox de Formas de Pagamento
        cbFormaPagamento.setItems(FXCollections.observableArrayList(
                "Dinheiro", "Cartão de Crédito", "Cartão de Débito", "Pix"
        ));

        // 3. Carrega os produtos do banco de dados para o ComboBox de seleção
        carregarProdutosNoComboBox();

        // 4. Configuração visual para mostrar Nome e Preço dentro do ComboBox de Produtos
        configurarExibicaoComboBoxProduto();
    }

    @FXML
    public void aoFinalizarVenda() {
        ObservableList<ItemVenda> itensNoCarrinho = tabelaCarrinho.getItems();

        if (itensNoCarrinho == null || itensNoCarrinho.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Carrinho Vazio",
                    "Não é possível finalizar uma venda sem produtos adicionados ao carrinho.");
            return;
        }

        String formaPagamento = cbFormaPagamento.getValue();
        if (formaPagamento == null || formaPagamento.trim().isEmpty() || formaPagamento.equals("Selecione...")) {
            exibirAlerta(Alert.AlertType.WARNING, "Forma de Pagamento Ausente",
                    "Por favor, selecione a forma de pagamento antes de confirmar.");
            return;
        }

        Venda novaVenda = new Venda();
        novaVenda.setSubtotal(subtotalVenda);
        novaVenda.setDesconto(descontoVenda);
        novaVenda.setTotal(totalVenda);
        novaVenda.setFormaPagamento(formaPagamento);

        if (cupomAtual != null) {
            novaVenda.setCupomId(cupomAtual.getId());
        }

        try {
            // CORREÇÃO: Passando a lista convertida corretamente
            //finalizarVendaNoBanco(novaVenda, itensNoCarrinho, cupomAtual);
            produtoDAO.finalizarVenda(novaVenda, itensNoCarrinho, cupomAtual);

            exibirAlerta(Alert.AlertType.INFORMATION, "Venda Concluída",
                    "A venda foi registrada com sucesso!\nO estoque e os cupons foram atualizados.");

            limparTelaVenda();

        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro ao Finalizar Venda",
                    "Ocorreu um erro crítico no banco de dados: " + e.getMessage());
        }
    }

    @FXML
    public void aoAdicionarProduto() {
        // 1. Pegar o produto selecionado no ComboBox
        Produto produtoSelecionado = cbProduto.getValue();
        if (produtoSelecionado == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Inválida", "Por favor, selecione um produto antes de adicionar.");
            return;
        }

        // 2. Pegar e validar a quantidade digitada
        int qtdDesejada;
        try {
            qtdDesejada = Integer.parseInt(txtQuantidade.getText().trim());
            if (qtdDesejada <= 0) {
                exibirAlerta(Alert.AlertType.WARNING, "Quantidade Inválida", "A quantidade deve ser maior que zero.");
                return;
            }
        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.WARNING, "Quantidade Inválida", "Digite um número inteiro válido para a quantidade.");
            return;
        }

        // 3. Regra do Diagrama: Verificar estoque disponível
        if (qtdDesejada > produtoSelecionado.getQuantidade()) {
            exibirAlerta(Alert.AlertType.ERROR, "Sem Estoque",
                    "Estoque insuficiente!\nProduto: " + produtoSelecionado.getNome() +
                            "\nDisponível: " + (int)produtoSelecionado.getQuantidade() + " unidades."); // Cast para int se for double no seu banco
            return;
        }

        // 4. Verificar se o produto já está no carrinho para somar a quantidade em vez de duplicar a linha
        ObservableList<ItemVenda> itensNoCarrinho = tabelaCarrinho.getItems();
        ItemVenda itemExistente = null;

        for (ItemVenda item : itensNoCarrinho) {
            if (item.getProdutoId() == produtoSelecionado.getId()) {
                itemExistente = item;
                break;
            }
        }

        if (itemExistente != null) {
            // Valida se a soma das quantidades não estoura o estoque total
            if ((itemExistente.getQuantidade() + qtdDesejada) > produtoSelecionado.getQuantidade()) {
                exibirAlerta(Alert.AlertType.ERROR, "Limite de Estoque", "A soma das quantidades no carrinho supera o estoque disponível.");
                return;
            }
            itemExistente.setQuantidade(itemExistente.getQuantidade() + qtdDesejada);
        } else {
            // Cria um novo item para a tabela do carrinho
            ItemVenda novoItem = new ItemVenda(
                    produtoSelecionado.getId(),
                    produtoSelecionado.getNome(),
                    qtdDesejada,
                    produtoSelecionado.getPreco()
            );
            tabelaCarrinho.getItems().add(novoItem);
        }

        tabelaCarrinho.refresh();

        atualizarTotaisDaVenda();

        cbProduto.setValue(null);
        txtQuantidade.setText("1");
    }

    @FXML
    public void aoAplicarCupom() {
        // TODO:  Implementação futura para validar cupom
    }

    @FXML
    public void irParaGerenciarProdutos() {
        try {
            // 1. Pega a janela (Stage) atual a partir de qualquer componente da tela
            javafx.stage.Stage stage = (javafx.stage.Stage) cbFormaPagamento.getScene().getWindow();

            // 2. Aponta para o arquivo FXML da tela de estoque (certifique-se de que o nome está idêntico)
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/vendaroupas/gerenciar-produtos.fxml")
            );

            // 3. Carrega a nova cena mantendo o tamanho padrão do seu projeto
            javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), 1024, 600);

            stage.setTitle("Sistema de Loja - Gerenciar Estoque");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Navegação",
                    "Não foi possível carregar a tela de estoque: " + e.getMessage());
        }
    }

    private void carregarProdutosNoComboBox() {
        try {
            // 1. Busca a lista real vinda do método listarTodos() que você acabou de mostrar
            List<Produto> listaProdutos = produtoDAO.listarTodos();

            // 2. Passa a lista recuperada para o ComboBox da tela
            cbProduto.setItems(FXCollections.observableArrayList(listaProdutos));

            System.out.println("Produtos carregados do banco para o ComboBox com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao carregar produtos no ComboBox: " + e.getMessage());
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Conexão",
                    "Não foi possível carregar os produtos do banco de dados.");
        }
    }

    // Configura o design do combobox de produtos para não exibir o endereço de memória do objeto
    private void configurarExibicaoComboBoxProduto() {
        cbProduto.setCellFactory(lv -> new ListCell<Produto>() {
            @Override
            protected void updateItem(Produto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNome() + " (R$ " + item.getPreco() + ")");
            }
        });

        cbProduto.setButtonCell(new ListCell<Produto>() {
            @Override
            protected void updateItem(Produto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNome());
            }
        });
    }

    private void limparTelaVenda() {
        tabelaCarrinho.getItems().clear();
        cbFormaPagamento.setValue(null);
        cbProduto.setValue(null);
        txtQuantidade.setText("1");
        if (txtCupom != null) txtCupom.clear();

        cupomAtual = null;
        subtotalVenda = 0.0;
        descontoVenda = 0.0;
        totalVenda = 0.0;

        lblSubtotal.setText("Subtotal: R$ 0,00");
        lblDesconto.setText("Desconto: R$ 0,00");
        lblTotal.setText("TOTAL: R$ 0,00");
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void finalizarVendaNoBanco(Venda venda, List<ItemVenda> itens, Cupom cupomAplicado) throws java.sql.SQLException {
    }

    private void atualizarTotaisDaVenda() {
        subtotalVenda = 0.0;

        // Soma o total de cada item no carrinho
        for (ItemVenda item : tabelaCarrinho.getItems()) {
            subtotalVenda += item.getTotalItem();
        }

        // Calcula desconto caso um cupom válido esteja ativo
        if (cupomAtual != null && cupomAtual.isValid()) {
            descontoVenda = subtotalVenda * (cupomAtual.getPorcentagemDesconto() / 100.0);
        } else {
            descontoVenda = 0.0;
        }

        totalVenda = subtotalVenda - descontoVenda;

        // Atualiza os Labels da tela formatando como moeda nacional
        lblSubtotal.setText(String.format("Subtotal: R$ %.2f", subtotalVenda));
        lblDesconto.setText(String.format("Desconto: R$ %.2f", descontoVenda));
        lblTotal.setText(String.format("TOTAL: R$ %.2f", totalVenda));
    }
}