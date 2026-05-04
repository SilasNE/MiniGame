package game.minigames;

import game.Main;
import game.engine.InputHandler;
import game.model.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klassisches "Button-Masher": 5 Sekunden Zeit, jeder Spieler hämmert
 * seine eigene Taste. Wer am häufigsten gedrückt hat, gewinnt.
 *
 * Tastenbelegung: Mario=A, Luigi=L, Peach=G, Bowser=Pfeil-Hoch.
 */
public class ButtonMashGame extends MiniGame {

    private static final double DURATION = 5.0;
    private static final KeyCode[] KEYS = { KeyCode.A, KeyCode.L, KeyCode.G, KeyCode.UP };

    private final List<Player> players;
    private final Map<Player, Integer> counts = new HashMap<>();
    private final Map<Player, KeyCode> keys = new HashMap<>();
    private double timeLeft = DURATION;

    public ButtonMashGame(List<Player> players) {
        this.players = players;
        for (int i = 0; i < players.size(); i++) {
            counts.put(players.get(i), 0);
            keys.put(players.get(i), KEYS[i % KEYS.length]);
        }
    }

    @Override
    public String getName() { return "Button Masher"; }

    @Override
    public String getDescription() {
        return "Drücke deine Taste so oft wie möglich in 5 Sekunden!";
    }

    @Override
    public void update(double dt, InputHandler input) {
        timeLeft -= dt;
        for (Player p : players) {
            if (input.wasJustPressed(keys.get(p))) {
                counts.merge(p, 1, Integer::sum);
            }
        }
        if (timeLeft <= 0) {
            timeLeft = 0;
            finished = true;
            winner = players.stream()
                    .max((a, b) -> Integer.compare(counts.get(a), counts.get(b)))
                    .orElse(players.get(0));
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Timer
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        gc.fillText(String.format("%.1f s", timeLeft),
                Main.WIDTH / 2.0 - 80, 100);

        // Spieler-Balken
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            double y = 180 + i * 110;

            // Spielermarker
            gc.setFill(p.getColor());
            gc.fillRect(80, y, 60, 60);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeRect(80, y, 60, 60);

            // Name + Taste
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            gc.fillText(p.getName() + "   [Taste: " + keys.get(p) + "]",
                    160, y + 25);

            // Fortschrittsbalken
            int count = counts.get(p);
            double barWidth = Math.min(count * 8, 700);
            gc.setFill(p.getColor());
            gc.fillRect(160, y + 35, barWidth, 22);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.strokeRect(160, y + 35, 700, 22);

            // Zähler
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 18));
            gc.fillText(String.valueOf(count), 880, y + 52);
        }
    }
}
