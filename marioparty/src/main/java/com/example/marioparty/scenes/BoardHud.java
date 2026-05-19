package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.model.GameState;
import com.example.marioparty.model.Player;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

class BoardHud {

    private final Rectangle[] highlights;
    private final Text[] stats;

    BoardHud(Pane pane, List<Player> players) {
        highlights = new Rectangle[players.size()];
        stats = new Text[players.size()];

        double boxW = 232;
        double boxH = 58;
        double gap = 10;
        double totalHudWidth = players.size() * boxW + Math.max(0, players.size() - 1) * gap;
        double x0 = (Main.WIDTH - totalHudWidth) / 2.0;
        double hudY = 10;

        for (int i = 0; i < players.size(); i++) {
            double x = x0 + i * (boxW + gap);
            Player hudPlayer = players.get(i);

            ImageView frame = new ImageView(BoardSceneAssets.loadHudImage(hudPlayer));
            frame.setFitWidth(boxW);
            frame.setFitHeight(boxH);
            frame.setPreserveRatio(false);
            frame.setSmooth(false);
            frame.setLayoutX(x);
            frame.setLayoutY(hudY);

            Rectangle highlight = new Rectangle(x + 2, hudY + 2, boxW - 8, boxH - 6);
            highlight.setFill(Color.TRANSPARENT);
            highlight.setStroke(Color.TRANSPARENT);
            highlight.setStrokeWidth(4);
            highlight.setArcWidth(12);
            highlight.setArcHeight(12);
            highlights[i] = highlight;

            ImageView portrait = new ImageView(BoardSceneAssets.loadPlayerImage(hudPlayer));
            portrait.setFitWidth(44);
            portrait.setFitHeight(44);
            portrait.setPreserveRatio(true);
            portrait.setSmooth(false);
            portrait.setEffect(BoardSceneAssets.createSpriteOutline());
            portrait.setLayoutX(x + 8);
            portrait.setLayoutY(hudY + 7);

            Text name = new Text(x + 58, hudY + 20, hudPlayer.getName() + (hudPlayer.isHuman() ? " (Du)" : " (CPU)"));
            name.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            name.setFill(Color.WHITE);
            name.setStroke(Color.BLACK);
            name.setStrokeWidth(0.45);

            Text statsText = new Text(x + 58, hudY + 38, "");
            statsText.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            statsText.setFill(Color.WHITE);
            statsText.setStroke(Color.BLACK);
            statsText.setStrokeWidth(0.35);
            statsText.setWrappingWidth(boxW - 68);
            stats[i] = statsText;

            pane.getChildren().addAll(frame, highlight, portrait, name, statsText);
        }
    }

    void update(GameState state) {
        List<Player> players = state.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            boolean active = (i == state.getCurrentPlayerIndex());
            highlights[i].setStroke(active ? Color.YELLOW : Color.TRANSPARENT);
            int inventoryCount = player.getInventory().size();
            stats[i].setText("★ " + player.getStars() + "   Münzen " + player.getCoins() + "\nItems " + inventoryCount);
        }
    }
}
