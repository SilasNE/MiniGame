package com.example.marioparty.ui.board;

import com.example.marioparty.model.Field;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

/**
 * Darstellung eines Graphen-Knotens als {@link Group}.
 */
public class BoardKnotView extends Group {

    private final int knotId;
    private final Circle circle;

    public BoardKnotView(int knotId, double cx, double cy, double radius) {
        this.knotId = knotId;
        this.circle = new Circle(cx, cy, radius);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        circle.setStrokeType(StrokeType.INSIDE);
        getChildren().add(circle);
    }

    public int getKnotId() {
        return knotId;
    }

    public void setFill(Color color) {
        circle.setFill(color);
    }

    public void applyFieldTypeColor(Field.Type type, boolean starHere) {
        setFill(starHere ? Color.GOLD : baseColor(type));
    }

    private static Color baseColor(Field.Type t) {
        return switch (t) {
            case BLUE -> Color.DODGERBLUE;
            case RED -> Color.CRIMSON;
            case STAR -> Color.GOLD;
            case EVENT -> Color.MEDIUMPURPLE;
            case START -> Color.WHITE;
            case ITEM_SHOP -> Color.rgb(180, 120, 40);
        };
    }
}
