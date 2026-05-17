package com.example.marioparty.model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Globaler Spielzustand: Spielerliste, aktuelles Brett, Rundenzähler.
 * Partie endet, sobald ein Spieler die eingestellte Sternanzahl ({@link #starsToWin}) erreicht hat.
 */
public class GameState {

    private final List<Player> players = new ArrayList<>();
    private Board board;
    private int currentPlayerIndex = 0;
    private int round = 1;
    /** Gewinnt, wer zuerst so viele Sterne hat (3, 5 oder 7). */
    private int starsToWin = 5;

    public GameState() {
        board = new Board();
        restartMatch(2, 5);
    }

    /**
     * Neues Spiel: Brett neu, Runden zurück, 4 Spieler — die ersten {@code humanPlayerCount}
     * (1 oder 2) sind menschlich, der Rest CPU.
     *
     * @param starsGoal Ziel-Sternanzahl (wird auf 3, 5 oder 7 begrenzt).
     */
    public void restartMatch(int humanPlayerCount, int starsGoal) {
        int h = Math.min(2, Math.max(1, humanPlayerCount));
        if (starsGoal <= 3) {
            this.starsToWin = 3;
        } else if (starsGoal >= 7) {
            this.starsToWin = 7;
        } else {
            this.starsToWin = 5;
        }
        this.board = new Board();
        currentPlayerIndex = 0;
        round = 1;
        players.clear();
        players.add(new Player("Mario", Color.RED, h >= 1));
        players.add(new Player("Luigi", Color.LIMEGREEN, h >= 2));
        players.add(new Player("Peach", Color.HOTPINK, false));
        players.add(new Player("Donkey Kong", Color.SADDLEBROWN, false));
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (currentPlayerIndex == 0) {
            round++;
        }
    }

    public boolean isGameOver() {
        return players.stream().anyMatch(p -> p.getStars() >= starsToWin);
    }

    public Player getWinner() {
        return players.stream()
                .max(Comparator.<Player>comparingInt(Player::getStars)
                        .thenComparingInt(Player::getCoins))
                .orElse(players.get(0));
    }

    public List<Player> getPlayers()    { return players; }
    public Player getCurrentPlayer()    { return players.get(currentPlayerIndex); }
    public int getCurrentPlayerIndex()  { return currentPlayerIndex; }
    public Board getBoard()             { return board; }
    public int getRound()               { return round; }
    public int getStarsToWin()          { return starsToWin; }
}
