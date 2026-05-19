package com.example.marioparty.minigames;

import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Player;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

public class DinoRunner {

    public enum Difficulty { EASY, MEDIUM, HARD }

    private final Player player;
    private final double groundY;
    private final KeyCode jumpKey;
    private final KeyCode duckKey;
    private final Difficulty difficulty;
    private final Pane pane;

    private double y;
    private double yVelocity = 0;
    private boolean isDucking = false;
    private boolean isDead = false;

    private static final double NORMAL_HEIGHT = 80;
    private static final double DUCK_HEIGHT = 40;
    private static final double WIDTH = 50;
    private static final double X_POS = 150;
    private static final double GRAVITY = 2500;
    private static final double JUMP_FORCE = -650;

    private ImageView imageView;
    private DinoObstacle lastTargetObstacle = null;
    private double botDuckTimer = 0;

    public DinoRunner(Player player, double groundY, KeyCode jumpKey, KeyCode duckKey, Difficulty difficulty, Pane pane) {
        this.player = player;
        this.groundY = groundY;
        this.jumpKey = jumpKey;
        this.duckKey = duckKey;
        this.difficulty = difficulty;
        this.pane = pane;
        this.y = groundY;
        createUI();
    }

    private void createUI() {
        String imagePath = "/images/" + player.getName() + ".png";

        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            imageView = new ImageView(img);
        } catch (Exception e) {
            imageView = new ImageView();
        }

        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(NORMAL_HEIGHT);
        imageView.setX(X_POS);
        imageView.setY(groundY - NORMAL_HEIGHT);



        pane.getChildren().add(imageView);
    }

    public void update(double dt, InputHandler input, List<DinoObstacle> obstacles, double currentSpeed) {
        if (player.isHuman()) {
            handleInput(input);
        } else {
            handleBot(dt, obstacles, currentSpeed);
        }

        yVelocity += GRAVITY * dt;
        y += yVelocity * dt;

        if (y >= groundY) {
            y = groundY;
            yVelocity = 0;
        }

        double currentHeight = isDucking ? DUCK_HEIGHT : NORMAL_HEIGHT;
        imageView.setFitHeight(currentHeight);
        imageView.setY(y - currentHeight);
    }

    private void handleInput(InputHandler input) {
        isDucking = (y == groundY && input.isDown(duckKey));

        if (y == groundY && !isDucking && input.wasJustPressed(jumpKey)) {
            yVelocity = JUMP_FORCE;
        }
    }

    private void handleBot(double dt, List<DinoObstacle> obstacles, double currentSpeed) {
        DinoObstacle closest = null;
        double minDist = Double.MAX_VALUE;

        for (DinoObstacle obs : obstacles) {
            double dist = obs.getX() - (X_POS + WIDTH);
            if (dist > 0 && dist < minDist) {
                minDist = dist;
                closest = obs;
            }
        }

        if (botDuckTimer > 0) {
            isDucking = true;
            botDuckTimer -= dt;
        } else {
            isDucking = false;
        }

        if (closest != null) {
            double timeToImpact = minDist / currentSpeed;
            double optimalReactionTime = (closest.getYOffset() > 0) ? 0.10 : 0.18;

            if (closest != lastTargetObstacle && timeToImpact <= optimalReactionTime) {
                lastTargetObstacle = closest;

                double errorChance = 0.02;
                if (difficulty == Difficulty.EASY) errorChance = 0.10;
                if (difficulty == Difficulty.MEDIUM) errorChance = 0.05;

                if (Math.random() >= errorChance) {
                    if (closest.getYOffset() > 0) {
                        double timeToPass = (closest.getWidth() + WIDTH) / currentSpeed;
                        botDuckTimer = timeToImpact + timeToPass + 0.05;
                    } else {
                        if (y == groundY) {
                            yVelocity = JUMP_FORCE;
                        }
                    }
                }
            }
        }
    }

    public void checkCollision(List<DinoObstacle> obstacles) {
        if (isDead) return;

        double myLeft = X_POS;
        double myRight = X_POS + WIDTH;
        double myBottom = y;
        double myTop = y - (isDucking ? DUCK_HEIGHT : NORMAL_HEIGHT);

        for (DinoObstacle obs : obstacles) {
            double obsLeft = obs.getX();
            double obsRight = obs.getX() + obs.getWidth();
            double obsBottom = groundY - obs.getYOffset();
            double obsTop = obsBottom - obs.getHeight();

            if (myRight > obsLeft && myLeft < obsRight && myBottom > obsTop && myTop < obsBottom) {
                isDead = true;
                imageView.setOpacity(0.3);
            }
        }
    }

    public boolean isDead() { return isDead; }

    public Player getPlayer() { return player; }
}