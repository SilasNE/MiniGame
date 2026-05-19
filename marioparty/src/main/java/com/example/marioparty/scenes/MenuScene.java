package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.engine.GameEngine;
import com.example.marioparty.engine.GameScene;
import com.example.marioparty.engine.InputHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Objects;

public class MenuScene extends GameScene {

    private double pulse = 0;
    private Text hint;
    private int selectedStarsGoal = 5;
    private Button star3;
    private Button star5;
    private Button star7;
    private Group infoPanel;

    public MenuScene(GameEngine engine) {
        super(engine);
    }

    @Override
    public void onEnter() {
        pulse = 0;
        selectedStarsGoal = 5;
        Pane pane = engine.getPane();
        final double contentX = Main.WIDTH / 2.0 - 310;
        final double contentW = 620;
        final double gap = 20;
        final double starW = (contentW - 2 * gap) / 3.0;
        final double columnW = (contentW - gap) / 2.0;
        final double rightColumnX = contentX + columnW + gap;

        Rectangle bg = new Rectangle(Main.WIDTH, Main.HEIGHT, Color.web("#0a74cf"));
        Rectangle skyGlow = new Rectangle(Main.WIDTH, Main.HEIGHT, Color.rgb(255, 255, 255, 0.08));

        ImageView title = new ImageView(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/Mario_Party_DS.png"),
                "Title image missing: /images/Mario_Party_DS.png")));
        title.setFitWidth(760);
        title.setPreserveRatio(true);
        title.setSmooth(true);
        title.setLayoutX(Main.WIDTH / 2.0 - 380);
        title.setLayoutY(28);

        Rectangle menuPanel = new Rectangle(contentX - 28, Main.HEIGHT / 2.0 - 126, contentW + 56, 360);
        menuPanel.setFill(Color.rgb(0, 45, 105, 0.72));
        menuPanel.setArcWidth(28);
        menuPanel.setArcHeight(28);
        menuPanel.setStroke(Color.WHITE);
        menuPanel.setStrokeWidth(3);

        Text subStars = new Text(contentX, Main.HEIGHT / 2.0 - 100, "Sterne zum Sieg:");
        subStars.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        subStars.setFill(Color.web("#ffd60a"));

        star3 = new Button("3 Sterne");
        star5 = new Button("5 Sterne");
        star7 = new Button("7 Sterne");
        styleChoiceBtn(star3, starW);
        styleChoiceBtn(star5, starW);
        styleChoiceBtn(star7, starW);
        star3.setLayoutX(contentX);
        star5.setLayoutX(contentX + starW + gap);
        star7.setLayoutX(contentX + 2 * (starW + gap));
        star3.setLayoutY(Main.HEIGHT / 2.0 - 60);
        star5.setLayoutY(Main.HEIGHT / 2.0 - 60);
        star7.setLayoutY(Main.HEIGHT / 2.0 - 60);
        star3.setOnAction(e -> selectStars(3));
        star5.setOnAction(e -> selectStars(5));
        star7.setOnAction(e -> selectStars(7));
        refreshStarButtonStyles();

        Text subHumans = new Text(contentX, Main.HEIGHT / 2.0 + 10,
                "Hauptspiel starten:");
        subHumans.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        subHumans.setFill(Color.web("#ffd60a"));

        Button one = new Button("1 Person + 3 Computer");
        Button two = new Button("2 Personen + 2 Computer");
        Button testMode = new Button("Testmodus");
        styleChoiceBtn(one, columnW);
        styleChoiceBtn(two, columnW);
        styleChoiceBtn(testMode, contentW);
        one.setLayoutX(contentX);
        one.setLayoutY(Main.HEIGHT / 2.0 + 45);
        two.setLayoutX(rightColumnX);
        two.setLayoutY(Main.HEIGHT / 2.0 + 45);
        testMode.setLayoutX(contentX);
        testMode.setLayoutY(Main.HEIGHT / 2.0 + 105);
        one.setOnAction(e -> startGame(1));
        two.setOnAction(e -> startGame(2));
        testMode.setOnAction(e -> engine.setScene(new TestModeScene(engine, selectedStarsGoal)));

        hint = new Text(contentX, Main.HEIGHT / 2.0 + 175,
                "Testmodus enthält Itemshop, Minispiele und weitere Testfunktionen.");
        hint.setFont(Font.font("Arial", 16));
        hint.setFill(Color.WHITE);

        Button info = new Button("Info");
        styleInfoBtn(info);
        info.setLayoutX(20);
        info.setLayoutY(Main.HEIGHT - 64);
        info.setOnAction(e -> infoPanel.setVisible(!infoPanel.isVisible()));

        infoPanel = buildInfoPanel();

        pane.getChildren().addAll(
                bg, skyGlow, title, menuPanel,
                subStars, star3, star5, star7,
                subHumans, one, two, testMode,
                hint, info, infoPanel
        );
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
        if (selected) {
            b.setStyle(selectedButtonStyle("#ffd60a", "#ff7a00", "#5b2b00"));
        } else {
            b.setStyle(normalButtonStyle("#ffffff", "#28a8ff", "#083e8c"));
        }
    }

    private void startGame(int humanCount) {
        engine.getState().restartMatch(humanCount, selectedStarsGoal);
        engine.setScene(new BoardScene(engine));
    }

    private static void styleChoiceBtn(Button b, double width) {
        b.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        b.setPrefWidth(width);
        b.setPrefHeight(44);
        b.setTextFill(Color.web("#083e8c"));
        b.setStyle(normalButtonStyle("#ffffff", "#28a8ff", "#083e8c"));
    }

    private static void styleInfoBtn(Button b) {
        b.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        b.setPrefWidth(120);
        b.setPrefHeight(44);
        b.setTextFill(Color.web("#5b2b00"));
        b.setStyle(selectedButtonStyle("#ffd60a", "#ff7a00", "#5b2b00"));
    }

    private static Group buildInfoPanel() {
        Rectangle panel = new Rectangle(20, 110, 470, 430);
        panel.setFill(Color.rgb(0, 45, 105, 0.9));
        panel.setArcWidth(18);
        panel.setArcHeight(18);
        panel.setStroke(Color.web("#ffd60a"));
        panel.setStrokeWidth(2);

        Text title = new Text(42, 145, "Info");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setFill(Color.web("#ffd60a"));

        Text body = new Text(42, 182,
                """
                Hauptspiel
                - 4 Figuren spielen auf dem Brett.
                - Ziel: als Erste/r die gewählte Sternzahl erreichen.
                - CPUs laufen an Abzweigungen per BS (Breitensuche) Richtung Stern.

                Items
                - Warp-Röhre: teleportiert direkt zum Stern.
                - Dreifach-Pilz: +3 auf den nächsten Wurf.
                - Münzblock: +12 Münzen sofort.

                Minispiele
                - Button Mash: Taste schnell drücken.
                - TicTacToe: Drei in einer Reihe.
                - Pong: Ball am Gegner vorbeibringen.
                - Schiffe versenken: gegnerische Flotte finden.
                - Dino Run: Hindernissen ausweichen (Springen/Ducken).

                Testmodus
                - Separates Menü für Itemshop, Minispiele und Debug-Funktionen.
                """);
        body.setFont(Font.font("Arial", 15));
        body.setFill(Color.WHITE);
        body.setWrappingWidth(420);

        Group g = new Group(panel, title, body);
        g.setVisible(false);
        return g;
    }

    private static String normalButtonStyle(String fill, String border, String textStroke) {
        return "-fx-background-color: " + fill + ";"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-width: 3;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 12;"
                + "-fx-text-fill: " + textStroke + ";";
    }

    private static String selectedButtonStyle(String fill, String border, String textStroke) {
        return "-fx-background-color: " + fill + ";"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-width: 4;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 12;"
                + "-fx-text-fill: " + textStroke + ";";
    }

    @Override
    public void update(double dt, InputHandler input) {
        pulse += dt;
        hint.setOpacity(0.65 + 0.35 * Math.sin(pulse * 2));
    }
}
