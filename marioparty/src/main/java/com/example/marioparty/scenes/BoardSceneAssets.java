package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.model.Player;
import com.example.marioparty.model.items.CoinBlockItem;
import com.example.marioparty.model.items.GameItem;
import com.example.marioparty.model.items.TripleMushroomItem;
import com.example.marioparty.model.items.WarpPipeItem;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BoardSceneAssets {

    public static void addBoardBackdrop(Pane pane) {
        Ellipse mainIsland = new Ellipse(Main.WIDTH / 2.0 + 40, 400, 455, 250);
        mainIsland.setFill(Color.web("#63c65f"));
        mainIsland.setStroke(Color.web("#fff6a8"));
        mainIsland.setStrokeWidth(5);

        Ellipse upperIsland = new Ellipse(560, 210, 250, 95);
        upperIsland.setFill(Color.web("#7bd86f"));
        upperIsland.setStroke(Color.rgb(255, 255, 255, 0.65));
        upperIsland.setStrokeWidth(4);

        Ellipse lowerIsland = new Ellipse(500, 535, 350, 105);
        lowerIsland.setFill(Color.web("#58bb59"));
        lowerIsland.setStroke(Color.rgb(255, 255, 255, 0.5));
        lowerIsland.setStrokeWidth(4);

        Ellipse leftCloud = new Ellipse(120, 610, 120, 55);
        leftCloud.setFill(Color.rgb(255, 255, 255, 0.35));
        Ellipse rightCloud = new Ellipse(890, 610, 150, 60);
        rightCloud.setFill(Color.rgb(255, 255, 255, 0.35));

        pane.getChildren().addAll(leftCloud, rightCloud, mainIsland, lowerIsland, upperIsland);
    }

    public static void styleOverlayButton(Button button) {
        button.setFont(Font.font("Arial", FontWeight.BOLD, 15));
    }

    public static void styleTestModeButton(Button button) {
        button.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        button.setPrefWidth(150);
        button.setPrefHeight(28);
    }

    public static DropShadow createSpriteOutline() {
        DropShadow outline = new DropShadow();
        outline.setBlurType(BlurType.GAUSSIAN);
        outline.setColor(Color.BLACK);
        outline.setRadius(4);
        outline.setSpread(0.82);
        outline.setOffsetX(0);
        outline.setOffsetY(0);
        return outline;
    }

    public static Image loadDiceImage(int value) {
        return new Image(BoardScene.class.getResourceAsStream("/images/dice" + value + ".png"));
    }

    public static Image loadPlayerImage(Player player) {
        String path = switch (player.getName()) {
            case "Mario" -> "/images/Mario.png";
            case "Luigi" -> "/images/Luigi.png";
            case "Wario" -> "/images/Wario.png";
            case "Donkey Kong" -> "/images/DonkeyKong.png";
            default -> "/images/Mario.png";
        };
        return new Image(BoardScene.class.getResourceAsStream(path));
    }

    public static Image loadHudImage(Player player) {
        String path = switch (player.getName()) {
            case "Mario" -> "/images/roteHUD.png";
            case "Luigi" -> "/images/grueneHUD.png";
            case "Wario" -> "/images/gelbeHUD.png";
            case "Donkey Kong" -> "/images/blaueHUD.png";
            default -> "/images/roteHUD.png";
        };
        return new Image(BoardScene.class.getResourceAsStream(path));
    }

    public static ImageView createItemIcon(GameItem item) {
        String path = switch (item.getId()) {
            case WarpPipeItem.ID -> "/images/roehre.png";
            case TripleMushroomItem.ID -> "/images/pilz.png";
            case CoinBlockItem.ID -> "/images/muenzblock.png";
            default -> null;
        };
        if (path == null) {
            return null;
        }
        ImageView icon = new ImageView(new Image(BoardScene.class.getResourceAsStream(path)));
        icon.setFitWidth(44);
        icon.setFitHeight(44);
        icon.setPreserveRatio(true);
        icon.setSmooth(false);
        icon.setEffect(createSpriteOutline());
        return icon;
    }
}
