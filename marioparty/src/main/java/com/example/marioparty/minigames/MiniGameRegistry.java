package com.example.marioparty.minigames;

import com.example.marioparty.model.Player;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Zentrale Registrierungsstelle aller Minispiele (Factory-Pattern).
 * Neues Minispiel = eine Zeile hier ergaenzen, sonst nichts aendern.
 */
public final class MiniGameRegistry {

    public record Entry(String name, int minPlayers, int maxPlayers,
                        BiFunction<List<Player>, Pane, MiniGame> factory) {}

    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Random RNG = new Random();

    static {
        register(new Entry("Button Masher",      2, 4, ButtonMashGame::new));
        register(new Entry("TicTacToe (vs Bot)", 1, 1, TicTacToeGame::new));
        register(new Entry("TicTacToe (1v1)",    2, 4,
                (players, pane) -> new TicTacToeGame(players.subList(0, 2), pane)));
    }

    private MiniGameRegistry() {}

    public static void register(Entry entry) { ENTRIES.add(entry); }
    public static List<Entry> all()          { return Collections.unmodifiableList(ENTRIES); }

    public static MiniGame randomFor(List<Player> players, Pane pane) {
        List<Entry> compatible = ENTRIES.stream()
                .filter(e -> players.size() >= e.minPlayers() && players.size() <= e.maxPlayers())
                .toList();
        if (compatible.isEmpty())
            throw new IllegalStateException("Kein Minispiel fuer " + players.size() + " Spieler");
        return compatible.get(RNG.nextInt(compatible.size())).factory().apply(players, pane);
    }
}
