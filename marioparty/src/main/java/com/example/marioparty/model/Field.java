package com.example.marioparty.model;

/**
 * Ein einzelnes Feld auf dem Brett. Hat einen Typ und eine Pixel-Position
 * (für das Rendering — die Logik selbst kennt keine Pixel).
 */
public class Field {

    public enum Type {
        BLUE,    // +3 Münzen
        RED,     // -3 Münzen
        STAR,    // Erweiterungspunkt: Stern für 20 Münzen kaufen
        EVENT,   // Erweiterungspunkt: Zufallsereignis
        START    // Startfeld
    }

    private final Type type;
    private final double x, y;

    public Field(Type type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    /** Wird aufgerufen, wenn ein Spieler auf dem Feld landet. */
    public void onLand(Player player) {
        switch (type) {
            case BLUE  -> player.addCoins(3);
            case RED   -> player.addCoins(-3);
            case STAR, EVENT, START -> { /* in der Szene behandeln */ }
        }
    }

    public Type getType() { return type; }
    public double getX()  { return x; }
    public double getY()  { return y; }
}
