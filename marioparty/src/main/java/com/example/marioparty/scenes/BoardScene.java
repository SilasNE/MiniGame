package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.engine.GameEngine;
import com.example.marioparty.engine.GameScene;
import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Dice;
import com.example.marioparty.model.Field;
import com.example.marioparty.model.GameState;
import com.example.marioparty.model.Player;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BoardScene extends GameScene {

    private enum Phase { WAITING_TO_ROLL, ROLLING, MOVING, FIELD_ACTION, NEXT_TURN }

    private Phase phase = Phase.WAITING_TO_ROLL;
    private int diceValue = 1;
    private int stepsLeft = 0;
    private double phaseTimer = 0;

    private List<Circle> playerNodes;
    private Rectangle[] hudBoxes;
    private Text[] hudStats;
    private Text roundText;
    private Rectangle diceBox;
    private Text diceLabel;
    private Text messageText;

    public BoardScene(GameEngine engine) {
        super(engine);
    }

    @Override
    public void onEnter() {
        phase = Phase.WAITING_TO_ROLL;
        phaseTimer = 0;

        Pane pane = engine.getPane();
        GameState state = engine.getState();
        List<Player> players = state.getPlayers();

        pane.getChildren().add(new Rectangle(Main.WIDTH, Main.HEIGHT, Color.web("#2d5016")));

        // Brett-Felder
        for (int i = 0; i < state.getBoard().size(); i++) {
            Field f = state.getBoard().getField(i);
            Circle c = new Circle(f.getX(), f.getY(), 28, colorFor(f.getType()));
            c.setStroke(Color.BLACK);
            c.setStrokeWidth(2);
            pane.getChildren().add(c);
        }

        // Spieler-Kreise
        playerNodes = new ArrayList<>();
        for (Player p : players) {
            Circle c = new Circle(11, p.getColor());
            c.setStroke(Color.BLACK);
            c.setStrokeWidth(2);
            pane.getChildren().add(c);
            playerNodes.add(c);
        }

        // HUD-Boxen
        hudBoxes = new Rectangle[players.size()];
        hudStats = new Text[players.size()];
        for (int i = 0; i < players.size(); i++) {
            double x = 20 + i * 246;
            Rectangle box = new Rectangle(x, 20, 230, 64);
            box.setFill(players.get(i).getColor());
            box.setArcWidth(12);
            box.setArcHeight(12);
            hudBoxes[i] = box;

            Text name = new Text(x + 12, 42, players.get(i).getName());
            name.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            name.setFill(Color.BLACK);

            Text stats = new Text(x + 12, 68, "");
            stats.setFont(Font.font("Arial", 16));
            stats.setFill(Color.BLACK);
            hudStats[i] = stats;

            pane.getChildren().addAll(box, name, stats);
        }

        // Rundenanzeige
        roundText = new Text(Main.WIDTH - 200, 115, "");
        roundText.setFont(Font.font("Arial", 20));
        roundText.setFill(Color.WHITE);
        pane.getChildren().add(roundText);

        // Würfel
        diceBox = new Rectangle(Main.WIDTH / 2.0 - 45, Main.HEIGHT - 130, 90, 90);
        diceBox.setFill(Color.WHITE);
        diceBox.setArcWidth(12);
        diceBox.setArcHeight(12);
        diceBox.setStroke(Color.BLACK);
        diceBox.setStrokeWidth(3);
        diceBox.setVisible(false);

        diceLabel = new Text("");
        diceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        diceLabel.setFill(Color.BLACK);
        diceLabel.setVisible(false);

        pane.getChildren().addAll(diceBox, diceLabel);

        // Nachrichtenleiste
        pane.getChildren().add(new Rectangle(0, Main.HEIGHT - 50, Main.WIDTH, 50) {{
            setFill(Color.rgb(0, 0, 0, 0.6));
        }});
        messageText = new Text(30, Main.HEIGHT - 18, "");
        messageText.setFont(Font.font("Arial", 22));
        messageText.setFill(Color.WHITE);
        pane.getChildren().add(messageText);

        refreshNodes(state);
    }

    @Override
    public void update(double dt, InputHandler input) {
        GameState state = engine.getState();
        Player current = state.getCurrentPlayer();

        switch (phase) {
            case WAITING_TO_ROLL -> {
                messageText.setText(current.getName() + " ist dran!  [LEERTASTE = würfeln]");
                if (input.wasJustPressed(KeyCode.SPACE)) {
                    phase = Phase.ROLLING;
                    phaseTimer = 0;
                }
            }
            case ROLLING -> {
                phaseTimer += dt;
                diceValue = Dice.roll();
                if (phaseTimer > 1.0) {
                    diceValue = Dice.roll();
                    stepsLeft = diceValue;
                    phase = Phase.MOVING;
                    phaseTimer = 0;
                    messageText.setText(current.getName() + " würfelt eine " + diceValue + "!");
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
                messageText.setText(describeFieldEffect(current, f));
                phase = Phase.NEXT_TURN;
                phaseTimer = 0;
            }
            case NEXT_TURN -> {
                phaseTimer += dt;
                if (phaseTimer > 1.5) {
                    state.nextPlayer();
                    if (state.isGameOver()) {
                        engine.setScene(new MenuScene(engine));
                        return;
                    }
                    if (state.getCurrentPlayerIndex() == 0) {
                        engine.setScene(new MiniGameScene(engine));
                        return;
                    }
                    phase = Phase.WAITING_TO_ROLL;
                }
            }
        }

        refreshNodes(state);
    }

    private void refreshNodes(GameState state) {
        List<Player> players = state.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            Field f = state.getBoard().getField(p.getBoardPosition());
            double offsetX = (i % 2) * 18 - 9;
            double offsetY = (i / 2) * 18 - 9;
            playerNodes.get(i).setCenterX(f.getX() + offsetX);
            playerNodes.get(i).setCenterY(f.getY() + offsetY);
        }

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            boolean active = (i == state.getCurrentPlayerIndex());
            hudBoxes[i].setStroke(active ? Color.YELLOW : Color.TRANSPARENT);
            hudBoxes[i].setStrokeWidth(active ? 4 : 0);
            hudStats[i].setText("Sterne: " + p.getStars() + "   Münzen: " + p.getCoins());
        }

        roundText.setText("Runde " + state.getRound() + " / " + state.getTotalRounds());

        boolean showDice = phase == Phase.ROLLING || phase == Phase.MOVING;
        diceBox.setVisible(showDice);
        diceLabel.setVisible(showDice);
        if (showDice) {
            diceLabel.setText(String.valueOf(diceValue));
            diceLabel.setX(Main.WIDTH / 2.0 - 45 + 22);
            diceLabel.setY(Main.HEIGHT - 130 + 68);
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