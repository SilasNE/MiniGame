package com.example.marioparty.ui.board;

import com.example.marioparty.model.Field;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class BoardKnotView extends Group {

    private final Circle circle;

    public BoardKnotView(double cx, double cy, double radius) {
        this.circle = new Circle(cx, cy, radius);
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(4);
        circle.setStrokeType(StrokeType.INSIDE);
        getChildren().add(circle);
    }

    public void setFill(Color color) {
        circle.setFill(color);
    }

    public void applyFieldTypeColor(Field.Type type, boolean starHere) {
        setFill(starHere ? Color.GOLD : baseColor(type));
    }

    private static Color baseColor(Field.Type t) {
        return switch (t) {
            case BLUE -> Color.web("#1e9bff");
            case RED -> Color.web("#ff4545");
            case STAR -> Color.web("#ffd60a");
            case NEUTRAL -> Color.web("#8fa8c4");
            case START -> Color.web("#fff6a8");
            case ITEM_SHOP -> Color.web("#ff9f1c");
        };
    }
}
