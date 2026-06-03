package com.example.vendaroupas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Carrega a tela de vendas como tela principal inicial
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("efetuar-venda.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 600);
        stage.setTitle("Sistema de Vendas - Caixa Aberto");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}