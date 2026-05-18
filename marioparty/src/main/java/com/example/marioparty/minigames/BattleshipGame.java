package com.example.marioparty.minigames;

import com.example.marioparty.Main;
import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Player;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Random;

public class BattleshipGame extends MiniGame {

    private static final int[][] FLEET_OPTIONS = {
            {3, 2, 2}, {3, 3}, {4, 2}, {2, 2, 2}
    };

    private static final double CELL          = 50;
    private static final double BOARD_PX      = CELL * BattleshipBoard.SIZE;
    private static final double GAP           = 80;
    private static final double OFFSET_LEFT_X = (Main.WIDTH - 2 * BOARD_PX - GAP) / 2.0;
    private static final double OFFSET_RIGHT_X= OFFSET_LEFT_X + BOARD_PX + GAP;
    private static final double[] BOARD_X     = { OFFSET_LEFT_X, OFFSET_RIGHT_X };
    private static final double OFFSET_Y      = 210;
    private static final double BOT_DELAY     = 1.0;
    private static final Random RNG           = new Random();

    private enum Phase { PLACING, HANDOVER, PLAYING, REVEAL }

    private final List<Player>    players;
    private final boolean         vsBot;

    private final BattleshipBoard[] boards = { new BattleshipBoard(), new BattleshipBoard() };
    private final BattleshipBot      ai     = new BattleshipBot();

    private int[]     shipLengths;
    private boolean[] shipHorizontal;
    private int       placingIndex       = 0;
    private int       placingPlayerIndex = 0;

    private Phase   phase              = Phase.PLACING;
    private int     currentPlayerIndex = 0;
    private boolean lastShotWasHit     = false;
    private double  botTimer           = 0;
    private double  revealTimer        = 0;

    private Rectangle[][] leftCells,   rightCells;
    private Group[][]     leftMarkers, rightMarkers;
    private Text          statusText;
    private Text          rotateHint;
    private Group         previewGroup;
    private Group         handoverOverlay;
    private Text          handoverLine1;

    public BattleshipGame(List<Player> players, Pane pane) {
        super(pane);
        if (players == null || players.isEmpty())
            throw new IllegalArgumentException("Schiffe versenken: mindestens 1 Spieler erwartet");
        this.players = players;
        this.vsBot   = (players.size() == 1);
    }

    @Override public String getName() { return "Schiffe versenken"; }

    @Override public String getDescription() {
        if (vsBot)
            return players.getFirst().getName() + " vs. Computer – Versenke alle Schiffe des Gegners!";
        return players.get(0).getName() + " vs. " + players.get(1).getName()
                + " – Versenke alle Schiffe des Gegners!";
    }

    @Override
    protected void onStart() {
        shipLengths    = FLEET_OPTIONS[RNG.nextInt(FLEET_OPTIONS.length)];
        shipHorizontal = randomOrientations();

        if (vsBot) boards[1].placeShipsRandomly(shipLengths);

        Text title = new Text(Main.WIDTH / 2.0 - 130, 70, "Schiffe versenken");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 34));
        title.setFill(Color.WHITE);

        String leftName  = players.get(0).getName();
        String rightName = vsBot ? "Computer" : players.get(1).getName();
        Text leftLabel  = new Text(OFFSET_LEFT_X  + BOARD_PX / 2.0 - 40, OFFSET_Y - 32, leftName);
        Text rightLabel = new Text(OFFSET_RIGHT_X + BOARD_PX / 2.0 - 55, OFFSET_Y - 32, rightName);
        leftLabel .setFont(Font.font("Arial", FontWeight.BOLD, 16)); leftLabel .setFill(Color.LIGHTBLUE);
        rightLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16)); rightLabel.setFill(Color.TOMATO);

        statusText = new Text(OFFSET_LEFT_X, OFFSET_Y + BOARD_PX + 40, "");
        statusText.setFont(Font.font("Arial", 18));
        statusText.setFill(Color.WHITE);

        rotateHint = new Text(OFFSET_LEFT_X, OFFSET_Y + BOARD_PX + 68, "[ R ]  =  Schiff drehen");
        rotateHint.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        rotateHint.setFill(Color.web("#ffd60a"));
        rotateHint.setVisible(false);

        leftCells    = new Rectangle[BattleshipBoard.SIZE][BattleshipBoard.SIZE];
        rightCells   = new Rectangle[BattleshipBoard.SIZE][BattleshipBoard.SIZE];
        leftMarkers  = new Group[BattleshipBoard.SIZE][BattleshipBoard.SIZE];
        rightMarkers = new Group[BattleshipBoard.SIZE][BattleshipBoard.SIZE];

        for (int r = 0; r < BattleshipBoard.SIZE; r++) {
            for (int c = 0; c < BattleshipBoard.SIZE; c++) {
                leftCells[r][c]    = makeCell(OFFSET_LEFT_X  + c * CELL, OFFSET_Y + r * CELL);
                rightCells[r][c]   = makeCell(OFFSET_RIGHT_X + c * CELL, OFFSET_Y + r * CELL);
                leftMarkers[r][c]  = new Group();
                rightMarkers[r][c] = new Group();
                pane.getChildren().addAll(leftCells[r][c],  leftMarkers[r][c],
                                          rightCells[r][c], rightMarkers[r][c]);
            }
        }

        previewGroup = new Group();

        Rectangle overlayBg = new Rectangle(0, 0, Main.WIDTH, Main.HEIGHT);
        overlayBg.setFill(Color.rgb(0, 0, 0, 0.93));
        handoverLine1 = new Text(0, Main.HEIGHT / 2.0 - 30, "");
        handoverLine1.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        handoverLine1.setFill(Color.WHITE);
        Text handoverSpaceHint = new Text(0, Main.HEIGHT / 2.0 + 30,
                "[ LEERTASTE ]  –  ich bin bereit!");
        handoverSpaceHint.setFont(Font.font("Arial", 22));
        handoverSpaceHint.setFill(Color.web("#ffd60a"));
        handoverOverlay = new Group(overlayBg, handoverLine1, handoverSpaceHint);
        handoverOverlay.setVisible(false);

        pane.getChildren().addAll(title, leftLabel, rightLabel, statusText, rotateHint,
                                   previewGroup, handoverOverlay);
        addCoordinateLabels(OFFSET_LEFT_X);
        addCoordinateLabels(OFFSET_RIGHT_X);

        phase              = Phase.PLACING;
        placingPlayerIndex = 0;
        placingIndex       = 0;
        updateStatus();
    }

    @Override
    public void update(double dt, InputHandler input) {
        if (finished) return;
        switch (phase) {
            case PLACING  -> updatePlacing(input);
            case HANDOVER -> updateHandover(input);
            case PLAYING  -> updatePlaying(dt, input);
            case REVEAL   -> { revealTimer += dt; if (revealTimer >= 3.0) finished = true; }
        }
    }

    private void updatePlacing(InputHandler input) {
        int     len   = shipLengths[placingIndex];
        double  boardX = BOARD_X[placingPlayerIndex];
        BattleshipBoard board = boards[placingPlayerIndex];

        if (input.wasJustPressed(KeyCode.R)) {
            shipHorizontal[placingIndex] = !shipHorizontal[placingIndex];
            updateStatus();
        }

        int col = (int) Math.floor((input.getMouseX() - boardX) / CELL);
        int row = (int) Math.floor((input.getMouseY() - OFFSET_Y) / CELL);
        drawPreview(boardX, row, col, len, shipHorizontal[placingIndex], board);

        if (input.wasMouseJustPressed() && board.canPlaceShip(row, col, len, shipHorizontal[placingIndex])) {
            board.placeShip(row, col, len, shipHorizontal[placingIndex]);
            placingIndex++;
            previewGroup.getChildren().clear();
            refreshBoard(placingPlayerIndex, true);

            if (placingIndex >= shipLengths.length) {
                finishPlacing();
            } else {
                updateStatus();
            }
        }
    }

    private void finishPlacing() {
        if (placingPlayerIndex == 0 && !vsBot) {
            placingPlayerIndex = 1;
            placingIndex       = 0;
            shipHorizontal     = randomOrientations();
            showHandover();
        } else {
            phase              = Phase.PLAYING;
            currentPlayerIndex = 0;
            if (!vsBot) {
                refreshBoard(0, false);
                refreshBoard(1, false);
            }
            rotateHint.setVisible(false);
            updateStatus();
        }
    }

    private void showHandover() {
        previewGroup.setVisible(false);
        rotateHint.setVisible(false);
        refreshBoard(0, false);

        String name = players.get(placingPlayerIndex).getName();
        String msg  = "Gerät bitte an " + name + " weitergeben  –  nicht hinschauen!";
        handoverLine1.setText(msg);

        double textW1 = msg.length() * 14.5;
        handoverLine1.setX(Main.WIDTH / 2.0 - textW1 / 2.0);
        handoverLine1.getParent().getChildrenUnmodifiable()
                .stream().filter(n -> n instanceof Text && n != handoverLine1)
                .forEach(n -> ((Text) n).setX(Main.WIDTH / 2.0 - 165));

        handoverOverlay.setVisible(true);
        phase = Phase.HANDOVER;
        updateStatus();
    }

    private void updateHandover(InputHandler input) {
        if (input.wasJustPressed(KeyCode.SPACE)) {
            handoverOverlay.setVisible(false);
            previewGroup.setVisible(true);
            rotateHint.setVisible(true);
            phase = Phase.PLACING;
            updateStatus();
        }
    }

    private void updatePlaying(double dt, InputHandler input) {
        if (vsBot && currentPlayerIndex == 1) {
            botTimer += dt;
            if (botTimer >= BOT_DELAY) {
                int[] move = ai.findNextShot(boards[0]);
                boolean hit = false;
                if (move != null) {
                    hit = boards[0].shoot(move[0], move[1]);
                    if (hit) ai.onHit(move[0], move[1], boards[0]);
                    refreshBoard(0, true);
                }
                if (boards[0].isDefeated()) { endGame(1); return; }
                lastShotWasHit = hit;
                if (!hit) currentPlayerIndex = 0;
                botTimer = 0;
                updateStatus();
            }
            return;
        }

        int     targetIndex  = 1 - currentPlayerIndex;
        double  targetBoardX = BOARD_X[targetIndex];
        Rectangle[][] targetCells = (targetIndex == 0) ? leftCells : rightCells;

        for (int r = 0; r < BattleshipBoard.SIZE; r++)
            for (int c = 0; c < BattleshipBoard.SIZE; c++)
                if (boards[targetIndex].getShot(r, c) == BattleshipBoard.WATER)
                    targetCells[r][c].setFill(isHovered(input, targetBoardX, r, c)
                            ? Color.web("#2d5986") : Color.web("#1a3a5c"));

        if (input.wasMouseJustPressed()) {
            int col = (int) Math.floor((input.getMouseX() - targetBoardX) / CELL);
            int row = (int) Math.floor((input.getMouseY() - OFFSET_Y)     / CELL);
            if (boards[targetIndex].canShoot(row, col)) {
                boolean hit = boards[targetIndex].shoot(row, col);
                if (hit) ai.onHit(row, col, boards[targetIndex]);
                refreshBoard(targetIndex, false);
                if (boards[targetIndex].isDefeated()) { endGame(currentPlayerIndex); return; }
                lastShotWasHit = hit;
                if (!hit) currentPlayerIndex = targetIndex;
                botTimer = 0;
                updateStatus();
            }
        }
    }

    private void endGame(int winnerIndex) {
        if (winnerIndex == 0) {
            winner = players.getFirst();
        } else if (vsBot) {
            winner = new Player("Computer", javafx.scene.paint.Color.GRAY, false);
        } else {
            winner = players.get(1);
        }
        statusText.setText(winner.getName() + " gewinnt! Alle Schiffe versenkt!");
        phase = Phase.REVEAL;
        refreshBoard(0, true);
        refreshBoard(1, true);
    }

    private void refreshBoard(int boardIndex, boolean showShips) {
        Rectangle[][]   cells   = (boardIndex == 0) ? leftCells   : rightCells;
        Group[][]        markers = (boardIndex == 0) ? leftMarkers : rightMarkers;
        double           boardX  = BOARD_X[boardIndex];
        BattleshipBoard  board   = boards[boardIndex];

        for (int r = 0; r < BattleshipBoard.SIZE; r++) {
            for (int c = 0; c < BattleshipBoard.SIZE; c++) {
                markers[r][c].getChildren().clear();
                int shot = board.getShot(r, c);
                if (shot == BattleshipBoard.HIT) {
                    cells[r][c].setFill(Color.web("#7b0000"));
                    addCross(markers[r][c], boardX + c * CELL, OFFSET_Y + r * CELL);
                } else if (shot == BattleshipBoard.MISS) {
                    cells[r][c].setFill(Color.web("#1a3a5c"));
                    addDot(markers[r][c], boardX + c * CELL, OFFSET_Y + r * CELL);
                } else if (showShips && board.getGrid(r, c) == BattleshipBoard.SHIP) {
                    cells[r][c].setFill(Color.web("#2e6da4"));
                } else {
                    cells[r][c].setFill(Color.web("#1a3a5c"));
                }
            }
        }
    }

    private void drawPreview(double boardX, int row, int col, int len, boolean horiz,
                              BattleshipBoard board) {
        previewGroup.getChildren().clear();
        boolean valid = board.canPlaceShip(row, col, len, horiz);
        Color c = valid ? Color.rgb(0, 200, 0, 0.5) : Color.rgb(200, 0, 0, 0.5);
        for (int i = 0; i < len; i++) {
            int r2 = row + (horiz ? 0 : i);
            int c2 = col + (horiz ? i : 0);
            if (r2 < 0 || r2 >= BattleshipBoard.SIZE || c2 < 0 || c2 >= BattleshipBoard.SIZE) continue;
            Rectangle rect = new Rectangle(
                    boardX + c2 * CELL + 1, OFFSET_Y + r2 * CELL + 1, CELL - 3, CELL - 3);
            rect.setFill(c);
            previewGroup.getChildren().add(rect);
        }
    }

    private void addCoordinateLabels(double boardX) {
        String[] colLabels = {"A", "B", "C", "D", "E", "F"};
        for (int c = 0; c < BattleshipBoard.SIZE; c++) {
            Text lbl = new Text(boardX + c * CELL + CELL / 2.0 - 6, OFFSET_Y - 10, colLabels[c]);
            lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            lbl.setFill(Color.LIGHTGRAY);
            pane.getChildren().add(lbl);
        }
        for (int r = 0; r < BattleshipBoard.SIZE; r++) {
            Text lbl = new Text(boardX - 22, OFFSET_Y + r * CELL + CELL / 2.0 + 5,
                    String.valueOf(r + 1));
            lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            lbl.setFill(Color.LIGHTGRAY);
            pane.getChildren().add(lbl);
        }
    }

    private void addCross(Group g, double x, double y) {
        double m = CELL * 0.22;
        Line l1 = new Line(x + m, y + m, x + CELL - m - 1, y + CELL - m - 1);
        Line l2 = new Line(x + CELL - m - 1, y + m, x + m, y + CELL - m - 1);
        l1.setStroke(Color.RED); l1.setStrokeWidth(5);
        l2.setStroke(Color.RED); l2.setStrokeWidth(5);
        g.getChildren().addAll(l1, l2);
    }

    private void addDot(Group g, double x, double y) {
        Circle dot = new Circle(x + CELL / 2.0 - 0.5, y + CELL / 2.0 - 0.5, CELL * 0.14);
        dot.setFill(Color.WHITE);
        g.getChildren().add(dot);
    }

    private Rectangle makeCell(double x, double y) {
        Rectangle rect = new Rectangle(x, y, CELL - 2, CELL - 2);
        rect.setFill(Color.web("#1a3a5c"));
        rect.setStroke(Color.web("#2d5986"));
        rect.setStrokeWidth(1);
        return rect;
    }

    private boolean isHovered(InputHandler input, double boardX, int r, int c) {
        double mx = input.getMouseX(), my = input.getMouseY();
        return mx >= boardX + c * CELL && mx < boardX + (c + 1) * CELL
            && my >= OFFSET_Y + r * CELL && my < OFFSET_Y + (r + 1) * CELL;
    }

    private boolean[] randomOrientations() {
        boolean[] result = new boolean[shipLengths.length];
        for (int i = 0; i < result.length; i++) result[i] = RNG.nextBoolean();
        return result;
    }

    private void updateStatus() {
        if (statusText == null) return;
        switch (phase) {
            case PLACING -> {
                int    len = shipLengths[placingIndex];
                String dir = shipHorizontal[placingIndex] ? "horizontal" : "vertikal";
                statusText.setText("Schiff " + (placingIndex + 1) + " von " + shipLengths.length
                        + "  –  Länge " + len + "  –  " + dir + "  –  Klick = platzieren");
                rotateHint.setVisible(true);
            }
            case HANDOVER -> {
                statusText.setText("");
                rotateHint.setVisible(false);
            }
            case PLAYING -> {
                rotateHint.setVisible(false);
                String bonus = lastShotWasHit ? "  Treffer – nochmal!" : "";
                if (vsBot) {
                    statusText.setText(currentPlayerIndex == 0
                            ? players.getFirst().getName() + " ist am Zug – klicke auf das Gegnerfeld" + bonus
                            : "Computer denkt nach..." + bonus);
                } else {
                    String current = players.get(currentPlayerIndex).getName();
                    String target  = players.get(1 - currentPlayerIndex).getName();
                    statusText.setText(current + " ist am Zug – klicke auf " + target + "s Feld" + bonus);
                }
            }
            case REVEAL -> rotateHint.setVisible(false);
        }
    }
}
