package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein Spieler mit Münzen, Sternen, Position und Inventar.
 */
public class Player {

    private final String name;
    private int coins;
    private int stars;
    private FieldNode currentPos;
    private final List<Item> inventory;

    public Player(String name, FieldNode startPos) {
        this.name = name;
        this.coins = 10;       // Startkapital
        this.stars = 0;
        this.currentPos = startPos;
        this.inventory = new ArrayList<>();
    }

    /** Münzen verändern (kann negativ sein). Kontostand kann nicht unter 0 fallen. */
    public void modifyCoins(int amount) {
        this.coins = Math.max(0, this.coins + amount);
    }

    /** Einen Stern hinzufügen. */
    public void addStar() {
        this.stars++;
    }

    // --- Getter / Setter ---
    public String getName() { return name; }
    public int getCoins() { return coins; }
    public int getStars() { return stars; }
    public FieldNode getCurrentPos() { return currentPos; }
    public void setCurrentPos(FieldNode currentPos) { this.currentPos = currentPos; }
    public List<Item> getInventory() { return inventory; }

    @Override
    public String toString() {
        return name + " [🪙" + coins + " ⭐" + stars + "]";
    }
}
