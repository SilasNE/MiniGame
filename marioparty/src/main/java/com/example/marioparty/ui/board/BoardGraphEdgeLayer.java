package com.example.marioparty.ui.board;

import com.example.marioparty.model.Board;
import com.example.marioparty.model.graph.BoardEdge;
import com.example.marioparty.model.graph.BoardKnot;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;

/**
 * Zeichnet alle gerichteten Kanten des Bretts: gleichmäßige Linien + kleine Pfeilspitze in Laufrichtung.
 */
public class BoardGraphEdgeLayer extends Group {

    private static final double KNOT_RADIUS_INSET = 30;
    private static final double ARROW_FRACTION = 0.78;
    private static final double ARROW_SIZE = 14;

    public BoardGraphEdgeLayer(Board board) {
        setMouseTransparent(true);
        for (BoardEdge edge : board.getAllEdges()) {
            BoardKnot a = board.getKnot(edge.fromKnotId());
            BoardKnot b = board.getKnot(edge.toKnotId());
            getChildren().add(buildDirectedEdge(a.getX(), a.getY(), b.getX(), b.getY()));
        }
    }

    private static Group buildDirectedEdge(double ax, double ay, double bx, double by) {
        double dx = bx - ax;
        double dy = by - ay;
        double len = Math.hypot(dx, dy);
        if (len < 1e-3) {
            return new Group();
        }
        double ux = dx / len;
        double uy = dy / len;
        double inset = KNOT_RADIUS_INSET;
        double sx = ax + ux * inset;
        double sy = ay + uy * inset;
        double ex = bx - ux * inset;
        double ey = by - uy * inset;

        Line line = new Line(sx, sy, ex, ey);
        line.setStroke(Color.rgb(210, 235, 200, 0.75));
        line.setStrokeWidth(3.2);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        double mx = sx + (ex - sx) * ARROW_FRACTION;
        double my = sy + (ey - sy) * ARROW_FRACTION;
        double px = -uy;
        double py = ux;
        double s = ARROW_SIZE * 0.55;
        Polygon head = new Polygon(
                mx + ux * ARROW_SIZE, my + uy * ARROW_SIZE,
                mx - px * s, my - py * s,
                mx + px * s, my + py * s
        );
        head.setFill(Color.rgb(240, 255, 230, 0.88));
        head.setStroke(Color.rgb(80, 120, 70, 0.85));
        head.setStrokeWidth(1);

        Group g = new Group();
        g.getChildren().addAll(line, head);
        return g;
    }
}
