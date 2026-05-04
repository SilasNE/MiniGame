package view;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Player;

import java.util.List;

/**
 * Zeigt am Bildschirmrand die Statistiken aller Spieler (Münzen, Sterne)
 * und blendet kurz Ereignismeldungen ein.
 */
public class HUDController {

    @FXML private VBox playerStatsContainer;
    @FXML private Label eventMessageLabel;

    @FXML
    public void initialize() {
        if (eventMessageLabel != null) {
            eventMessageLabel.setVisible(false);
        }
    }

    /** Aktualisiert die angezeigten Stats aller Spieler. */
    public void updateStats(List<Player> players) {
        if (playerStatsContainer == null) return;
        playerStatsContainer.getChildren().clear();
        for (Player p : players) {
            Label lbl = new Label(p.getName()
                    + "   Münzen: " + p.getCoins()
                    + "   Sterne: " + p.getStars());
            lbl.getStyleClass().add("player-stat");
            playerStatsContainer.getChildren().add(lbl);
        }
    }

    /** Blendet eine Nachricht ein, die nach 2.5 s wieder verschwindet. */
    public void showEventMessage(String msg) {
        if (eventMessageLabel == null) {
            System.out.println("[EVENT] " + msg);
            return;
        }
        eventMessageLabel.setText(msg);
        eventMessageLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(e -> eventMessageLabel.setVisible(false));
        pause.play();
    }
}
