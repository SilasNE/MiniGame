package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.engine.GameEngine;
import com.example.marioparty.engine.GameScene;
import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.minigames.ButtonMashGame;
import com.example.marioparty.minigames.MiniGame;
import com.example.marioparty.model.Player;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MiniGameScene extends GameScene {

    private MiniGame miniGame;
    private boolean rewardGiven = false;
    private double resultTimer = 0;

    private Group introGroup;
    private Rectangle resultOverlay;
    private Text resultText;
    private Text rewardText;

    public MiniGameScene(GameEngine engine) {
        super(engine);
    }

    @Override
    public void onEnter() {
        Pane pane = engine.getPane();
        miniGame = new ButtonMashGame(engine.getState().getPlayers(), pane);

        pane.getChildren().add(new Rectangle(Main.WIDTH, Main.HEIGHT, Color.web("#000033")));

        // Intro-Overlay
        Text titleText = new Text(Main.WIDTH / 2.0 - 280, 150, "MINIGAME: " + miniGame.getName());
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleText.setFill(Color.YELLOW);

        Text descText = new Text(Main.WIDTH / 2.0 - 320, 250, miniGame.getDescription());
        descText.setFont(Font.font("Arial", 24));
        descText.setFill(Color.WHITE);

        Text startText = new Text(Main.WIDTH / 2.0 - 220, 450, "Drücke LEERTASTE zum Starten");
        startText.setFont(Font.font("Arial", 28));
        startText.setFill(Color.LIGHTYELLOW);

        introGroup = new Group(titleText, descText, startText);
        pane.getChildren().add(introGroup);

        // Ergebnis-Overlay (zunächst versteckt)
        resultOverlay = new Rectangle(Main.WIDTH, Main.HEIGHT, Color.rgb(0, 0, 0, 0.7));
        resultOverlay.setVisible(false);

        resultText = new Text(Main.WIDTH / 2.0 - 200, 350, "");
        resultText.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        resultText.setFill(Color.GOLD);
        resultText.setVisible(false);

        rewardText = new Text(Main.WIDTH / 2.0 - 150, 410, "+10 Münzen");
        rewardText.setFont(Font.font("Arial", 28));
        rewardText.setFill(Color.WHITE);
        rewardText.setVisible(false);

        pane.getChildren().addAll(resultOverlay, resultText, rewardText);
    }

    @Override
    public void update(double dt, InputHandler input) {
        if (!miniGame.isStarted()) {
            if (input.wasJustPressed(KeyCode.SPACE)) {
                engine.getPane().getChildren().remove(introGroup);
                miniGame.start();
            }
            return;
        }

        if (!miniGame.isFinished()) {
            miniGame.update(dt, input);
            return;
        }

        if (!rewardGiven) {
            Player winner = miniGame.getWinner();
            winner.addCoins(10);
            rewardGiven = true;
            resultText.setText(winner.getName() + " gewinnt!");
            resultOverlay.setVisible(true);
            resultText.setVisible(true);
            rewardText.setVisible(true);
        }

        resultTimer += dt;
        if (resultTimer > 3.0) {
            engine.setScene(new BoardScene(engine));
        }
    }
}