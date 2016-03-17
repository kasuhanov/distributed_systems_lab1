package ru.kasuhanov.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FxApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));
        Parent root = (Parent)loader.load();
        Controller controller = (Controller) loader.getController();
        primaryStage.setOnCloseRequest(event -> {
            controller.close();
        });
        primaryStage.setTitle("Socket client");
        primaryStage.setScene(new Scene(root, 420, 300));
        primaryStage.show();
    }
}
