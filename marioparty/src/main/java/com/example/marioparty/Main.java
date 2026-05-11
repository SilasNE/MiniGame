package com.example.marioparty;

import com.example.marioparty.engine.GameEngine;
import com.example.marioparty.scenes.MenuScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class    Main extends Application {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;

    @Override
    public void start(Stage stage) {
        // GameEngine erwartet ein Pane (Scene Graph). Kein Canvas als Root — sonst NoSuchMethodError bei veralteten .class-Dateien.
        Pane root = new Pane();
        Scene fxScene = new Scene(root, WIDTH, HEIGHT);

        GameEngine engine = new GameEngine(root, fxScene);
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