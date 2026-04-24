package minigame;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Player;

import java.util.List;

/**
 * Einfaches TicTacToe als Minispiel.
 * Zunächst nur ein Gerüst – die eigentliche Spiellogik (Gewinnprüfung,
 * Spielerwechsel, Ergebnis-Eintrag in MiniGameResult) muss noch ergänzt werden.
 */
public class TicTacToe implements MiniGame {

    private List<Player> players;
    private MiniGameResult result;
    private char[][] field;       // 3x3 Spielfeld
    private int currentIdx;

    @Override
    public void initialize(List<Player> players) {
        this.players = players;
        this.result = new MiniGameResult();
        this.field = new char[3][3];
        this.currentIdx = 0;
    }

    @Override
    public void start(Pane container) {
        container.getChildren().clear();

        VBox root = new VBox(10);
        Label info = new Label("TicTacToe – " + players.get(currentIdx).getName() + " ist dran.");
        GridPane grid = new GridPane();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Button b = new Button(" ");
                b.setPrefSize(80, 80);
                final int row = r, col = c;
                b.setOnAction(e -> {
                    // TODO: Zug ausführen, Gewinner prüfen, ggf. Ergebnis setzen
                    b.setText(currentIdx == 0 ? "X" : "O");
                    b.setDisable(true);
                    field[row][col] = b.getText().charAt(0);
                    currentIdx = (currentIdx + 1) % players.size();
                    info.setText(players.get(currentIdx).getName() + " ist dran.");
                });
                grid.add(b, c, r);
            }
        }

        root.getChildren().addAll(info, grid);
        container.getChildren().add(root);
    }

    @Override
    public MiniGameResult getResults() {
        return result;
    }
}
