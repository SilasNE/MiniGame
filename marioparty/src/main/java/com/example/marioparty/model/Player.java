package com.example.marioparty.model;

import javafx.scene.paint.Color;

/**
 * Spielerdaten. UI-frei — die Farbe ist nur ein einfacher Repräsentations-Hint
 * für das Rendering, kein Sprite.
 */
public class Player {

    private final String name;
    private final Color color;
    /** Position = Knoten-Id im {@link Board}-Graphen. */
    private int boardKnotId = 0;
    /** Genug für Stern-Kauf ({@link Board#STAR_COIN_COST}) nach ein paar Feldern. */
    private int coins = 25;
    private int stars = 0;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName()       { return name; }
    public Color getColor()       { return color; }

    /** Alias für ältere Aufrufer — gleichbedeutend mit {@link #getBoardKnotId()}. */
    public int getBoardPosition() {
        return boardKnotId;
    }

    public int getBoardKnotId() {
        return boardKnotId;
    }

    public void setBoardKnotId(int knotId) {
        this.boardKnotId = knotId;
    }

    public int getCoins()         { return coins; }
    public int getStars()         { return stars; }

    public void addCoins(int n)   { coins = Math.max(0, coins + n); }
    public void addStars(int n)   { stars = Math.max(0, stars + n); }
}
