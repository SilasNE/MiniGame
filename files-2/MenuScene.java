package game.scenes;

import game.Main;
import game.engine.GameEngine;
import game.engine.GameScene;
import game.engine.InputHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Startbildschirm. Wartet auf Leertaste, dann zur BoardScene.
 */
public class MenuScene extends GameScene {

    private double pulse = 0;

    public MenuScene(GameEngine engine) {
        super(engine);
    }

    @Override
    public void update(double dt, InputHandler input) {
        pulse += dt;
        if (input.wasJustPressed(KeyCode.SPACE)) {
            engine.setScene(new BoardScene(engine));
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.web("#1a1a2e"));
        gc.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);

        // Titel
        gc.setFill(Color.web("#ffd60a"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        String title = "MINI MARIO PARTY";
        gc.fillText(title, Main.WIDTH / 2.0 - 360, Main.HEIGHT / 2.0 - 60);

        // Pulsierender "Press Space"-Hinweis
        double alpha = 0.5 + 0.5 * Math.sin(pulse * 3);
        gc.setFill(Color.color(1, 1, 1, alpha));
        gc.setFont(Font.font("Arial", 28));
        gc.fillText("Drücke LEERTASTE zum Starten",
                Main.WIDTH / 2.0 - 220, Main.HEIGHT / 2.0 + 40);

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Arial", 18));
        gc.fillText("4 Spieler  •  5 Runden  •  Wer hat die meisten Sterne?",
                Main.WIDTH / 2.0 - 250, Main.HEIGHT / 2.0 + 100);
    }
}
