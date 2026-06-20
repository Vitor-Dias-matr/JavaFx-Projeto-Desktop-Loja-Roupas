package com.example.vendaroupas.view;

import javafx.application.Application;
import com.example.vendaroupas.controller.NavegacaoController;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Carrega a tela de vendas como tela principal inicial
        new NavegacaoController().exibirTelaVendas(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
