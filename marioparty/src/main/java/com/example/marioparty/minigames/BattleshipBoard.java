package com.example.marioparty.minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Reine Spielbrett-Logik fuer Schiffe versenken - ohne JavaFX.
 * Brett: 6x6, Zellzustaende:
 *   WATER = 0 (Wasser, noch kein Schuss)
 *   SHIP  = 1 (Schiff auf diesem Feld)
 *   MISS  = 2 (Schuss ins Wasser)
 *   HIT   = 3 (Schiff getroffen)
 * Bezug zur Vorlesung: Separation of Concerns, Datenkapselung.
 */
public class BattleshipBoard {

    public static final int SIZE  = 6;
    public static final int WATER = 0;
    public static final int SHIP  = 1;
    public static final int MISS  = 2;
    public static final int HIT   = 3;

    private final int[][] grid  = new int[SIZE][SIZE]; // Schiffsposition
    private final int[][] shots = new int[SIZE][SIZE]; // abgefeuerte Schuesse
    private int totalShipCells = 0;
    private int hitCount       = 0;

    /**
     * Platziert Schiffe der angegebenen Laengen zufaellig auf dem Brett.
     * Schiffe duerfen sich nicht ueberlappen.
     *
     * @param shipLengths Array mit den Laengen der Schiffe, z. B. {3, 2, 2}
     */
    public void placeShipsRandomly(int[] shipLengths) {
        Random rng = new Random();
        for (int len : shipLengths) {
            boolean placed = false;
            while (!placed) {
                boolean horiz = rng.nextBoolean();
                int maxR = horiz ? SIZE - 1 : SIZE - len;
                int maxC = horiz ? SIZE - len : SIZE - 1;
                int r = rng.nextInt(maxR + 1);
                int c = rng.nextInt(maxC + 1);

                boolean ok = true;
                for (int i = 0; i < len; i++) {
                    int row = r + (horiz ? 0 : i);
                    int col = c + (horiz ? i : 0);
                    if (grid[row][col] != WATER) { ok = false; break; }
                }
                if (ok) {
                    for (int i = 0; i < len; i++) {
                        int row = r + (horiz ? 0 : i);
                        int col = c + (horiz ? i : 0);
                        grid[row][col] = SHIP;
                        totalShipCells++;
                    }
                    placed = true;
                }
            }
        }
    }

    /** Gibt true zurueck, wenn auf dieses Feld noch nicht geschossen wurde. */
    public boolean canShoot(int r, int c) {
        return r >= 0 && r < SIZE && c >= 0 && c < SIZE && shots[r][c] == WATER;
    }

    /**
     * Schiesst auf das Feld (r, c).
     * @return true bei Treffer, false bei Wasser
     */
    public boolean shoot(int r, int c) {
        if (!canShoot(r, c)) return false;
        if (grid[r][c] == SHIP) {
            shots[r][c] = HIT;
            hitCount++;
            return true;
        }
        shots[r][c] = MISS;
        return false;
    }

    /** Alle Schiffe wurden versenkt. */
    public boolean isDefeated() {
        return totalShipCells > 0 && hitCount >= totalShipCells;
    }

    public int getGrid(int r, int c)  { return grid[r][c]; }
    public int getShot(int r, int c)  { return shots[r][c]; }

    /**
     * Prueft, ob ein Schiff der Laenge len ab (r,c) platziert werden kann.
     * Schiff darf das Brett nicht verlassen und nicht ein belegtes Feld beruehren.
     *
     * @param r     Startzeile
     * @param c     Startspalte
     * @param len   Schiffslaenge
     * @param horiz true = horizontal, false = vertikal
     */
    public boolean canPlaceShip(int r, int c, int len, boolean horiz) {
        for (int i = 0; i < len; i++) {
            int row = r + (horiz ? 0 : i);
            int col = c + (horiz ? i : 0);
            if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return false;
            if (grid[row][col] != WATER) return false;
        }
        return true;
    }

    /**
     * Platziert ein Schiff der Laenge len ab (r,c).
     * Vorher sollte canPlaceShip() geprueft werden.
     */
    public void placeShip(int r, int c, int len, boolean horiz) {
        for (int i = 0; i < len; i++) {
            int row = r + (horiz ? 0 : i);
            int col = c + (horiz ? i : 0);
            grid[row][col] = SHIP;
            totalShipCells++;
        }
    }

    /** Alle Felder, auf die noch nicht geschossen wurde. */
    public List<int[]> getUnshotCells() {
        List<int[]> result = new ArrayList<>();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (shots[r][c] == WATER) result.add(new int[]{r, c});
        return result;
    }
}
