package com.example.marioparty.minigames;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.function.Consumer;

public class MemoryCard {

    private final String imagePath;
    private boolean isFlipped = false;
    private boolean isMatched = false;

    private StackPane stack;
    private Rectangle rect;
    private ImageView imageView;
    private final Consumer<MemoryCard> onClick;

    public MemoryCard(String imagePath, Consumer<MemoryCard> onClick) {
        this.imagePath = imagePath;
        this.onClick = onClick;
    }

    public String getImagePath() { return imagePath; }
    public boolean isFlipped() { return isFlipped; }
    public boolean isMatched() { return isMatched; }

    public void setSuccessColor() {
        rect.setFill(Color.LIGHTGREEN);
    }

    public void setMatched(boolean matched) {
        this.isMatched = matched;
        if (matched && stack != null) {
            stack.setVisible(false);
        }
    }

    public StackPane createUI(double size) {
        stack = new StackPane();
        rect = new Rectangle(size, size);
        rect.setFill(Color.web("#1e9bff"));
        rect.setStroke(Color.WHITE);
        rect.setStrokeWidth(3);

        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            imageView = new ImageView(img);
            imageView.setFitWidth(size * 0.8);
            imageView.setFitHeight(size * 0.8);
            imageView.setPreserveRatio(true);
            imageView.setVisible(false);
        } catch (Exception e) {
            imageView = new ImageView();
        }

        stack.getChildren().addAll(rect, imageView);
        stack.setOnMouseClicked(event -> onClick.accept(this));

        return stack;
    }

    public void flip() {
        isFlipped = true;
        rect.setFill(Color.WHITE);
        if (imageView != null) imageView.setVisible(true);
    }

    public void unflip() {
        isFlipped = false;
        rect.setFill(Color.web("#1e9bff"));
        if (imageView != null) imageView.setVisible(false);
    }
}