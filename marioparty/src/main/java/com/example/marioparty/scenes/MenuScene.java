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

public class MenuScene extends GameScene {

    private double pulse = 0;
    private Text hint;
    private int selectedStarsGoal = 5;
    private Button threeStarsButton;
    private Button fiveStarsButton;
    private Button sevenStarsButton;
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

        ImageView title = new ImageView(new Image(
                getClass().getResourceAsStream("/images/Mario_Party_DS.png")));
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

        threeStarsButton = new Button("3 Sterne");
        fiveStarsButton = new Button("5 Sterne");
        sevenStarsButton = new Button("7 Sterne");
        styleChoiceBtn(threeStarsButton, starW);
        styleChoiceBtn(fiveStarsButton, starW);
        styleChoiceBtn(sevenStarsButton, starW);
        threeStarsButton.setLayoutX(contentX);
        fiveStarsButton.setLayoutX(contentX + starW + gap);
        sevenStarsButton.setLayoutX(contentX + 2 * (starW + gap));
        threeStarsButton.setLayoutY(Main.HEIGHT / 2.0 - 60);
        fiveStarsButton.setLayoutY(Main.HEIGHT / 2.0 - 60);
        sevenStarsButton.setLayoutY(Main.HEIGHT / 2.0 - 60);
        threeStarsButton.setOnAction(e -> selectStars(3));
        fiveStarsButton.setOnAction(e -> selectStars(5));
        sevenStarsButton.setOnAction(e -> selectStars(7));
        refreshStarButtonStyles();

        Text subHumans = new Text(contentX, Main.HEIGHT / 2.0 + 10,
                "Hauptspiel starten:");
        subHumans.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        subHumans.setFill(Color.web("#ffd60a"));

        Button singleHumanButton = new Button("1 Person + 3 Computer");
        Button twoHumansButton = new Button("2 Personen + 2 Computer");
        Button testModeButton = new Button("Testmodus");
        styleChoiceBtn(singleHumanButton, columnW);
        styleChoiceBtn(twoHumansButton, columnW);
        styleChoiceBtn(testModeButton, contentW);
        singleHumanButton.setLayoutX(contentX);
        singleHumanButton.setLayoutY(Main.HEIGHT / 2.0 + 45);
        twoHumansButton.setLayoutX(rightColumnX);
        twoHumansButton.setLayoutY(Main.HEIGHT / 2.0 + 45);
        testModeButton.setLayoutX(contentX);
        testModeButton.setLayoutY(Main.HEIGHT / 2.0 + 105);
        singleHumanButton.setOnAction(e -> startGame(1));
        twoHumansButton.setOnAction(e -> startGame(2));
        testModeButton.setOnAction(e -> engine.setScene(new TestModeScene(engine, selectedStarsGoal)));

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
                subStars, threeStarsButton, fiveStarsButton, sevenStarsButton,
                subHumans, singleHumanButton, twoHumansButton, testModeButton,
                hint, info, infoPanel
        );
    }

    private void selectStars(int starCount) {
        selectedStarsGoal = starCount;
        refreshStarButtonStyles();
    }

    private void refreshStarButtonStyles() {
        styleStarBtn(threeStarsButton, selectedStarsGoal == 3);
        styleStarBtn(fiveStarsButton, selectedStarsGoal == 5);
        styleStarBtn(sevenStarsButton, selectedStarsGoal == 7);
    }

    private static void styleStarBtn(Button button, boolean selected) {
        if (selected) {
            button.setStyle(selectedButtonStyle("#ffd60a", "#ff7a00", "#5b2b00"));
        } else {
            button.setStyle(normalButtonStyle("#ffffff", "#28a8ff", "#083e8c"));
        }
    }

    private void startGame(int humanCount) {
        engine.getState().restartMatch(humanCount, selectedStarsGoal);
        engine.setScene(new BoardScene(engine));
    }

    private static void styleChoiceBtn(Button button, double width) {
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setPrefWidth(width);
        button.setPrefHeight(44);
        button.setTextFill(Color.web("#083e8c"));
        button.setStyle(normalButtonStyle("#ffffff", "#28a8ff", "#083e8c"));
    }

    private static void styleInfoBtn(Button button) {
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setPrefWidth(120);
        button.setPrefHeight(44);
        button.setTextFill(Color.web("#5b2b00"));
        button.setStyle(selectedButtonStyle("#ffd60a", "#ff7a00", "#5b2b00"));
    }

    private static Group buildInfoPanel() {
        Rectangle panel = new Rectangle(20, 110, 470, 500);
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
                - Memory: Paare aufdecken, mehr Treffer gewinnt.

                Testmodus
                - Separates Menü für Itemshop, Minispiele und Debug-Funktionen.
                """);
        body.setFont(Font.font("Arial", 15));
        body.setFill(Color.WHITE);
        body.setWrappingWidth(420);

        Group infoGroup = new Group(panel, title, body);
        infoGroup.setVisible(false);
        return infoGroup;
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
        hint.setOpacity(0.65);
    }
}
