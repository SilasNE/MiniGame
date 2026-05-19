package com.example.marioparty.minigames;

import com.example.marioparty.Main;
import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Player;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtonMashGame extends MiniGame {

    private static final double DURATION = 5.0;
    private static final KeyCode[] KEYS = { KeyCode.A, KeyCode.L, KeyCode.G, KeyCode.UP };
    private static final double[] CPU_MASH_RATES = { 6.5, 7.2, 7.9, 8.5 };

    private final List<Player> players;
    private final Map<Player, Integer> counts = new HashMap<>();
    private final Map<Player, KeyCode> keys = new HashMap<>();
    private final Map<Player, Double> cpuMashAccumulator = new HashMap<>();
    private final Map<Player, Double> cpuMashRate = new HashMap<>();
    private double timeLeft = DURATION;

    private Text timerText;
    private final Map<Player, Rectangle> bars = new HashMap<>();
    private final Map<Player, Text> countTexts = new HashMap<>();

    public ButtonMashGame(List<Player> players, Pane pane) {
        super(pane);
        this.players = players;
        this.participants = List.copyOf(players);
        int cpuSlot = 0;
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            counts.put(p, 0);
            keys.put(p, KEYS[i % KEYS.length]);
            cpuMashAccumulator.put(p, 0.0);
            if (!p.isHuman()) {
                int idx = Math.min(cpuSlot, CPU_MASH_RATES.length - 1);
                cpuMashRate.put(p, CPU_MASH_RATES[idx]);
                cpuSlot++;
            }
        }
    }

    @Override
    public String getName() { return "Button Masher"; }

    @Override
    public String getDescription() {
        return "Drücke deine Taste so oft wie möglich in 5 Sekunden! (CPU masht automatisch.)";
    }

    @Override
    protected void onStart() {
        for (Player p : players) {
            cpuMashAccumulator.put(p, 0.0);
        }
        timerText = new Text(Main.WIDTH / 2.0 - 80, 100, "5.0 s");
        timerText.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        timerText.setFill(Color.web("#ffd60a"));
        pane.getChildren().add(timerText);

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            double y = 180 + i * 110;

            Rectangle marker = new Rectangle(80, y, 60, 60);
            marker.setFill(p.getColor());
            marker.setStroke(Color.WHITE);
            marker.setStrokeWidth(3);

            String tag = p.isHuman() ? "Du" : "CPU";
            Text label = new Text(160, y + 25, p.getName() + " (" + tag + ")   [" + keys.get(p) + "]");
            label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            label.setFill(Color.WHITE);

            Rectangle barBg = new Rectangle(160, y + 35, 700, 22);
            barBg.setFill(Color.TRANSPARENT);
            barBg.setStroke(Color.web("#ffd60a"));
            barBg.setStrokeWidth(2);

            Rectangle bar = new Rectangle(160, y + 35, 0, 22);
            bar.setFill(p.getColor());
            bars.put(p, bar);

            Text countText = new Text(880, y + 52, "0");
            countText.setFont(Font.font("Arial", 18));
            countText.setFill(Color.WHITE);
            countTexts.put(p, countText);

            pane.getChildren().addAll(marker, label, barBg, bar, countText);
        }
    }

    private void registerPress(Player p) {
        counts.merge(p, 1, Integer::sum);
        int count = counts.get(p);
        bars.get(p).setWidth(Math.min(count * 8, 700));
        countTexts.get(p).setText(String.valueOf(count));
    }

    @Override
    public void update(double deltatime, InputHandler input) {
        timeLeft -= deltatime;
        for (Player p : players) {
            if (p.isHuman()) {
                if (input.wasJustPressed(keys.get(p))) {
                    registerPress(p);
                }
            } else {
                double rate = cpuMashRate.getOrDefault(p, 7.0);
                double acc = cpuMashAccumulator.get(p) + deltatime * rate;
                while (acc >= 1.0 && timeLeft > 0) {
                    registerPress(p);
                    acc -= 1.0;
                }
                cpuMashAccumulator.put(p, acc);
            }
        }
        timerText.setText(String.format("%.1f s", Math.max(0, timeLeft)));

        if (timeLeft <= 0) {
            finished = true;
            winner = players.stream()
                    .max((a, b) -> Integer.compare(counts.get(a), counts.get(b)))
                    .orElse(players.get(0));
        }
    }
}
