package com.example.marioparty.model;

import com.example.marioparty.model.items.GameItem;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;





public class Player {

    private final String name;
    private final Color color;
    private final boolean human;

    private int boardKnotId = 0;

    private int coins = 25;
    private int stars = 0;
    private int rollBonus = 0;
    private final List<GameItem> inventory = new ArrayList<>();

    public Player(String name, Color color, boolean human) {
        this.name = name;
        this.color = color;
        this.human = human;
    }

    public boolean isHuman() {
        return human;
    }

    public String getName()       { return name; }
    public Color getColor()       { return color; }

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

    public int getRollBonus() {
        return rollBonus;
    }

    public void addRollBonus(int n) {
        rollBonus = Math.max(0, rollBonus + n);
    }

    public void clearRollBonus() {
        rollBonus = 0;
    }

    public List<GameItem> getInventory() {
        return inventory;
    }

    public List<GameItem> getInventoryView() {
        return Collections.unmodifiableList(inventory);
    }

    public boolean hasUsableItems() {
        return !inventory.isEmpty();
    }

    public void addToInventory(GameItem item) {
        inventory.add(item);
    }
}
