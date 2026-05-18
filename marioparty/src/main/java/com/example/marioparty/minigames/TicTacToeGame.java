package com.example.marioparty.minigames;

import com.example.marioparty.Main;
import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Player;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

public class TicTacToeGame extends MiniGame {

    private static final double CELL     = 140;
    private static final double BOARD_PX = CELL * TicTacToeBoard.SIZE;
    private static final double OFFSET_X = (Main.WIDTH - BOARD_PX) / 2.0;
    private static final double OFFSET_Y = 170;
    private static final double BOT_DELAY = 0.5;

    private final TicTacToeBoard board = new TicTacToeBoard();
    private final List<Player> players;
    private final boolean vsBot;
    private final TicTacToeBot bot;

    private int currentMark = TicTacToeBoard.X;
    private double botTimer = 0;

    private Text statusText;
    private final Group[][] cellGroups = new Group[TicTacToeBoard.SIZE][TicTacToeBoard.SIZE];
    private Line winLine;

    public TicTacToeGame(List<Player> players, Pane pane, double botErrorRate) {
        super(pane);
        if (players == null || players.isEmpty() || players.size() > 2)
            throw new IllegalArgumentException("TicTacToe: 1 oder 2 Spieler erwartet");
        this.players = players;
        this.vsBot   = players.size() == 1;
        this.bot     = vsBot ? new TicTacToeBot(TicTacToeBoard.O, botErrorRate) : null;
    }

    @Override public String getName() { return "TicTacToe"; }

    @Override public String getDescription() {
        return vsBot ? "Du (X) gegen den Computer (O). Klicke auf ein Feld."
                     : "X gegen O - abwechselnd per Mausklick.";
    }

    @Override
    protected void onStart() {
        Text title = new Text(Main.WIDTH / 2.0 - 95, 80, "TicTacToe");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setFill(Color.WHITE);

        statusText = new Text(OFFSET_X, OFFSET_Y - 25, "");
        statusText.setFont(Font.font("Arial", 22));
        statusText.setFill(Color.WHITE);

        Group grid = new Group();
        for (int i = 1; i < TicTacToeBoard.SIZE; i++) {
            Line v = new Line(OFFSET_X + i * CELL, OFFSET_Y, OFFSET_X + i * CELL, OFFSET_Y + BOARD_PX);
            v.setStroke(Color.WHITE); v.setStrokeWidth(3);
            Line h = new Line(OFFSET_X, OFFSET_Y + i * CELL, OFFSET_X + BOARD_PX, OFFSET_Y + i * CELL);
            h.setStroke(Color.WHITE); h.setStrokeWidth(3);
            grid.getChildren().addAll(v, h);
        }

        for (int r = 0; r < TicTacToeBoard.SIZE; r++)
            for (int c = 0; c < TicTacToeBoard.SIZE; c++) {
                cellGroups[r][c] = new Group();
                pane.getChildren().add(cellGroups[r][c]);
            }

        winLine = new Line();
        winLine.setStroke(Color.YELLOW);
        winLine.setStrokeWidth(8);
        winLine.setVisible(false);

        pane.getChildren().addAll(title, grid, statusText, winLine);
        updateStatus();
    }

    @Override
    public void update(double dt, InputHandler input) {
        if (board.isGameOver()) {
            if (!finished) finishGame();
            return;
        }

        if (vsBot && currentMark == TicTacToeBoard.O) {
            botTimer += dt;
            if (botTimer >= BOT_DELAY) {
                int[] move = bot.findBestMove(board);
                if (move != null) {
                    board.place(move[0], move[1], TicTacToeBoard.O);
                    drawMark(move[0], move[1], TicTacToeBoard.O);
                }
                currentMark = TicTacToeBoard.X;
                botTimer    = 0;
                updateStatus();
            }
            return;
        }

        if (input.wasMouseJustPressed()) {
            int col = (int) Math.floor((input.getMouseX() - OFFSET_X) / CELL);
            int row = (int) Math.floor((input.getMouseY() - OFFSET_Y) / CELL);
            if (row >= 0 && row < TicTacToeBoard.SIZE
                    && col >= 0 && col < TicTacToeBoard.SIZE
                    && board.place(row, col, currentMark)) {
                drawMark(row, col, currentMark);
                currentMark = (currentMark == TicTacToeBoard.X) ? TicTacToeBoard.O : TicTacToeBoard.X;
                updateStatus();
            }
        }
    }

    private void drawMark(int row, int col, int mark) {
        double cx = OFFSET_X + col * CELL + CELL / 2;
        double cy = OFFSET_Y + row * CELL + CELL / 2;
        double s  = CELL * 0.3;
        if (mark == TicTacToeBoard.X) {
            Line l1 = new Line(cx - s, cy - s, cx + s, cy + s);
            Line l2 = new Line(cx - s, cy + s, cx + s, cy - s);
            l1.setStroke(Color.TOMATO); l1.setStrokeWidth(8);
            l2.setStroke(Color.TOMATO); l2.setStrokeWidth(8);
            cellGroups[row][col].getChildren().addAll(l1, l2);
        } else {
            Circle o = new Circle(cx, cy, s);
            o.setFill(Color.TRANSPARENT);
            o.setStroke(Color.DEEPSKYBLUE);
            o.setStrokeWidth(8);
            cellGroups[row][col].getChildren().add(o);
        }
    }

    private void updateStatus() {
        if (statusText == null) return;
        String who = vsBot
                ? (currentMark == TicTacToeBoard.X ? players.getFirst().getName() : "Computer")
                : players.get(currentMark == TicTacToeBoard.X ? 0 : 1).getName();
        statusText.setText("Am Zug: " + who);
    }

    private void finishGame() {
        int w = board.getWinner();
        if      (w == TicTacToeBoard.X) winner = players.get(0);
        else if (w == TicTacToeBoard.O) winner = vsBot ? new com.example.marioparty.model.Player("Computer", javafx.scene.paint.Color.GRAY, false) : players.get(1);
        else                            winner = null;
        finished = true;

        int[][] line = board.getWinningLine();
        if (line != null) {
            winLine.setStartX(OFFSET_X + line[0][1] * CELL + CELL / 2);
            winLine.setStartY(OFFSET_Y + line[0][0] * CELL + CELL / 2);
            winLine.setEndX  (OFFSET_X + line[2][1] * CELL + CELL / 2);
            winLine.setEndY  (OFFSET_Y + line[2][0] * CELL + CELL / 2);
            winLine.setVisible(true);
        }
    }
}
