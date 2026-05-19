package com.example.marioparty.ui.board;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;

public class Split extends Group {

    public Split(
            double fromX,
            double fromY,
            double to1x,
            double to1y,
            Runnable onFirstPathChosen,
            double to2x,
            double to2y,
            Runnable onSecondPathChosen) {

        Polygon arrow1 = buildArrow(fromX, fromY, to1x, to1y, Color.rgb(0, 200, 255, 0.85));
        Polygon arrow2 = buildArrow(fromX, fromY, to2x, to2y, Color.rgb(255, 120, 200, 0.85));

        arrow1.setCursor(Cursor.HAND);
        arrow1.setOnMouseClicked(event -> onFirstPathChosen.run());
        arrow2.setCursor(Cursor.HAND);
        arrow2.setOnMouseClicked(event -> onSecondPathChosen.run());

        getChildren().addAll(arrow1, arrow2);
    }

    private static Polygon buildArrow(double fromX, double fromY, double toX, double toY, Color fill) {
        Point2D dir = new Point2D(toX - fromX, toY - fromY).normalize();
        double len = new Point2D(toX - fromX, toY - fromY).magnitude();
        double shaft = Math.min(len * 0.55, 120);
        double tipX = fromX + dir.getX() * shaft;
        double tipY = fromY + dir.getY() * shaft;

        Point2D perp = new Point2D(-dir.getY(), dir.getX());
        double w = 18;
        double base1X = fromX + dir.getX() * (shaft * 0.55) + perp.getX() * w;
        double base1Y = fromY + dir.getY() * (shaft * 0.55) + perp.getY() * w;
        double base2X = fromX + dir.getX() * (shaft * 0.55) - perp.getX() * w;
        double base2Y = fromY + dir.getY() * (shaft * 0.55) - perp.getY() * w;

        Polygon arrowPolygon = new Polygon(
                tipX, tipY,
                base1X, base1Y,
                base2X, base2Y
        );
        arrowPolygon.setFill(fill);
        arrowPolygon.setStroke(Color.BLACK);
        arrowPolygon.setStrokeWidth(2);
        arrowPolygon.setStrokeLineJoin(StrokeLineJoin.ROUND);
        return arrowPolygon;
    }
}
