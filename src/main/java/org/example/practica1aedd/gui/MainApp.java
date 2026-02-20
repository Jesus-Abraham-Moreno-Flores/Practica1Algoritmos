package org.example.practica1aedd.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.practica1aedd.solitaire.SolitaireGame;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        SolitaireGame juego = new SolitaireGame();
        GameController controller = new GameController(juego, stage);

        Scene scene = new Scene(controller.getRoot(), 1100, 720);
        scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm());

        stage.setTitle("Solitaire Game");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
