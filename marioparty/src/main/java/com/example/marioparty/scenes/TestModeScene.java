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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

public class TestModeScene extends GameScene {

    private final int starsGoal;

    public TestModeScene(GameEngine engine, int starsGoal) {
        super(engine);
        this.starsGoal = starsGoal;
    }

    @Override
    public void onEnter() {
        Pane pane = engine.getPane();
        double contentX = Main.WIDTH / 2.0 - 310;
        double contentW = 620;
        double gap = 20;
        double columnW = (contentW - gap) / 2.0;
        double rightColumnX = contentX + columnW + gap;

        Rectangle bg = new Rectangle(Main.WIDTH, Main.HEIGHT, Color.web("#101820"));

        Text title = new Text(contentX, 90, "TESTMODUS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 58));
        title.setFill(Color.web("#ffd60a"));

        Text help = new Text(contentX, 135,
                "ESC: zurück zum Menü  •  Testbrett enthält Itemshop-, Münz- und Minispiel-Buttons");
        help.setFont(Font.font("Arial", 16));
        help.setFill(Color.LIGHTGRAY);

        Button board = new Button("Testbrett öffnen");
        styleChoiceBtn(board, contentW);
        board.setLayoutX(contentX);
        board.setLayoutY(175);
        board.setOnAction(e -> startTestBoard());

        Text miniTitle = new Text(contentX, 255, "Minispiele testen:");
        miniTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        miniTitle.setFill(Color.WHITE);

        Button testTicTacToeBot = testButton("TicTacToe: Allein vs Bot", contentX, 290);
        testTicTacToeBot.setOnAction(e -> startTicTacToeBotTest());

        Button testTicTacToePlayers = testButton("TicTacToe: 2 Spieler", rightColumnX, 290);
        testTicTacToePlayers.setOnAction(e -> startTicTacToePlayersTest());

        Button testButtonMashBots = testButton("Button Mash: Allein vs Bots", contentX, 340);
        testButtonMashBots.setOnAction(e -> startButtonMashBotTest());

        Button testButtonMashPlayers = testButton("Button Mash: 2 Spieler", rightColumnX, 340);
        testButtonMashPlayers.setOnAction(e -> startButtonMashPlayersTest());

        Button testPongBot = testButton("Pong: Allein vs Bot", contentX, 390);
        testPongBot.setOnAction(e -> startPongBotTest());

        Button testPongPlayers = testButton("Pong: 2 Spieler", rightColumnX, 390);
        testPongPlayers.setOnAction(e -> startPongPlayersTest());

        Button testBattleship = testButton("Schiffe versenken: vs Bot", contentX, 440);
        testBattleship.setOnAction(e -> startBattleshipTest());

        Button testBattleshipPlayers = testButton("Schiffe versenken: 2 Spieler", rightColumnX, 440);
        testBattleshipPlayers.setOnAction(e -> startBattleshipPlayersTest());

        Button back = new Button("Zurück zum Menü");
        styleChoiceBtn(back, contentW);
        back.setLayoutX(contentX);
        back.setLayoutY(515);
        back.setOnAction(e -> engine.setScene(new MenuScene(engine)));

        pane.getChildren().addAll(
                bg, title, help, board, miniTitle,
                testTicTacToeBot, testTicTacToePlayers,
                testButtonMashBots, testButtonMashPlayers,
                testPongBot, testPongPlayers,
                testBattleship, testBattleshipPlayers,
                back
        );
    }

    private Button testButton(String label, double x, double y) {
        Button b = new Button(label);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        b.setPrefWidth(300);
        b.setPrefHeight(42);
        b.setLayoutX(x);
        b.setLayoutY(y);
        return b;
    }

    private void startTestBoard() {
        engine.getState().restartMatch(2, starsGoal);
        engine.setScene(new BoardScene(engine, true));
    }

    private void startTicTacToeBotTest() {
        GameState state = engine.getState();
        state.restartMatch(1, starsGoal);
        List<Player> players = state.getPlayers();
        TicTacToeGame game = new TicTacToeGame(List.of(players.getFirst()), engine.getPane(), 0.35);
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startTicTacToePlayersTest() {
        GameState state = engine.getState();
        state.restartMatch(2, starsGoal);
        List<Player> players = state.getPlayers();
        TicTacToeGame game = new TicTacToeGame(List.of(players.get(0), players.get(1)), engine.getPane(), 0.0);
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startButtonMashBotTest() {
        GameState state = engine.getState();
        state.restartMatch(1, starsGoal);
        ButtonMashGame game = new ButtonMashGame(state.getPlayers(), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startButtonMashPlayersTest() {
        GameState state = engine.getState();
        state.restartMatch(2, starsGoal);
        List<Player> players = state.getPlayers();
        ButtonMashGame game = new ButtonMashGame(List.of(players.get(0), players.get(1)), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startPongBotTest() {
        GameState state = engine.getState();
        state.restartMatch(1, starsGoal);
        List<Player> players = state.getPlayers();
        PongGame game = new PongGame(List.of(players.getFirst()), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startPongPlayersTest() {
        GameState state = engine.getState();
        state.restartMatch(2, starsGoal);
        List<Player> players = state.getPlayers();
        PongGame game = new PongGame(List.of(players.get(0), players.get(1)), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startBattleshipTest() {
        GameState state = engine.getState();
        state.restartMatch(1, starsGoal);
        List<Player> players = state.getPlayers();
        BattleshipGame game = new BattleshipGame(List.of(players.getFirst()), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private void startBattleshipPlayersTest() {
        GameState state = engine.getState();
        state.restartMatch(2, starsGoal);
        List<Player> players = state.getPlayers();
        BattleshipGame game = new BattleshipGame(List.of(players.get(0), players.get(1)), engine.getPane());
        engine.setScene(new MiniGameScene(engine, game, true));
    }

    private static void styleChoiceBtn(Button b, double width) {
        b.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        b.setPrefWidth(width);
        b.setPrefHeight(44);
    }

    @Override
    public void update(double dt, InputHandler input) {
        if (input.wasJustPressed(KeyCode.ESCAPE)) {
            engine.setScene(new MenuScene(engine));
        }
    }
}
