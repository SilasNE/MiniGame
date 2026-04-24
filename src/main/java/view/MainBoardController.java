package view;

import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import logic.GameEngine;
import minigame.MiniGame;
import minigame.MiniGameResult;
import model.FieldNode;
import model.FieldType;
import model.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Steuert die Haupt-Spielbrett-Ansicht:
 *   - zeichnet das Brett
 *   - reagiert auf den "Würfeln"-Button
 *   - animiert die Spielfigur entlang des gewählten Pfades
 *   - zeigt nach dem Zug den Feldeffekt und ggf. ein Minispiel
 */
public class MainBoardController {

    private GameEngine engine;

    @FXML private HUDController hudController;   // wird vom FXMLLoader injiziert (fx:id="hud")
    @FXML private Pane boardView;                // Brett-Zeichenfläche
    @FXML private Label diceLabel;               // zeigt letztes Würfelergebnis
    @FXML private Button rollButton;             // der Würfel-Knopf

    /** Bildliche Darstellung jedes Spielers (Kreis o. Ä.) für die Animation. */
    private final Map<Player, Circle> playerTokens = new HashMap<>();

    /**
     * Wird vom Main-Einstieg aufgerufen, nachdem der Controller per FXMLLoader
     * geladen wurde. Übergibt die Engine und baut das Brett auf.
     */
    public void setEngine(GameEngine engine) {
        this.engine = engine;
        drawBoard();
        placePlayers();
        updateUI();
    }

    /** Zeichnet Felder + Verbindungen anhand des BoardGraph. */
    private void drawBoard() {
        boardView.getChildren().clear();

        // Kanten (Linien zwischen Nachbarn)
        for (FieldNode node : engine.getBoard().getNodes().values()) {
            for (FieldNode nb : node.getNeighbors()) {
                if (node.getId() < nb.getId()) { // jede Kante nur einmal
                    Line l = new Line(node.getxPos(), node.getyPos(),
                                      nb.getxPos(),  nb.getyPos());
                    l.setStroke(Color.GRAY);
                    l.setStrokeWidth(3);
                    boardView.getChildren().add(l);
                }
            }
        }
        // Felder als Kreise
        for (FieldNode node : engine.getBoard().getNodes().values()) {
            Circle c = new Circle(node.getxPos(), node.getyPos(), 18);
            c.setFill(colorForType(node.getType()));
            c.setStroke(Color.BLACK);
            boardView.getChildren().add(c);
        }
    }

    private Color colorForType(FieldType type) {
        switch (type) {
            case START:       return Color.WHITE;
            case COIN_PLUS:   return Color.LIGHTGREEN;
            case COIN_MINUS:  return Color.LIGHTCORAL;
            case MINIGAME:    return Color.ORANGE;
            case STAR_SHOP:   return Color.GOLD;
            case TELEPORT:    return Color.LIGHTBLUE;
            case EVENT:       return Color.PLUM;
            default:          return Color.LIGHTGRAY;
        }
    }

    /** Legt für jeden Spieler einen farbigen Token auf das Startfeld. */
    private void placePlayers() {
        Color[] palette = {Color.RED, Color.BLUE, Color.DARKGREEN, Color.PURPLE,
                           Color.DARKORANGE, Color.DEEPPINK};
        int i = 0;
        for (Player p : engine.getPlayers()) {
            Circle token = new Circle(10, palette[i % palette.length]);
            token.setStroke(Color.BLACK);
            token.setCenterX(p.getCurrentPos().getxPos() + (i - 1) * 6);
            token.setCenterY(p.getCurrentPos().getyPos() - 8);
            boardView.getChildren().add(token);
            playerTokens.put(p, token);
            i++;
        }
    }

    /** An den "Würfeln"-Button gebunden. */
    @FXML
    public void onRollDice() {
        if (engine.isGameOver()) return;
        rollButton.setDisable(true);

        Player current = engine.getCurrentPlayer();
        int steps = engine.handleDiceRoll();
        if (diceLabel != null) diceLabel.setText("🎲 " + steps);

        List<List<FieldNode>> options = engine.getBoard()
                .findPath(current.getCurrentPos(), steps);

        if (options.isEmpty()) {
            finishTurn(current);
            return;
        }

        List<FieldNode> chosen = (options.size() == 1)
                ? options.get(0)
                : showPathSelectionUI(options);

        animatePlayerMove(chosen, () -> {
            // Spielerposition aktualisieren (letztes Feld des Pfades)
            FieldNode target = chosen.get(chosen.size() - 1);
            current.setCurrentPos(target);
            engine.executeFieldEffect(current);

            // Minispiel ggf. starten
            if (target.getType() == FieldType.MINIGAME) {
                launchMiniGame();
            } else {
                finishTurn(current);
            }
        });
    }

    /** Animiert einen Kreis entlang der gegebenen Feldliste. */
    public void animatePlayerMove(List<FieldNode> path, Runnable onFinished) {
        Player current = engine.getCurrentPlayer();
        Circle token = playerTokens.get(current);

        SequentialTransition seq = new SequentialTransition();
        for (int i = 0; i < path.size() - 1; i++) {
            FieldNode a = path.get(i);
            FieldNode b = path.get(i + 1);
            Path p = new Path();
            p.getElements().add(new MoveTo(a.getxPos(), a.getyPos()));
            p.getElements().add(new LineTo(b.getxPos(), b.getyPos()));
            PathTransition pt = new PathTransition(Duration.millis(350), p, token);
            seq.getChildren().add(pt);
        }
        seq.setOnFinished(e -> onFinished.run());
        seq.play();
    }

    /** Öffnet einen Auswahl-Dialog, wenn mehrere Pfade möglich sind. */
    private List<FieldNode> showPathSelectionUI(List<List<FieldNode>> options) {
        // Einfache Variante über ChoiceDialog – kann später durch
        // klickbare Pfeile auf dem Brett ersetzt werden.
        Map<String, List<FieldNode>> map = new HashMap<>();
        for (int i = 0; i < options.size(); i++) {
            List<FieldNode> path = options.get(i);
            FieldNode target = path.get(path.size() - 1);
            String key = "Pfad " + (i + 1) + " -> " + target.toString();
            map.put(key, path);
        }
        ChoiceDialog<String> dlg = new ChoiceDialog<>(map.keySet().iterator().next(), map.keySet());
        dlg.setTitle("Pfad wählen");
        dlg.setHeaderText("An welcher Abzweigung möchtest du weiter?");
        Optional<String> res = dlg.showAndWait();
        return map.get(res.orElse(map.keySet().iterator().next()));
    }

    private void launchMiniGame() {
        MiniGame game = engine.pickRandomMiniGame();
        game.initialize(engine.getPlayers());

        // Minispiel in ein Overlay-Pane rendern
        StackPane overlay = new StackPane();
        overlay.setPrefSize(boardView.getWidth(), boardView.getHeight());
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
        Pane gameContainer = new Pane();
        overlay.getChildren().add(gameContainer);
        ((Pane) boardView.getParent()).getChildren().add(overlay);

        game.start(gameContainer);

        // TODO: Sobald das Minispiel zu Ende ist (Callback einbauen),
        //       diese Zeilen aufrufen:
        //
        //   MiniGameResult r = game.getResults();
        //   engine.applyMiniGameResult(r);
        //   ((Pane) boardView.getParent()).getChildren().remove(overlay);
        //   finishTurn(engine.getCurrentPlayer());
    }

    private void finishTurn(Player current) {
        updateUI();
        engine.nextTurn();

        if (engine.isGameOver()) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Spielende");
            a.setHeaderText("Gewinner: " + engine.getWinner().getName());
            a.showAndWait();
            rollButton.setDisable(true);
        } else {
            rollButton.setDisable(false);
        }
    }

    /** HUD und eventuell zusätzliche UI-Elemente aktualisieren. */
    public void updateUI() {
        if (hudController != null) {
            hudController.updateStats(engine.getPlayers());
        }
    }
}
