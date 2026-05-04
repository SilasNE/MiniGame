package game.model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Globaler Spielzustand: Spielerliste, aktuelles Brett, Rundenzähler.
 * Wird einmal in der Engine erzeugt und von allen Szenen geteilt.
 */
public class GameState {

    private final List<Player> players = new ArrayList<>();
    private final Board board = new Board();
    private int currentPlayerIndex = 0;
    private int round = 1;
    private final int totalRounds = 5;

    public GameState() {
        players.add(new Player("Mario",  Color.RED));
        players.add(new Player("Luigi",  Color.LIMEGREEN));
        players.add(new Player("Peach",  Color.HOTPINK));
        players.add(new Player("Bowser", Color.ORANGE));
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (currentPlayerIndex == 0) round++;
    }

    public boolean isGameOver() {
        return round > totalRounds;
    }

    /** Sieger = meiste Sterne, bei Gleichstand meiste Münzen. */
    public Player getWinner() {
        return players.stream()
                .max(Comparator.<Player>comparingInt(Player::getStars)
                        .thenComparingInt(Player::getCoins))
                .orElse(players.get(0));
    }

    public List<Player> getPlayers()    { return players; }
    public Player getCurrentPlayer()    { return players.get(currentPlayerIndex); }
    public int getCurrentPlayerIndex()  { return currentPlayerIndex; }
    public Board getBoard()             { return board; }
    public int getRound()               { return round; }
    public int getTotalRounds()         { return totalRounds; }
}
