package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.engine.GameEngine;
import com.example.marioparty.engine.GameScene;
import com.example.marioparty.engine.InputHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MenuScene extends GameScene {

    private double pulse = 0;
    private Text pressSpace;

    public MenuScene(GameEngine engine) {
        super(engine);
    }

    @Override
    public void onEnter() {
        pulse = 0;
        Pane pane = engine.getPane();

        Rectangle bg = new Rectangle(Main.WIDTH, Main.HEIGHT, Color.web("#1a1a2e"));

        Text title = new Text(Main.WIDTH / 2.0 - 360, Main.HEIGHT / 2.0 - 60, "MINI MARIO PARTY");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        title.setFill(Color.web("#ffd60a"));

        pressSpace = new Text(Main.WIDTH / 2.0 - 220, Main.HEIGHT / 2.0 + 40, "Drücke LEERTASTE zum Starten");
        pressSpace.setFont(Font.font("Arial", 28));
        pressSpace.setFill(Color.WHITE);

        Text info = new Text(Main.WIDTH / 2.0 - 250, Main.HEIGHT / 2.0 + 100,
                "4 Spieler  •  5 Runden  •  Wer hat die meisten Sterne?");
        info.setFont(Font.font("Arial", 18));
        info.setFill(Color.LIGHTGRAY);

        pane.getChildren().addAll(bg, title, pressSpace, info);
    }

    @Override
    public void update(double dt, InputHandler input) {
        pulse += dt;
        pressSpace.setOpacity(0.5 + 0.5 * Math.sin(pulse * 3));

        if (input.wasJustPressed(KeyCode.SPACE)) {
            engine.setScene(new BoardScene(engine));
        }
    }
}