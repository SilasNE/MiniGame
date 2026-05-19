package com.example.marioparty.minigames;

import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Player;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class DinoGame extends MiniGame {

    private final List<Player> players;
    private DinoRunner[] runners;
    private final List<DinoObstacle> obstacles = new ArrayList<>();

    private double spawnTimer = 0;
    private final double baseSpeed = 500;
    private double speedMultiplier = 1.0;

    public DinoGame(List<Player> players, Pane pane) {
        super(pane);
        if (players.size() > 2) {
            this.players = new ArrayList<>(players.subList(0, 2));
        } else {
            this.players = new ArrayList<>(players);
        }
        this.participants = new ArrayList<>(this.players);
    }

    @Override
    public String getName() { return "Dino Run"; }

    @Override
    public String getDescription() { return "Überlebe länger als dein Gegner! Springen (W/UP) und Ducken (S/DOWN)."; }

    @Override
    protected void onStart() {
        drawEnvironment();
        runners = new DinoRunner[2];
        runners[0] = new DinoRunner(players.get(0), 300, KeyCode.W, KeyCode.S, getDifficulty(players.get(0)), pane);
        runners[1] = new DinoRunner(players.get(1), 600, KeyCode.UP, KeyCode.DOWN, getDifficulty(players.get(1)), pane);
    }

    private DinoRunner.Difficulty getDifficulty(Player bot) {
        if (bot.isHuman()) return DinoRunner.Difficulty.HARD;

        Player human = players.stream().filter(Player::isHuman).findFirst().orElse(null);
        if (human == null) return DinoRunner.Difficulty.MEDIUM;

        int diff = human.getCoins() - bot.getCoins();
        if (diff > 3) return DinoRunner.Difficulty.HARD;
        if (diff < -3) return DinoRunner.Difficulty.EASY;
        return DinoRunner.Difficulty.MEDIUM;
    }

    private void drawEnvironment() {
        Rectangle lane1Bg = new Rectangle(0, 100, 1280, 220);
        lane1Bg.setFill(Color.WHITE);

        Rectangle lane2Bg = new Rectangle(0, 400, 1280, 220);
        lane2Bg.setFill(Color.WHITE);

        Line separator = new Line(0, 360, 1280, 360);
        separator.setStroke(Color.WHITE);
        separator.setStrokeWidth(4);
        separator.getStrokeDashArray().addAll(20d, 20d);

        Line ground1 = new Line(0, 300, 1280, 300);
        ground1.setStroke(Color.BLACK);
        ground1.setStrokeWidth(2);

        Line ground2 = new Line(0, 600, 1280, 600);
        ground2.setStroke(Color.BLACK);
        ground2.setStrokeWidth(2);

        pane.getChildren().addAll(lane1Bg, lane2Bg, separator, ground1, ground2);
    }

    @Override
    public void update(double dt, InputHandler input) {
        if (finished) return;

        speedMultiplier += dt * 0.05;
        spawnTimer -= dt;

        if (spawnTimer <= 0) {
            spawnObstacle();
            spawnTimer = (1.0 + Math.random() * 1.5) / speedMultiplier;
        }

        double currentSpeed = baseSpeed * speedMultiplier;

        for (int i = obstacles.size() - 1; i >= 0; i--) {
            DinoObstacle obs = obstacles.get(i);
            obs.update(dt, currentSpeed);
            if (obs.isOffScreen()) {
                obs.removeFromPane();
                obstacles.remove(i);
            }
        }

        int aliveCount = 0;
        DinoRunner lastAlive = null;

        for (DinoRunner runner : runners) {
            if (!runner.isDead()) {
                runner.update(dt, input, obstacles, currentSpeed);
                runner.checkCollision(obstacles);
                if (!runner.isDead()) {
                    aliveCount++;
                    lastAlive = runner;
                }
            }
        }

        if (aliveCount <= 1) {
            finished = true;
            winner = (aliveCount == 1) ? lastAlive.getPlayer() : null;
        }
    }

    private void spawnObstacle() {
        boolean isFlying = Math.random() > 0.6;
        double height = isFlying ? 40 : 50;
        double width = 40;
        double yOffset = isFlying ? 50 : 0;

        obstacles.add(new DinoObstacle(width, height, yOffset, isFlying, pane));
    }
}