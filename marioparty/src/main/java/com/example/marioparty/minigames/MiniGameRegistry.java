package com.example.marioparty.minigames;

import com.example.marioparty.model.Player;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Zentrale Registrierungsstelle aller Minispiele (Factory-Pattern).
 * Neues Minispiel = eine Zeile im static-Block ergaenzen, sonst nichts aendern.
 * Die Filterung basiert auf der Anzahl der menschlichen Spieler,
 * damit bei 1 Mensch vs. 3 CPUs automatisch der Bot-Modus gewaehlt wird.
 */
public final class MiniGameRegistry {

    public record Entry(String name, int minHumans, int maxHumans,
                        BiFunction<List<Player>, Pane, MiniGame> factory) {}

    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Random RNG = new Random();

    static {
        // minHumans/maxHumans = Anzahl menschlicher Spieler, fuer die das Spiel passt
        register(new Entry("Button Masher",      1, 4, ButtonMashGame::new));
        register(new Entry("TicTacToe (vs Bot)", 1, 1,
                (players, pane) -> new TicTacToeGame(
                        humanOnly(players).subList(0, 1), pane, botErrorRate(players))));
        register(new Entry("TicTacToe (1v1)",    2, 4,
                (players, pane) -> new TicTacToeGame(
                        humanOnly(players).subList(0, 2), pane, 0.0)));

        register(new Entry("Pong (vs Computer)", 1, 1,
                (players, pane) -> new PongGame(humanOnly(players).subList(0, 1), pane)));

        register(new Entry("Pong (1v1)", 2, 2,
                (players, pane) -> new PongGame(humanOnly(players).subList(0, 2), pane)));

    }

    private MiniGameRegistry() {}

    public static void register(Entry entry) { ENTRIES.add(entry); }

    /** Gibt nur die menschlichen Spieler zurueck. */
    private static List<Player> humanOnly(List<Player> players) {
        return players.stream().filter(Player::isHuman).toList();
    }

    /**
     * Berechnet die Bot-Fehlerrate basierend auf dem Muenzvorsprung des Menschen.
     * Rubber-Banding: wer vorne liegt, bekommt einen haerteren Bot.
     * Vorsprung &gt; +10 Muenzen: 0.1 (schwer), 0 bis +10: 0.35 (mittel), &lt; 0: 0.6 (leicht).
     */
    private static double botErrorRate(List<Player> players) {
        Player human = humanOnly(players).getFirst();
        double avgCpu = players.stream()
                .filter(p -> !p.isHuman())
                .mapToInt(p -> p.getCoins() + p.getStars() * 20)
                .average()
                .orElse(25.0);
        double lead = (human.getCoins() + human.getStars() * 20) - avgCpu;
        if (lead > 10)  return 0.1;   // Spieler fuehrt → Bot spielt hart
        if (lead >= 0)  return 0.35;  // Ausgeglichen → mittlere Schwierigkeit
        return 0.6;                    // Spieler liegt hinten → Bot macht Fehler
    }

    /**
     * Waehlt zufaellig ein passendes Minispiel basierend auf der Anzahl
     * menschlicher Spieler aus.
     */
    public static MiniGame randomFor(List<Player> players, Pane pane) {
        int humanCount = (int) players.stream().filter(Player::isHuman).count();

        List<Entry> compatible = ENTRIES.stream()
                .filter(e -> humanCount >= e.minHumans() && humanCount <= e.maxHumans())
                .toList();
        if (compatible.isEmpty())
            throw new IllegalStateException("Kein Minispiel fuer " + humanCount + " menschliche Spieler");
        return compatible.get(RNG.nextInt(compatible.size())).factory().apply(players, pane);
    }
}
