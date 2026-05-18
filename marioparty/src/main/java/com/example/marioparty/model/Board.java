package com.example.marioparty.model;

import com.example.marioparty.model.graph.BoardKnot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * Brett als <strong>gerichteter Graph</strong>: feste Pixel-Positionen pro {@code addKnot}.
 * Großer Rundweg mit mehreren Abzweigungen und Rückwegen, damit es mehr nach Mario-Party-Map wirkt.
 */
public class Board {

    public static final int STAR_COIN_COST = 20;

    public static final int KNOT_COUNT = 48;

    /** Fester Item-Shop (Knoten-Id). */
    public static final int ITEM_SHOP_KNOT_ID = 15;

    private final List<BoardKnot> knots = new ArrayList<>();
    private final Random random = new Random();
    private int starKnotId;

    public Board() {
        buildLargeFixedTopology();
        randomizePlayfieldTypes();
        placeInitialStar();
    }

    /**
     * Alle Koordinaten fest eingetragen. Hauptweg: großer Rundweg um die Insel.
     * Abzweigungen: innere Brücke, obere Route, untere Route und ein Rückweg.
     */
    private void buildLargeFixedTopology() {
        knots.clear();
        addKnot(0, 900.00, 460.00, Field.Type.START);
        addKnot(1, 860.00, 515.00, Field.Type.BLUE);
        addKnot(2, 800.00, 555.00, Field.Type.BLUE);
        addKnot(3, 730.00, 575.00, Field.Type.BLUE);
        addKnot(4, 660.00, 565.00, Field.Type.BLUE);
        addKnot(5, 600.00, 530.00, Field.Type.BLUE);
        addKnot(6, 545.00, 485.00, Field.Type.BLUE);
        addKnot(7, 480.00, 500.00, Field.Type.BLUE);
        addKnot(8, 420.00, 545.00, Field.Type.BLUE);
        addKnot(9, 350.00, 575.00, Field.Type.BLUE);
        addKnot(10, 280.00, 560.00, Field.Type.BLUE);
        addKnot(11, 215.00, 520.00, Field.Type.BLUE);
        addKnot(12, 165.00, 465.00, Field.Type.BLUE);
        addKnot(13, 130.00, 400.00, Field.Type.BLUE);
        addKnot(14, 125.00, 330.00, Field.Type.BLUE);
        addKnot(15, 165.00, 270.00, Field.Type.BLUE);
        addKnot(16, 225.00, 225.00, Field.Type.BLUE);
        addKnot(17, 295.00, 200.00, Field.Type.BLUE);
        addKnot(18, 370.00, 205.00, Field.Type.BLUE);
        addKnot(19, 440.00, 240.00, Field.Type.BLUE);
        addKnot(20, 500.00, 285.00, Field.Type.BLUE);
        addKnot(21, 565.00, 260.00, Field.Type.BLUE);
        addKnot(22, 630.00, 215.00, Field.Type.BLUE);
        addKnot(23, 705.00, 195.00, Field.Type.BLUE);
        addKnot(24, 780.00, 210.00, Field.Type.BLUE);
        addKnot(25, 845.00, 255.00, Field.Type.BLUE);
        addKnot(26, 895.00, 315.00, Field.Type.BLUE);
        addKnot(27, 925.00, 380.00, Field.Type.BLUE);

        addKnot(28, 505.00, 420.00, Field.Type.BLUE);
        addKnot(29, 565.00, 390.00, Field.Type.BLUE);
        addKnot(30, 635.00, 375.00, Field.Type.BLUE);
        addKnot(31, 705.00, 398.00, Field.Type.BLUE);
        addKnot(32, 760.00, 440.00, Field.Type.BLUE);
        addKnot(33, 820.00, 500.00, Field.Type.BLUE);

        addKnot(34, 415.00, 150.00, Field.Type.BLUE);
        addKnot(35, 505.00, 135.00, Field.Type.BLUE);
        addKnot(36, 610.00, 140.00, Field.Type.BLUE);
        addKnot(37, 710.00, 160.00, Field.Type.BLUE);

        addKnot(38, 300.00, 475.00, Field.Type.BLUE);
        addKnot(39, 380.00, 445.00, Field.Type.BLUE);
        addKnot(40, 470.00, 455.00, Field.Type.BLUE);
        addKnot(41, 570.00, 500.00, Field.Type.BLUE);

        addKnot(42, 210.00, 375.00, Field.Type.BLUE);
        addKnot(43, 295.00, 345.00, Field.Type.BLUE);
        addKnot(44, 380.00, 330.00, Field.Type.BLUE);
        addKnot(45, 460.00, 335.00, Field.Type.BLUE);

        addKnot(46, 640.00, 330.00, Field.Type.BLUE);
        addKnot(47, 535.00, 345.00, Field.Type.BLUE);

        for (int i = 0; i < 27; i++) {
            link(i, i + 1);
        }
        link(27, 0);

        link(6, 28);
        link(28, 29);
        link(29, 30);
        link(30, 31);
        link(31, 32);
        link(32, 33);
        link(33, 2);

        link(18, 34);
        link(34, 35);
        link(35, 36);
        link(36, 37);
        link(37, 24);

        link(10, 38);
        link(38, 39);
        link(39, 40);
        link(40, 41);
        link(41, 4);

        link(13, 42);
        link(42, 43);
        link(43, 44);
        link(44, 45);
        link(45, 20);

        link(31, 46);
        link(46, 47);
        link(47, 15);
    }

    private void randomizePlayfieldTypes() {
        for (int id = 1; id < KNOT_COUNT; id++) {
            if (id == ITEM_SHOP_KNOT_ID) {
                continue;
            }
            k(id).setFieldType(randomNonStartFieldType());
        }
        k(ITEM_SHOP_KNOT_ID).setFieldType(Field.Type.ITEM_SHOP);
    }

    private Field.Type randomNonStartFieldType() {
        return switch (random.nextInt(10)) {
            case 0, 1, 2, 3, 4 -> Field.Type.BLUE;
            case 5, 6, 7 -> Field.Type.RED;
            default -> Field.Type.EVENT;
        };
    }

    private void addKnot(int id, double x, double y, Field.Type type) {
        knots.add(new BoardKnot(id, x, y, type));
    }

    private BoardKnot k(int id) {
        return knots.get(id);
    }

    private void link(int fromKnotId, int toKnotId) {
        k(fromKnotId).addTargetKnotId(toKnotId);
    }

    public List<Integer> getTargetKnotIds(int knotId) {
        return k(knotId).getTargetKnotIds();
    }

    public BoardKnot getKnot(int knotId) {
        return k(knotId);
    }

    public Field getField(int knotId) {
        BoardKnot knot = k(knotId);
        return new Field(knot.getFieldType(), knot.getX(), knot.getY());
    }

    public int size() {
        return knots.size();
    }

    public List<Field> getFields() {
        List<Field> out = new ArrayList<>();
        for (int i = 0; i < knots.size(); i++) {
            out.add(getField(i));
        }
        return Collections.unmodifiableList(out);
    }

    private List<Integer> starCandidateKnotIds() {
        List<Integer> out = new ArrayList<>();
        for (BoardKnot knot : knots) {
            if (knot.getFieldType() != Field.Type.START && knot.getFieldType() != Field.Type.ITEM_SHOP) {
                out.add(knot.getId());
            }
        }
        return out;
    }

    private void placeInitialStar() {
        List<Integer> c = starCandidateKnotIds();
        if (c.isEmpty()) {
            starKnotId = 0;
            return;
        }
        starKnotId = c.get(random.nextInt(c.size()));
    }

    public void respawnStarAfterPurchase() {
        List<Integer> c = starCandidateKnotIds();
        if (c.isEmpty()) {
            return;
        }
        if (c.size() == 1) {
            starKnotId = c.get(0);
            return;
        }
        int next;
        do {
            next = c.get(random.nextInt(c.size()));
        } while (next == starKnotId);
        starKnotId = next;
    }

    public int getStarFieldIndex() {
        return starKnotId;
    }

    public int getStarKnotId() {
        return starKnotId;
    }

    public boolean isStarAt(int knotId) {
        return knotId == starKnotId;
    }

    /**
     * Kürzeste Weglänge in Kanten — Breitensuche auf dem gerichteten Graphen.
     */
    public int bfsDistance(int fromKnotId, int toKnotId) {
        if (fromKnotId == toKnotId) {
            return 0;
        }
        Queue<Integer> q = new ArrayDeque<>();
        Map<Integer, Integer> dist = new HashMap<>();
        q.add(fromKnotId);
        dist.put(fromKnotId, 0);
        while (!q.isEmpty()) {
            int u = q.poll();
            int d = dist.get(u);
            for (int v : getTargetKnotIds(u)) {
                if (dist.containsKey(v)) {
                    continue;
                }
                int nd = d + 1;
                if (v == toKnotId) {
                    return nd;
                }
                dist.put(v, nd);
                q.add(v);
            }
        }
        return 1_000_000;
    }

    /** Bei Gabelung: Nachfolger mit geringstem BFS-Abstand zum Stern (bei Gleichstand kleinere Id). */
    public int pickSuccessorTowardStar(int starKnotId, List<Integer> successors) {
        if (successors.isEmpty()) {
            return -1;
        }
        int best = successors.getFirst();
        int bestD = bfsDistance(best, starKnotId);
        for (int s : successors) {
            int d = bfsDistance(s, starKnotId);
            if (d < bestD || (d == bestD && s < best)) {
                best = s;
                bestD = d;
            }
        }
        return best;
    }
}
