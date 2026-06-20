package com.example.vendaroupas.view;

import com.example.vendaroupas.controller.NavegacaoController;
import com.example.vendaroupas.model.Cupom;
import com.example.vendaroupas.model.CupomDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CupomViewController implements Initializable {
    @FXML private TextField txtCodigo;
    @FXML private TextField txtPorcentagem;
    @FXML private DatePicker dpDataValidade;
    @FXML private CheckBox chkAtivo;
    @FXML private TableView<Cupom> tabelaCupons;
    @FXML private TableColumn<Cupom, Integer> colId;
    @FXML private TableColumn<Cupom, String> colCodigo;
    @FXML private TableColumn<Cupom, Double> colPorcentagem;
    @FXML private TableColumn<Cupom, LocalDateTime> colDataValidade;
    @FXML private TableColumn<Cupom, Boolean> colSituacao;

    private final CupomDAO cupomDAO = new CupomDAO();
    private final NavegacaoController navegacaoController = new NavegacaoController();
    private Cupom cupomSelecionado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colPorcentagem.setCellValueFactory(new PropertyValueFactory<>("porcentagemDesconto"));
        colDataValidade.setCellValueFactory(new PropertyValueFactory<>("dataValidade"));
        colSituacao.setCellValueFactory(new PropertyValueFactory<>("ativo"));

        colPorcentagem.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(Double valor, boolean vazio) {
                super.updateItem(valor, vazio);
                setText(vazio || valor == null ? null : String.format("%.2f%%", valor));
            }
        });
        colDataValidade.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime valor, boolean vazio) {
                super.updateItem(valor, vazio);
                setText(vazio || valor == null ? null : valor.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        });
        colSituacao.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean ativo, boolean vazio) {
                super.updateItem(ativo, vazio);
                setText(vazio || ativo == null ? null : (ativo ? "Ativo" : "Inativo"));
            }
        });

        chkAtivo.setSelected(true);
        carregarTabela();
    }

    @FXML
    private void aoSalvar() {
        String codigo = txtCodigo.getText().trim();
        LocalDate validade = dpDataValidade.getValue();
        if (codigo.isEmpty() || txtPorcentagem.getText().isBlank() || validade == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Validação", "Preencha o nome, a porcentagem e a data de validade do cupom.");
            return;
        }

        try {
            double porcentagem = Double.parseDouble(txtPorcentagem.getText().replace(",", "."));
            if (porcentagem <= 0 || porcentagem > 100) {
                throw new NumberFormatException();
            }

            Cupom cupom = cupomSelecionado == null ? new Cupom() : cupomSelecionado;
            cupom.setCodigo(codigo);
            cupom.setPorcentagemDesconto(porcentagem);
            cupom.setDataValidade(validade.atTime(LocalTime.MAX));
            cupom.setAtivo(chkAtivo.isSelected());

            if (cupomSelecionado == null) {
                cupomDAO.salvar(cupom);
            } else {
                cupomDAO.atualizar(cupom);
            }
            limparFormulario();
            carregarTabela();
        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.WARNING, "Porcentagem inválida", "Informe uma porcentagem maior que 0 e menor ou igual a 100.");
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro ao salvar", "Não foi possível salvar o cupom. Verifique se o nome já existe.");
        }
    }

    @FXML
    private void aoEditar() {
        cupomSelecionado = tabelaCupons.getSelectionModel().getSelectedItem();
        if (cupomSelecionado == null) {
            exibirAlerta(Alert.AlertType.INFORMATION, "Seleção", "Selecione um cupom para editar.");
            return;
        }
        txtCodigo.setText(cupomSelecionado.getCodigo());
        txtPorcentagem.setText(String.valueOf(cupomSelecionado.getPorcentagemDesconto()));
        dpDataValidade.setValue(cupomSelecionado.getDataValidade() == null ? null : cupomSelecionado.getDataValidade().toLocalDate());
        chkAtivo.setSelected(cupomSelecionado.isAtivo());
    }

    @FXML
    private void aoExcluir() {
        Cupom selecionado = tabelaCupons.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            exibirAlerta(Alert.AlertType.INFORMATION, "Seleção", "Selecione um cupom para excluir.");
            return;
        }
        try {
            cupomDAO.deletar(selecionado.getId());
            limparFormulario();
            carregarTabela();
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro ao excluir", "O cupom não pode ser excluído porque pode estar vinculado a uma venda. Você pode desativá-lo.");
        }
    }

    @FXML
    private void aoLimpar() {
        limparFormulario();
    }

    @FXML
    private void irParaVendas() {
        try {
            navegacaoController.exibirTelaVendas((Stage) txtCodigo.getScene().getWindow());
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de navegação", "Não foi possível abrir a tela de vendas.");
        }
    }

    private void carregarTabela() {
        try {
            tabelaCupons.setItems(FXCollections.observableArrayList(cupomDAO.listarTodos()));
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de conexão", "Não foi possível carregar os cupons.");
        }
    }

    private void limparFormulario() {
        txtCodigo.clear();
        txtPorcentagem.clear();
        dpDataValidade.setValue(null);
        chkAtivo.setSelected(true);
        tabelaCupons.getSelectionModel().clearSelection();
        cupomSelecionado = null;
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
