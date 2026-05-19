package com.example.marioparty.ui.board;

import com.example.marioparty.model.Board;
import com.example.marioparty.model.graph.BoardKnot;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;

public class BoardGraphArrows extends Group {

    private static final double KNOT_RADIUS_INSET = 24;
    private static final double ARROW_POSITION_ALONG_LINE = 0.78;
    private static final double ARROW_HEAD_LENGTH = 14;

    public BoardGraphArrows(Board board) {
        setMouseTransparent(true);
        for (int fromKnotId = 0; fromKnotId < board.size(); fromKnotId++) {
            BoardKnot fromKnot = board.getKnot(fromKnotId);
            for (int toKnotId : board.getTargetKnotIds(fromKnotId)) {
                BoardKnot toKnot = board.getKnot(toKnotId);
                getChildren().add(buildDirectedEdge(
                        fromKnot.getX(), fromKnot.getY(),
                        toKnot.getX(), toKnot.getY()));
            }
        }
    }

    private static Group buildDirectedEdge(double fromX, double fromY, double toX, double toY) {
        double deltaX = toX - fromX;
        double deltaY = toY - fromY;
        double edgeLength = Math.hypot(deltaX, deltaY);

        double unitDirX = deltaX / edgeLength;
        double unitDirY = deltaY / edgeLength;

        double lineStartX = fromX + unitDirX * KNOT_RADIUS_INSET;
        double lineStartY = fromY + unitDirY * KNOT_RADIUS_INSET;
        double lineEndX = toX - unitDirX * KNOT_RADIUS_INSET;
        double lineEndY = toY - unitDirY * KNOT_RADIUS_INSET;

        Line edgeLine = new Line(lineStartX, lineStartY, lineEndX, lineEndY);
        edgeLine.setStroke(Color.rgb(255, 255, 255, 0.72));
        edgeLine.setStrokeWidth(5.0);
        edgeLine.setStrokeLineCap(StrokeLineCap.ROUND);

        double arrowCenterX = lineStartX + (lineEndX - lineStartX) * ARROW_POSITION_ALONG_LINE;
        double arrowCenterY = lineStartY + (lineEndY - lineStartY) * ARROW_POSITION_ALONG_LINE;
        double perpendicularX = -unitDirY;
        double perpendicularY = unitDirX;
        double arrowHalfWidth = ARROW_HEAD_LENGTH * 0.55;

        Polygon arrowHead = new Polygon(
                arrowCenterX + unitDirX * ARROW_HEAD_LENGTH,
                arrowCenterY + unitDirY * ARROW_HEAD_LENGTH,
                arrowCenterX - perpendicularX * arrowHalfWidth,
                arrowCenterY - perpendicularY * arrowHalfWidth,
                arrowCenterX + perpendicularX * arrowHalfWidth,
                arrowCenterY + perpendicularY * arrowHalfWidth
        );
        arrowHead.setFill(Color.rgb(255, 246, 168, 0.95));
        arrowHead.setStroke(Color.rgb(8, 62, 140, 0.85));
        arrowHead.setStrokeWidth(1);

        Group directedEdge = new Group();
        directedEdge.getChildren().addAll(edgeLine, arrowHead);
        return directedEdge;
    }
}
