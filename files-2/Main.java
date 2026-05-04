package game;

import game.engine.GameEngine;
import game.scenes.MenuScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Einstiegspunkt. Erzeugt Fenster, Canvas und startet die GameEngine
 * mit der Anfangsszene (Menü).
 */
public class Main extends Application {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        Pane root = new Pane(canvas);
        Scene fxScene = new Scene(root);

        GameEngine engine = new GameEngine(canvas, fxScene);
        engine.setScene(new MenuScene(engine));

        stage.setTitle("Mini Mario Party");
        stage.setScene(fxScene);
        stage.setResizable(false);
        stage.show();

        engine.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
