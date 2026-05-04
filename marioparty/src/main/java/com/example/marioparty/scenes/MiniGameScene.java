package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.engine.GameEngine;
import com.example.marioparty.engine.GameScene;
import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.minigames.ButtonMashGame;
import com.example.marioparty.minigames.MiniGame;
import com.example.marioparty.model.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Hülle für Minispiele. Zeigt Intro, lässt das MiniGame laufen, zeigt Ergebnis,
 * vergibt Belohnung und kehrt zur BoardScene zurück.
 *
 * Erweiterung: zufälliges MiniGame aus einer Liste auswählen.
 */
public class MiniGameScene extends GameScene {

    private final MiniGame miniGame;
    private boolean rewardGiven = false;
    private double resultTimer = 0;

    public MiniGameScene(GameEngine engine) {
        super(engine);
        // Erweiterung: hier zufällig aus einer Minigame-Liste wählen
        this.miniGame = new ButtonMashGame(engine.getState().getPlayers());
    }

    @Override
    public void update(double dt, InputHandler input) {
        if (!miniGame.isStarted()) {
            if (input.wasJustPressed(KeyCode.SPACE)) {
                miniGame.start();
            }
            return;
        }

        if (!miniGame.isFinished()) {
            miniGame.update(dt, input);
            return;
        }

        // Belohnung genau einmal vergeben
        if (!rewardGiven) {
            Player winner = miniGame.getWinner();
            winner.addStars(1);
            winner.addCoins(10);
            rewardGiven = true;
        }

        resultTimer += dt;
        if (resultTimer > 3.0) {
            engine.setScene(new BoardScene(engine));
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.web("#000033"));
        gc.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);

        if (!miniGame.isStarted()) {
            gc.setFill(Color.YELLOW);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
            gc.fillText("MINIGAME: " + miniGame.getName(),
                    Main.WIDTH / 2.0 - 280, 150);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 24));
            gc.fillText(miniGame.getDescription(),
                    Main.WIDTH / 2.0 - 320, 250);

            gc.setFill(Color.LIGHTYELLOW);
            gc.setFont(Font.font("Arial", 28));
            gc.fillText("Drücke LEERTASTE zum Starten",
                    Main.WIDTH / 2.0 - 220, 450);
            return;
        }

        miniGame.render(gc);

        if (miniGame.isFinished()) {
            // halbtransparenter Overlay
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);

            Player winner = miniGame.getWinner();
            gc.setFill(Color.GOLD);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
            gc.fillText(winner.getName() + " gewinnt!",
                    Main.WIDTH / 2.0 - 200, 350);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 28));
            gc.fillText("+1 Stern   +10 Münzen",
                    Main.WIDTH / 2.0 - 150, 410);
        }
    }
}
