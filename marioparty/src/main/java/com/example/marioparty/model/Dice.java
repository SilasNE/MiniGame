package com.example.marioparty.model;

import java.util.Random;

public final class Dice {

    private static final Random rng = new Random();

    private Dice() {}

    public static int roll() {
        return rng.nextInt(6) + 1;
    }
}
