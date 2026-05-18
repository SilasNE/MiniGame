package com.example.marioparty.ui.board;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;

import java.util.function.IntConsumer;

/**
 * Zwei klickbare Pfeile an einer Abzweigung — jeweils eine ausgehende Kante / Ziel-Knoten.
 */
public class ForkArrowChoice extends Group {

    public ForkArrowChoice(
            double fromX,
            double fromY,
            double to1x,
            double to1y,
            int targetKnotId1,
            double to2x,
            double to2y,
            int targetKnotId2,
            IntConsumer onTargetKnotChosen) {

        Polygon arrow1 = buildArrow(fromX, fromY, to1x, to1y, Color.rgb(0, 200, 255, 0.85));
        Polygon arrow2 = buildArrow(fromX, fromY, to2x, to2y, Color.rgb(255, 120, 200, 0.85));

        wireClick(arrow1, targetKnotId1, onTargetKnotChosen);
        wireClick(arrow2, targetKnotId2, onTargetKnotChosen);

        getChildren().addAll(arrow1, arrow2);
    }

    private static void wireClick(Polygon poly, int targetKnotId, IntConsumer onTargetKnotChosen) {
        poly.setCursor(Cursor.HAND);
        poly.setOnMouseClicked(e -> onTargetKnotChosen.accept(targetKnotId));
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

        Polygon p = new Polygon(
                tipX, tipY,
                base1X, base1Y,
                base2X, base2Y
        );
        p.setFill(fill);
        p.setStroke(Color.BLACK);
        p.setStrokeWidth(2);
        p.setStrokeLineJoin(StrokeLineJoin.ROUND);
        return p;
    }
}
