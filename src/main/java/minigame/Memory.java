package minigame;

import javafx.scene.layout.Pane;
import model.Player;

import java.util.List;

/**
 * Memory-Minispiel.
 * Gerüst: Kartenpaare aufdecken, wer die meisten Paare findet gewinnt.
 * Noch zu implementieren: Karten-Layout, Aufdeck-Logik, Zugwechsel, Ergebnis.
 */
public class Memory implements MiniGame {

    private List<Player> players;
    private MiniGameResult result;

    @Override
    public void initialize(List<Player> players) {
        this.players = players;
        this.result = new MiniGameResult();
        // TODO: Karten-Array erzeugen und mischen
    }

    @Override
    public void start(Pane container) {
        container.getChildren().clear();
        // TODO: Karten-Grid in 'container' aufbauen
    }

    @Override
    public MiniGameResult getResults() {
        return result;
    }
}
