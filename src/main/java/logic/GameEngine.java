package logic;

import model.*;
import minigame.MiniGame;
import minigame.MiniGameResult;
import minigame.TicTacToe;
import minigame.Memory;

import java.util.*;

/**
 * Zentraler Spielablauf: verwaltet Spielerliste, aktueller Zug, Runden,
 * Würfelwurf und Feldeffekte. Kennt keine UI – die UI ruft die Methoden auf.
 */
public class GameEngine {

    private final List<Player> players;
    private int currentPlayerIndex;
    private final BoardGraph board;
    private int roundCount;
    private final Random random = new Random();

    private static final int MAX_ROUNDS = 10;   // Spielende nach X Runden
    private static final int STAR_COST = 20;    // Sternpreis im Shop

    public GameEngine(List<Player> players, BoardGraph board) {
        if (players.size() < 2 || players.size() > 6) {
            throw new IllegalArgumentException("Spielerzahl muss zwischen 2 und 6 liegen.");
        }
        this.players = players;
        this.board = board;
        this.currentPlayerIndex = 0;
        this.roundCount = 1;
    }

    /** Würfelt eine Zahl von 1 bis 6. */
    public int handleDiceRoll() {
        return random.nextInt(6) + 1;
    }

    /**
     * Führt den Effekt des Feldes aus, auf dem der Spieler nach seinem Zug steht.
     */
    public void executeFieldEffect(Player p) {
        FieldType type = p.getCurrentPos().getType();
        switch (type) {
            case COIN_PLUS:
                p.modifyCoins(3);
                break;
            case COIN_MINUS:
                p.modifyCoins(-3);
                break;
            case STAR_SHOP:
                if (p.getCoins() >= STAR_COST && p.getCurrentPos().isHasStar()) {
                    p.modifyCoins(-STAR_COST);
                    p.addStar();
                    p.getCurrentPos().setHasStar(false);
                    // TODO: Stern auf ein zufälliges anderes Feld versetzen
                }
                break;
            case MINIGAME:
                // TODO: hier Minispiel triggern (UI erledigt das eigentliche Starten)
                break;
            case TELEPORT:
                // TODO: Spieler auf ein zufälliges anderes Feld beamen
                break;
            case EVENT:
                // TODO: zufälliges Event auslösen
                break;
            case START:
            default:
                // nichts
                break;
        }
    }

    /** Zum nächsten Spieler wechseln; falls wieder Spieler 0 -> neue Runde. */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (currentPlayerIndex == 0) {
            roundCount++;
        }
    }

    /** Wendet das Minispiel-Ergebnis an: jeder Spieler bekommt seine Belohnung. */
    public void applyMiniGameResult(MiniGameResult result) {
        for (Player p : players) {
            p.modifyCoins(result.getReward(p));
        }
    }

    /**
     * Liefert ein zufälliges Minispiel-Objekt. Später erweiterbar.
     */
    public MiniGame pickRandomMiniGame() {
        return random.nextBoolean() ? new TicTacToe() : new Memory();
    }

    public boolean isGameOver() {
        return roundCount > MAX_ROUNDS;
    }

    /** Sieger = meiste Sterne, bei Gleichstand die meisten Münzen. */
    public Player getWinner() {
        return players.stream()
                .max(Comparator.comparingInt(Player::getStars)
                        .thenComparingInt(Player::getCoins))
                .orElse(null);
    }

    // --- Getter ---
    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }
    public List<Player> getPlayers() { return players; }
    public BoardGraph getBoard() { return board; }
    public int getRoundCount() { return roundCount; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
}
