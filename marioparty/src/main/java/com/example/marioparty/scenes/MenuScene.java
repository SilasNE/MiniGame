package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.engine.GameEngine;
import com.example.marioparty.engine.GameScene;
import com.example.marioparty.engine.InputHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MenuScene extends GameScene {

    private double pulse = 0;
    private Text hint;
    private int selectedStarsGoal = 5;
    private Button star3;
    private Button star5;
    private Button star7;

    public MenuScene(GameEngine engine) {
        super(engine);
    }

    @Override
    public void onEnter() {
        pulse = 0;
        selectedStarsGoal = 5;
        Pane pane = engine.getPane();

        Rectangle bg = new Rectangle(Main.WIDTH, Main.HEIGHT, Color.web("#1a1a2e"));

        Text title = new Text(Main.WIDTH / 2.0 - 360, Main.HEIGHT / 2.0 - 200, "MINI MARIO PARTY");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        title.setFill(Color.web("#ffd60a"));

        Text subStars = new Text(Main.WIDTH / 2.0 - 240, Main.HEIGHT / 2.0 - 100, "Sterne zum Sieg:");
        subStars.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        subStars.setFill(Color.WHITE);

        star3 = new Button("3 Sterne");
        star5 = new Button("5 Sterne");
        star7 = new Button("7 Sterne");
        styleBtn(star3);
        styleBtn(star5);
        styleBtn(star7);
        star3.setLayoutX(Main.WIDTH / 2.0 - 230);
        star5.setLayoutX(Main.WIDTH / 2.0 - 40);
        star7.setLayoutX(Main.WIDTH / 2.0 + 150);
        star3.setLayoutY(Main.HEIGHT / 2.0 - 60);
        star5.setLayoutY(Main.HEIGHT / 2.0 - 60);
        star7.setLayoutY(Main.HEIGHT / 2.0 - 60);
        star3.setOnAction(e -> selectStars(3));
        star5.setOnAction(e -> selectStars(5));
        star7.setOnAction(e -> selectStars(7));
        refreshStarButtonStyles();

        Text subHumans = new Text(Main.WIDTH / 2.0 - 280, Main.HEIGHT / 2.0 + 20,
                "Menschliche Spieler? (Rest = CPU)");
        subHumans.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        subHumans.setFill(Color.WHITE);

        Button one = new Button("1 Mensch");
        Button two = new Button("2 Menschen");
        styleBtn(one);
        styleBtn(two);
        one.setLayoutX(Main.WIDTH / 2.0 - 220);
        one.setLayoutY(Main.HEIGHT / 2.0 + 55);
        two.setLayoutX(Main.WIDTH / 2.0 + 40);
        two.setLayoutY(Main.HEIGHT / 2.0 + 55);
        one.setOnAction(e -> startGame(1));
        two.setOnAction(e -> startGame(2));

        hint = new Text(Main.WIDTH / 2.0 - 280, Main.HEIGHT / 2.0 + 130,
                "4 Spieler  •  Erster mit gewählter Sternzahl gewinnt  •  CPU: Items, BFS, Button-Mash");
        hint.setFont(Font.font("Arial", 16));
        hint.setFill(Color.LIGHTGRAY);

        pane.getChildren().addAll(bg, title, subStars, star3, star5, star7, subHumans, one, two, hint);
    }

    private void selectStars(int n) {
        selectedStarsGoal = n;
        refreshStarButtonStyles();
    }

    private void refreshStarButtonStyles() {
        styleStarBtn(star3, selectedStarsGoal == 3);
        styleStarBtn(star5, selectedStarsGoal == 5);
        styleStarBtn(star7, selectedStarsGoal == 7);
    }

    private static void styleStarBtn(Button b, boolean selected) {
        styleBtn(b);
        if (selected) {
            b.setStyle("-fx-base: #ffd60a;");
        } else {
            b.setStyle("");
        }
    }

    private void startGame(int humanCount) {
        engine.getState().restartMatch(humanCount, selectedStarsGoal);
        engine.setScene(new BoardScene(engine));
    }

    private static void styleBtn(Button b) {
        b.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        b.setPrefWidth(180);
        b.setPrefHeight(44);
    }

    @Override
    public void update(double dt, InputHandler input) {
        pulse += dt;
        hint.setOpacity(0.65 + 0.35 * Math.sin(pulse * 2));
    }
}
