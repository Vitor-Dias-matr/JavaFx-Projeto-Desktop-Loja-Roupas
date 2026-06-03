module com.example.vendaroupas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Necessário para a conexão com o MySQL

    requires io.github.cdimascio.dotenv.java;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    // Permite que o JavaFX acesse a classe Main/Application
    opens com.example.vendaroupas to javafx.fxml;
    exports com.example.vendaroupas;

    // --- ADICIONE ESTAS DUAS LINHAS ABAIXO ---
    // Permite que o FXMLLoader instancie e injete os componentes no seu Controller
    opens com.example.vendaroupas.controller to javafx.fxml;
    exports com.example.vendaroupas.controller;

    // Permite que as tabelas (TableView) acessem os atributos da classe Produto
    opens com.example.vendaroupas.model to javafx.base;
    exports com.example.vendaroupas.model;
}