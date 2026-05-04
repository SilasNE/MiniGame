package game.model;

import java.util.Random;

/**
 * Würfel 1..6. Statisch, weil zustandslos.
 */
public final class Dice {

    private static final Random rng = new Random();

    private Dice() {}

    public static int roll() {
        return rng.nextInt(6) + 1;
    }
}
