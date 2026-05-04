package game.model;

import javafx.scene.paint.Color;

/**
 * Spielerdaten. UI-frei — die Farbe ist nur ein einfacher Repräsentations-Hint
 * für das Rendering, kein Sprite.
 */
public class Player {

    private final String name;
    private final Color color;
    private int boardPosition = 0;
    private int coins = 10;
    private int stars = 0;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public void move(int steps, int boardSize) {
        boardPosition = (boardPosition + steps + boardSize) % boardSize;
    }

    public String getName()       { return name; }
    public Color getColor()       { return color; }
    public int getBoardPosition() { return boardPosition; }
    public int getCoins()         { return coins; }
    public int getStars()         { return stars; }

    public void addCoins(int n)   { coins = Math.max(0, coins + n); }
    public void addStars(int n)   { stars = Math.max(0, stars + n); }
}
