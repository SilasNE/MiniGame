package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.engine.GameEngine;
import com.example.marioparty.engine.GameScene;
import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Dice;
import com.example.marioparty.model.Field;
import com.example.marioparty.model.GameState;
import com.example.marioparty.model.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Hauptspielszene. Zustandsautomat mit Phasen:
 *   WAITING_TO_ROLL → ROLLING → MOVING → FIELD_ACTION → NEXT_TURN → ...
 *
 * Nach jeder vollen Runde (alle 4 Spieler dran) wechselt sie zur MiniGameScene.
 */
public class BoardScene extends GameScene {

    private enum Phase { WAITING_TO_ROLL, ROLLING, MOVING, FIELD_ACTION, NEXT_TURN }

    private Phase phase = Phase.WAITING_TO_ROLL;
    private int diceValue = 1;
    private int stepsLeft = 0;
    private double phaseTimer = 0;
    private String message = "";

    public BoardScene(GameEngine engine) {
        super(engine);
    }

    @Override
    public void onEnter() {
        phase = Phase.WAITING_TO_ROLL;
        phaseTimer = 0;
    }

    @Override
    public void update(double dt, InputHandler input) {
        GameState state = engine.getState();
        Player current = state.getCurrentPlayer();

        switch (phase) {
            case WAITING_TO_ROLL -> {
                message = current.getName() + " ist dran!  [LEERTASTE = würfeln]";
                if (input.wasJustPressed(KeyCode.SPACE)) {
                    phase = Phase.ROLLING;
                    phaseTimer = 0;
                }
            }
            case ROLLING -> {
                phaseTimer += dt;
                // Visueller Würfel-"Flicker"
                diceValue = Dice.roll();
                if (phaseTimer > 1.0) {
                    diceValue = Dice.roll();         // finaler Wurf
                    stepsLeft = diceValue;
                    phase = Phase.MOVING;
                    phaseTimer = 0;
                    message = current.getName() + " würfelt eine " + diceValue + "!";
                }
            }
            case MOVING -> {
                phaseTimer += dt;
                if (phaseTimer > 0.3 && stepsLeft > 0) {
                    current.move(1, state.getBoard().size());
                    stepsLeft--;
                    phaseTimer = 0;
                }
                if (stepsLeft == 0) {
                    phase = Phase.FIELD_ACTION;
                    phaseTimer = 0;
                }
            }
            case FIELD_ACTION -> {
                Field f = state.getBoard().getField(current.getBoardPosition());
                f.onLand(current);
                message = describeFieldEffect(current, f);
                phase = Phase.NEXT_TURN;
                phaseTimer = 0;
            }
            case NEXT_TURN -> {
                phaseTimer += dt;
                if (phaseTimer > 1.5) {
                    state.nextPlayer();

                    if (state.isGameOver()) {
                        // Vereinfacht: zurück ins Menü. Erweiterungspunkt: ResultScene.
                        engine.setScene(new MenuScene(engine));
                        return;
                    }
                    // Nach jeder vollen Runde: Minispiel
                    if (state.getCurrentPlayerIndex() == 0) {
                        engine.setScene(new MiniGameScene(engine));
                        return;
                    }
                    phase = Phase.WAITING_TO_ROLL;
                }
            }
        }
    }

    private String describeFieldEffect(Player p, Field f) {
        return switch (f.getType()) {
            case BLUE  -> p.getName() + " landet auf BLAU: +3 Münzen";
            case RED   -> p.getName() + " landet auf ROT: -3 Münzen";
            case STAR  -> p.getName() + " landet auf einem Sternfeld!";
            case EVENT -> p.getName() + " landet auf einem Event-Feld!";
            case START -> p.getName() + " erreicht das Startfeld";
        };
    }

    @Override
    public void render(GraphicsContext gc) {
        // Hintergrund
        gc.setFill(Color.web("#2d5016"));
        gc.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);

        GameState state = engine.getState();

        // Felder
        for (int i = 0; i < state.getBoard().size(); i++) {
            Field f = state.getBoard().getField(i);
            Color c = colorFor(f.getType());
            gc.setFill(c);
            gc.fillOval(f.getX() - 28, f.getY() - 28, 56, 56);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(f.getX() - 28, f.getY() - 28, 56, 56);
        }

        // Spieler (versetzt, damit sie sich auf gleichem Feld nicht überdecken)
        for (int p = 0; p < state.getPlayers().size(); p++) {
            Player player = state.getPlayers().get(p);
            Field f = state.getBoard().getField(player.getBoardPosition());
            double offsetX = (p % 2) * 18 - 9;
            double offsetY = (p / 2) * 18 - 9;
            gc.setFill(player.getColor());
            gc.fillOval(f.getX() + offsetX - 11, f.getY() + offsetY - 11, 22, 22);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(f.getX() + offsetX - 11, f.getY() + offsetY - 11, 22, 22);
        }

        // HUD: Spieler-Stats oben
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        for (int p = 0; p < state.getPlayers().size(); p++) {
            Player player = state.getPlayers().get(p);
            boolean isActive = (p == state.getCurrentPlayerIndex());
            double x = 20 + p * 246;

            gc.setFill(player.getColor());
            gc.fillRoundRect(x, 20, 230, 64, 12, 12);
            if (isActive) {
                gc.setStroke(Color.YELLOW);
                gc.setLineWidth(4);
                gc.strokeRoundRect(x, 20, 230, 64, 12, 12);
            }
            gc.setFill(Color.BLACK);
            gc.fillText(player.getName(), x + 12, 42);
            gc.fillText("Sterne: " + player.getStars()
                    + "   Münzen: " + player.getCoins(), x + 12, 68);
        }

        // Rundenanzeige
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Runde " + state.getRound() + " / " + state.getTotalRounds(),
                Main.WIDTH - 200, 115);

        // Würfel
        if (phase == Phase.ROLLING || phase == Phase.MOVING) {
            double dx = Main.WIDTH / 2.0 - 45;
            double dy = Main.HEIGHT - 130;
            gc.setFill(Color.WHITE);
            gc.fillRoundRect(dx, dy, 90, 90, 12, 12);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(3);
            gc.strokeRoundRect(dx, dy, 90, 90, 12, 12);
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 56));
            gc.fillText(String.valueOf(diceValue), dx + 28, dy + 68);
        }

        // Nachricht unten
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRect(0, Main.HEIGHT - 50, Main.WIDTH, 50);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 22));
        gc.fillText(message, 30, Main.HEIGHT - 18);
    }

    private Color colorFor(Field.Type t) {
        return switch (t) {
            case BLUE  -> Color.DODGERBLUE;
            case RED   -> Color.CRIMSON;
            case STAR  -> Color.GOLD;
            case EVENT -> Color.MEDIUMPURPLE;
            case START -> Color.WHITE;
        };
    }
}
