package com.example.marioparty.model;

public class Field {

    public enum Type {
        BLUE,
        RED,
        STAR,
        NEUTRAL,
        START,
        ITEM_SHOP
    }

    private final Type type;
    private final double x;
    private final double y;

    public Field(Type type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public void onLand(Player player) {
        switch (type) {
            case BLUE -> player.addCoins(3);
            case RED -> player.addCoins(-3);
            case STAR, NEUTRAL, START, ITEM_SHOP -> { }
        }
    }

    public Type getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
