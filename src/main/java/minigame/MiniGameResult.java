package minigame;

import model.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Ergebnis eines Minispiels. Hält die Platzierung jedes Spielers
 * und berechnet die Münzbelohnung.
 */
public class MiniGameResult {

    /** Spieler -> Platzierung (1 = erster Platz) */
    private final Map<Player, Integer> ranking;

    public MiniGameResult() {
        this.ranking = new HashMap<>();
    }

    public void setRank(Player player, int rank) {
        ranking.put(player, rank);
    }

    public Map<Player, Integer> getRanking() {
        return ranking;
    }

    /**
     * Belohnung nach Platzierung. Anpassbar je nach Geschmack.
     */
    public int getReward(Player p) {
        Integer rank = ranking.get(p);
        if (rank == null) return 0;
        switch (rank) {
            case 1: return 10;
            case 2: return 5;
            case 3: return 2;
            default: return 0;
        }
    }
}
