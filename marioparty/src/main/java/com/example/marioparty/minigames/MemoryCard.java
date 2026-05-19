package com.example.marioparty.minigames;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public class MemoryCard {

    private final String symbol;
    private boolean isFlipped = false;
    private boolean isMatched = false;

    private StackPane stack;
    private Rectangle rect;
    private Text text;
    private final Consumer<MemoryCard> onClick;

    public MemoryCard(String symbol, Consumer<MemoryCard> onClick) {
        this.symbol = symbol;
        this.onClick = onClick;
    }

    public String getSymbol() { return symbol; }
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
        rect.setFill(Color.BLUE);
        rect.setStroke(Color.WHITE);
        rect.setStrokeWidth(2);

        text = new Text(symbol);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        text.setFill(Color.BLACK);
        text.setVisible(false);

        stack.getChildren().addAll(rect, text);
        stack.setOnMouseClicked(event -> onClick.accept(this));

        return stack;
    }

    public void flip() {
        isFlipped = true;
        rect.setFill(Color.WHITE);
        text.setVisible(true);
    }

    public void unflip() {
        isFlipped = false;
        rect.setFill(Color.BLUE);
        text.setVisible(false);
    }
}