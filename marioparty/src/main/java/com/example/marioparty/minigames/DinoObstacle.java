package com.example.marioparty.minigames;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DinoObstacle {

    private double x = 1280;
    private final double width, height, yOffset;
    private final Rectangle rect1;
    private final Rectangle rect2;
    private final Pane pane;

    public DinoObstacle(double width, double height, double yOffset, boolean isFlying, Pane pane) {
        this.width = width;
        this.height = height;
        this.yOffset = yOffset;
        this.pane = pane;

        Color color = isFlying ? Color.ORANGE : Color.RED;

        rect1 = new Rectangle(x, 300 - height - yOffset, width, height);
        rect1.setFill(color);

        rect2 = new Rectangle(x, 600 - height - yOffset, width, height);
        rect2.setFill(color);

        pane.getChildren().addAll(rect1, rect2);
    }

    public void update(double dt, double currentSpeed) {
        x -= currentSpeed * dt;
        rect1.setX(x);
        rect2.setX(x);
    }

    public boolean isOffScreen() { return x + width < 0; }

    public void removeFromPane() { pane.getChildren().removeAll(rect1, rect2); }

    public double getX() { return x; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getYOffset() { return yOffset; }
}