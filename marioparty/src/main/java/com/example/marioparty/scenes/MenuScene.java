package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.engine.GameEngine;
import com.example.marioparty.engine.GameScene;
import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.minigames.BattleshipGame;
import com.example.marioparty.minigames.ButtonMashGame;
import com.example.marioparty.minigames.PongGame;
import com.example.marioparty.minigames.TicTacToeGame;
import com.example.marioparty.model.GameState;
import com.example.marioparty.model.Player;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

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
        final double contentX = Main.WIDTH / 2.0 - 310;
        final double contentW = 620;
        final double gap = 20;
        final double starW = (contentW - 2 * gap) / 3.0;
        final double columnW = (contentW - gap) / 2.0;
        final double rightColumnX = contentX + columnW + gap;

        Rectangle bg = new Rectangle(Main.WIDTH, Main.HEIGHT, Color.web("#1a1a2e"));

        Text title = new Text(Main.WIDTH / 2.0 - 360, Main.HEIGHT / 2.0 - 200, "MINI MARIO PARTY");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        title.setFill(Color.web("#ffd60a"));

        Text subStars = new Text(contentX, Main.HEIGHT / 2.0 - 100, "Sterne zum Sieg:");
        subStars.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        subStars.setFill(Color.WHITE);

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
        subHumans.setFill(Color.WHITE);

        Button one = new Button("1 Person + 3 Computer");
        Button two = new Button("2 Personen + 2 Computer");
        styleChoiceBtn(one, columnW);
        styleChoiceBtn(two, columnW);
        one.setLayoutX(contentX);
        one.setLayoutY(Main.HEIGHT / 2.0 + 45);
        two.setLayoutX(rightColumnX);
        two.setLayoutY(Main.HEIGHT / 2.0 + 45);
        one.setOnAction(e -> startGame(1));
        two.setOnAction(e -> startGame(2));

        hint = new Text(contentX, Main.HEIGHT / 2.0 + 115,
                "Hauptspiel: Es spielen immer 4 Figuren, freie Plätze übernimmt der Computer.");
        hint.setFont(Font.font("Arial", 16));
        hint.setFill(Color.LIGHTGRAY);

        Text testGamesTitle = new Text(contentX, Main.HEIGHT / 2.0 + 145,
                "Minispiele testen:");
        testGamesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        testGamesTitle.setFill(Color.WHITE);

        Button testTicTacToeBot = new Button("TicTacToe: Allein vs Bot");
        styleTestBtn(testTicTacToeBot);
        testTicTacToeBot.setLayoutX(contentX);
        testTicTacToeBot.setLayoutY(Main.HEIGHT / 2.0 + 180);
        testTicTacToeBot.setOnAction(e -> startTicTacToeBotTest());

        Button testTicTacToePlayers = new Button("TicTacToe: 2 Spieler");
        styleTestBtn(testTicTacToePlayers);
        testTicTacToePlayers.setLayoutX(rightColumnX);
        testTicTacToePlayers.setLayoutY(Main.HEIGHT / 2.0 + 180);
        testTicTacToePlayers.setOnAction(e -> startTicTacToePlayersTest());

        Button testButtonMashBots = new Button("Button Mash: Allein vs Bots");
        styleTestBtn(testButtonMashBots);
        testButtonMashBots.setLayoutX(contentX);
        testButtonMashBots.setLayoutY(Main.HEIGHT / 2.0 + 230);
        testButtonMashBots.setOnAction(e -> startButtonMashBotTest());

        Button testButtonMashPlayers = new Button("Button Mash: 2 Spieler");
        styleTestBtn(testButtonMashPlayers);
        testButtonMashPlayers.setLayoutX(rightColumnX);
        testButtonMashPlayers.setLayoutY(Main.HEIGHT / 2.0 + 230);
        testButtonMashPlayers.setOnAction(e -> startButtonMashPlayersTest());

        Button testPongBot = new Button("Pong: Allein vs Bot");
        styleTestBtn(testPongBot);
        testPongBot.setLayoutX(contentX);
        testPongBot.setLayoutY(Main.HEIGHT / 2.0 + 280);
        testPongBot.setOnAction(e -> startPongBotTest());

        Button testPongPlayers = new Button("Pong: 2 Spieler");
        styleTestBtn(testPongPlayers);
        testPongPlayers.setLayoutX(rightColumnX);
        testPongPlayers.setLayoutY(Main.HEIGHT / 2.0 + 280);
        testPongPlayers.setOnAction(e -> startPongPlayersTest());

        Button testBattleship = new Button("Schiffe versenken: vs Bot");
        styleTestBtn(testBattleship);
        testBattleship.setLayoutX(contentX);
        testBattleship.setLayoutY(Main.HEIGHT / 2.0 + 330);
        testBattleship.setOnAction(e -> startBattleshipTest());

        Button testBattleshipPlayers = new Button("Schiffe versenken: 2 Spieler");
        styleTestBtn(testBattleshipPlayers);
        testBattleshipPlayers.setLayoutX(rightColumnX);
        testBattleshipPlayers.setLayoutY(Main.HEIGHT / 2.0 + 330);
        testBattleshipPlayers.setOnAction(e -> startBattleshipPlayersTest());

        pane.getChildren().addAll(
                bg, title,
                subStars, star3, star5, star7,
                subHumans, one, two,
                testGamesTitle,
                testTicTacToePlayers, testTicTacToeBot,
                testButtonMashPlayers, testButtonMashBots,
                testPongPlayers, testPongBot,
                testBattleship, testBattleshipPlayers,
                hint
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
            b.setStyle("-fx-base: #ffd60a;");
        } else {
            b.setStyle("");
        }
    }

    private void startGame(int humanCount) {
        engine.getState().restartMatch(humanCount, selectedStarsGoal);
        engine.setScene(new BoardScene(engine));
    }

    private void startTicTacToeBotTest() {
        GameState state = engine.getState();
        state.restartMatch(1, selectedStarsGoal);
        List<Player> players = state.getPlayers();
        TicTacToeGame game = new TicTacToeGame(List.of(players.getFirst()), engine.getPane(), 0.35);
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startTicTacToePlayersTest() {
        GameState state = engine.getState();
        state.restartMatch(2, selectedStarsGoal);
        List<Player> players = state.getPlayers();
        TicTacToeGame game = new TicTacToeGame(List.of(players.get(0), players.get(1)), engine.getPane(), 0.0);
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startButtonMashBotTest() {
        GameState state = engine.getState();
        state.restartMatch(1, selectedStarsGoal);
        ButtonMashGame game = new ButtonMashGame(state.getPlayers(), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startButtonMashPlayersTest() {
        GameState state = engine.getState();
        state.restartMatch(2, selectedStarsGoal);
        List<Player> players = state.getPlayers();
        ButtonMashGame game = new ButtonMashGame(List.of(players.get(0), players.get(1)), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startPongBotTest() {
        GameState state = engine.getState();
        state.restartMatch(1, selectedStarsGoal);
        List<Player> players = state.getPlayers();
        PongGame game = new PongGame(List.of(players.getFirst()), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startPongPlayersTest() {
        GameState state = engine.getState();
        state.restartMatch(2, selectedStarsGoal);
        List<Player> players = state.getPlayers();
        PongGame game = new PongGame(List.of(players.get(0), players.get(1)), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startBattleshipTest() {
        GameState state = engine.getState();
        state.restartMatch(1, selectedStarsGoal);
        List<Player> players = state.getPlayers();
        BattleshipGame game = new BattleshipGame(List.of(players.getFirst()), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startBattleshipPlayersTest() {
        GameState state = engine.getState();
        state.restartMatch(2, selectedStarsGoal);
        List<Player> players = state.getPlayers();
        BattleshipGame game = new BattleshipGame(List.of(players.get(0), players.get(1)), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private static void styleChoiceBtn(Button b, double width) {
        b.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        b.setPrefWidth(width);
        b.setPrefHeight(44);
    }

    private static void styleTestBtn(Button b) {
        b.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        b.setPrefWidth(300);
        b.setPrefHeight(42);
    }

    @Override
    public void update(double dt, InputHandler input) {
        pulse += dt;
        hint.setOpacity(0.65 + 0.35 * Math.sin(pulse * 2));
    }
}
