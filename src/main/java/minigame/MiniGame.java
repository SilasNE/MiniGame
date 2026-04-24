package minigame;

import javafx.scene.layout.Pane;
import model.Player;

import java.util.List;

/**
 * Gemeinsame Schnittstelle für alle Minispiele.
 * Ablauf aus Sicht der GameEngine:
 *   1. initialize(players)
 *   2. start(container)     -> UI wird in den Container gerendert
 *   3. getResults()         -> nach Ende aufrufen, Belohnungen verteilen
 */
public interface MiniGame {

    /** Minispiel mit den teilnehmenden Spielern vorbereiten. */
    void initialize(List<Player> players);

    /** UI in das gegebene Pane einsetzen und das Spiel starten. */
    void start(Pane container);

    /** Ergebnis inkl. Rangliste für die Belohnungsverteilung. */
    MiniGameResult getResults();
}
