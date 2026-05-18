package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.engine.GameEngine;
import com.example.marioparty.engine.GameScene;
import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.minigames.MiniGame;
import com.example.marioparty.minigames.MiniGameRegistry;
import com.example.marioparty.model.Player;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiniGameScene extends GameScene {

    private static final int REWARD_COINS = 10;
    private static final Random RNG = new Random();

    private final MiniGame selectedMiniGame;
    private final boolean returnToMenuAfterFinish;
    private final boolean returnToTestBoardAfterFinish;
    private MiniGame miniGame;
    private boolean rewardGiven = false;
    private double resultTimer = 0;

    private Group introGroup;
    private Rectangle resultOverlay;
    private Text resultText;
    private Text rewardText;

    public MiniGameScene(GameEngine engine) {
        this(engine, null, false, false);
    }

    public MiniGameScene(GameEngine engine, MiniGame selectedMiniGame, boolean returnToMenuAfterFinish) {
        this(engine, selectedMiniGame, returnToMenuAfterFinish, false);
    }

    public MiniGameScene(
            GameEngine engine,
            MiniGame selectedMiniGame,
            boolean returnToMenuAfterFinish,
            boolean returnToTestBoardAfterFinish) {
        super(engine);
        this.selectedMiniGame = selectedMiniGame;
        this.returnToMenuAfterFinish = returnToMenuAfterFinish;
        this.returnToTestBoardAfterFinish = returnToTestBoardAfterFinish;
    }

    @Override
    public void onEnter() {
        Pane pane = engine.getPane();
        if (selectedMiniGame != null) {
            miniGame = selectedMiniGame;
        } else {
            miniGame = MiniGameRegistry.randomFor(engine.getState().getPlayers(), pane);
        }

        pane.getChildren().add(new Rectangle(Main.WIDTH, Main.HEIGHT, Color.web("#000033")));

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
            rewardGiven = true;
            StringBuilder rewardMessage = new StringBuilder();
            if (winner != null) {
                if (engine.getState().getPlayers().contains(winner)) {
                    winner.addCoins(REWARD_COINS);
                    rewardMessage.append(winner.getName())
                            .append(": +")
                            .append(REWARD_COINS)
                            .append(" Münzen");
                }
                resultText.setText(winner.getName() + " gewinnt!");
            } else {
                resultText.setText("Unentschieden!");
            }
            Player benchWinner = pickBenchWinner();
            if (benchWinner != null) {
                benchWinner.addCoins(REWARD_COINS);
                if (!rewardMessage.isEmpty()) {
                    rewardMessage.append("\n");
                }
                rewardMessage.append(benchWinner.getName())
                        .append(" gewinnt parallel: +")
                        .append(REWARD_COINS)
                        .append(" Münzen");
            }
            if (!rewardMessage.isEmpty()) {
                rewardText.setText(rewardMessage.toString());
                rewardText.setVisible(true);
                rewardText.toFront();
            }
            resultOverlay.setVisible(true);
            resultOverlay.toFront();
            resultText.setVisible(true);
            resultText.toFront();
        }

        resultTimer += dt;
        if (resultTimer > 3.0) {
            if (returnToTestBoardAfterFinish) {
                engine.setScene(new BoardScene(engine, true));
            } else if (returnToMenuAfterFinish) {
                engine.setScene(new TestModeScene(engine, engine.getState().getStarsToWin()));
            } else {
                engine.setScene(new BoardScene(engine));
            }
        }
    }

    private Player pickBenchWinner() {
        List<Player> participants = miniGame.getParticipants();
        List<Player> bench = new ArrayList<>();
        for (Player p : engine.getState().getPlayers()) {
            if (!participants.contains(p)) {
                bench.add(p);
            }
        }
        if (bench.isEmpty()) {
            return null;
        }
        return bench.get(RNG.nextInt(bench.size()));
    }
}
