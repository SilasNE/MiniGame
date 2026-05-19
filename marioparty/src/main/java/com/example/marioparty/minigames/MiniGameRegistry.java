package com.example.marioparty.minigames;

import com.example.marioparty.model.Player;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public final class MiniGameRegistry {

    public record Entry(String name, int minHumans, int maxHumans,
                        BiFunction<List<Player>, Pane, MiniGame> factory) {}

    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Random RNG = new Random();

    static {
        register(new Entry("Button Masher",      1, 4, ButtonMashGame::new));
        register(new Entry("TicTacToe (vs Bot)", 1, 1,
                (players, pane) -> new TicTacToeGame(
                        humanOnly(players).subList(0, 1), pane, botErrorRate(players))));
        register(new Entry("TicTacToe (1v1)",    2, 4,
                (players, pane) -> new TicTacToeGame(
                        humanOnly(players).subList(0, 2), pane, 0.0)));
        register(new Entry("Pong (vs Computer)", 1, 1,
                (players, pane) -> new PongGame(humanOnly(players).subList(0, 1), pane)));
        register(new Entry("Pong (1v1)",         2, 2,
                (players, pane) -> new PongGame(humanOnly(players).subList(0, 2), pane)));
        register(new Entry("Schiffe versenken",       1, 1,
                (players, pane) -> new BattleshipGame(humanOnly(players).subList(0, 1), pane)));
        register(new Entry("Schiffe versenken (1v1)", 2, 4,
                (players, pane) -> new BattleshipGame(humanOnly(players).subList(0, 2), pane)));
        register(new Entry("Dino Run (1v1)", 2, 4,
                (players, pane) -> new DinoGame(humanOnly(players).subList(0, 2), pane)));
        register(new Entry("Dino Run (vs Bot)", 1, 1,
                (players, pane) -> new DinoGame(players.subList(0, 2), pane)));
        register(new Entry("Memory (vs Bot)", 1, 1,
                (players, pane) -> new MemoryGame(players.subList(0, 2), pane)));
        register(new Entry("Memory (1v1)",    2, 4,
                (players, pane) -> new MemoryGame(humanOnly(players).subList(0, 2), pane)));
    }

    private MiniGameRegistry() {}

    public static void register(Entry entry) { ENTRIES.add(entry); }

    private static List<Player> humanOnly(List<Player> players) {
        return players.stream().filter(Player::isHuman).toList();
    }

    private static double botErrorRate(List<Player> players) {
        Player human = humanOnly(players).getFirst();
        double avgCpu = players.stream()
                .filter(p -> !p.isHuman())
                .mapToInt(p -> p.getCoins() + p.getStars() * 20)
                .average()
                .orElse(25.0);
        double lead = (human.getCoins() + human.getStars() * 20) - avgCpu;
        if (lead > 10)  return 0.05;
        if (lead >= 0)  return 0.15;
        return 0.25;
    }

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
