package com.example.vendaroupas.controller;

import com.example.vendaroupas.view.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controla exclusivamente o fluxo de navegação entre as telas do sistema.
 */
public class NavegacaoController {

    public void exibirTelaVendas(Stage stage) throws IOException {
        trocarCena(stage, "efetuar-venda.fxml", "Sistema de Vendas - Caixa Aberto");
    }

    public void exibirTelaProdutos(Stage stage) throws IOException {
        trocarCena(stage, "gerenciar-produtos.fxml", "Sistema de Loja - Gerenciar Estoque");
    }

    private void trocarCena(Stage stage, String arquivoFxml, String titulo) throws IOException {
        FXMLLoader carregador = new FXMLLoader(HelloApplication.class.getResource(arquivoFxml));
        stage.setScene(new Scene(carregador.load(), 1024, 600));
        stage.setTitle(titulo);
    }
}
