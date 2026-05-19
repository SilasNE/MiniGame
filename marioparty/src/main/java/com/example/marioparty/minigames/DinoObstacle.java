package com.example.marioparty.minigames;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class DinoObstacle {

    private double x = 1280;
    private final double width, height, yOffset;
    private ImageView img1;
    private ImageView img2;
    private final Pane pane;

    public DinoObstacle(double width, double height, double yOffset, boolean isFlying, Pane pane) {
        this.width = width;
        this.height = height;
        this.yOffset = yOffset;
        this.pane = pane;

        String imagePath;
            if(isFlying){
                imagePath = "/images/cheepcheep.png";
            }else{
                imagePath = "/images/goomba.png";
            }

        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            img1 = new ImageView(img);
            img2 = new ImageView(img);
        } catch (Exception e) {
            img1 = new ImageView();
            img2 = new ImageView();
        }

        img1.setFitWidth(width);
        img1.setFitHeight(height);
        img1.setX(x);
        img1.setY(300 - height - yOffset);

        img2.setFitWidth(width);
        img2.setFitHeight(height);
        img2.setX(x);
        img2.setY(600 - height - yOffset);

        pane.getChildren().addAll(img1, img2);
    }

    public void update(double deltatime, double currentSpeed) {
        x -= currentSpeed * deltatime;
        img1.setX(x);
        img2.setX(x);
    }

    public boolean isOffScreen() { return x + width < 0; }

    public void removeFromPane() { pane.getChildren().removeAll(img1, img2); }

    public double getX() { return x; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getYOffset() { return yOffset; }
}