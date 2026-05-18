package com.example.marioparty.minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * KI fuer Schiffe versenken: Hunt-and-Target-Strategie.
 * Hunt-Modus:   Schachbrettmuster-Optimierung (jedes zweite Feld), um kleine
 *               Schiffe (Laenge 2) effizienter zu finden.
 * Target-Modus: Nach einem Treffer werden alle vier Nachbarfelder in eine
 *               Warteschlange eingetragen und der Reihe nach geprueft.
 * Bezug zur Vorlesung: Zustandsautomat (Hunt/Target), Heuristik, Greedy-Strategie.
 */
public class BattleshipAi {

    private final List<int[]> targetQueue = new ArrayList<>();
    private static final Random RNG = new Random();

    /**
     * Waehlt das naechste Zielfeld.
     * Zuerst aus der Target-Warteschlange (nach Treffer), sonst zufaelliges Feld.
     */
    public int[] findNextShot(BattleshipBoard board) {
        // Target-Modus: priorisiere Felder neben einem Treffer
        while (!targetQueue.isEmpty()) {
            int[] next = targetQueue.removeFirst();
            if (board.canShoot(next[0], next[1])) return next;
        }

        // Hunt-Modus: Schachbrettmuster (nur gerade Felder) fuer bessere Abdeckung
        List<int[]> candidates = new ArrayList<>();
        for (int[] cell : board.getUnshotCells())
            if ((cell[0] + cell[1]) % 2 == 0) candidates.add(cell);

        // Fallback: alle verbliebenen Felder
        if (candidates.isEmpty()) candidates = board.getUnshotCells();
        if (candidates.isEmpty()) return null;

        return candidates.get(RNG.nextInt(candidates.size()));
    }

    /**
     * Wird nach einem Treffer aufgerufen.
     * Traegt alle noch ungeschossenen Nachbarfelder in die Warteschlange ein.
     */
    public void onHit(int r, int c, BattleshipBoard board) {
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (board.canShoot(nr, nc)) targetQueue.add(new int[]{nr, nc});
        }
    }

}
