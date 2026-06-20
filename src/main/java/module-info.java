module com.example.vendaroupas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Necessário para a conexão com o MySQL

    requires io.github.cdimascio.dotenv.java;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    // View: telas, aplicação JavaFX e controladores ligados aos arquivos FXML.
    opens com.example.vendaroupas.view to javafx.fxml;
    exports com.example.vendaroupas.view;

    // Controller: fluxo de navegação entre View e Model.
    exports com.example.vendaroupas.controller;

    // Model: entidades, regras de negócio e acesso aos dados.
    opens com.example.vendaroupas.model to javafx.base;
    exports com.example.vendaroupas.model;
}
