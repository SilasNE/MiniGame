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
 * Unterer Ast (ab 8) läuft zuerst weiter nach unten, steigt dann über Zwischenknoten wieder zur
 * Einmündung bei Knoten 12.
 */
public class Board {

    public static final int STAR_COIN_COST = 20;

    public static final int KNOT_COUNT = 30;

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
     * Alle Koordinaten fest eingetragen (minimal nach links gerückt, damit rechts nichts abgeschnitten wirkt).
     * Unterer Pfad: 22→23→24 weiter unten, dann 25→…→29 wieder hoch zu 12.
     */
    private void buildLargeFixedTopology() {
        knots.clear();
        addKnot(0, 4.00, 454.00, Field.Type.START);
        addKnot(1, 95.00, 454.00, Field.Type.BLUE);
        addKnot(2, 186.00, 454.00, Field.Type.BLUE);
        addKnot(3, 277.00, 454.00, Field.Type.BLUE);
        addKnot(4, 368.00, 454.00, Field.Type.BLUE);
        addKnot(5, 459.00, 454.00, Field.Type.BLUE);
        addKnot(6, 550.00, 454.00, Field.Type.BLUE);
        addKnot(7, 641.00, 454.00, Field.Type.BLUE);
        addKnot(8, 732.00, 454.00, Field.Type.BLUE);
        addKnot(9, 791.14, 383.52, Field.Type.BLUE);
        addKnot(10, 850.27, 313.05, Field.Type.BLUE);
        addKnot(11, 909.41, 242.57, Field.Type.BLUE);
        addKnot(12, 968.55, 172.10, Field.Type.BLUE);
        addKnot(13, 877.55, 172.10, Field.Type.BLUE);
        addKnot(14, 786.55, 172.10, Field.Type.BLUE);
        addKnot(15, 695.55, 172.10, Field.Type.BLUE);
        addKnot(16, 604.55, 172.10, Field.Type.BLUE);
        addKnot(17, 513.55, 172.10, Field.Type.BLUE);
        addKnot(18, 422.55, 172.10, Field.Type.BLUE);
        addKnot(19, 331.55, 172.10, Field.Type.BLUE);
        addKnot(20, 240.55, 172.10, Field.Type.BLUE);
        addKnot(21, 81.35, 358.00, Field.Type.BLUE);
        addKnot(22, 778.00, 528.00, Field.Type.BLUE);
        addKnot(23, 830.00, 608.00, Field.Type.BLUE);
        addKnot(24, 882.00, 648.00, Field.Type.BLUE);
        addKnot(25, 928.00, 578.00, Field.Type.BLUE);
        addKnot(26, 962.00, 502.00, Field.Type.BLUE);
        addKnot(27, 980.00, 422.00, Field.Type.BLUE);
        addKnot(28, 986.00, 342.00, Field.Type.BLUE);
        addKnot(29, 980.00, 252.00, Field.Type.BLUE);

        for (int i = 0; i < 8; i++) {
            link(i, i + 1);
        }
        link(8, 9);
        link(8, 22);
        link(9, 10);
        link(10, 11);
        link(11, 12);
        link(22, 23);
        link(23, 24);
        link(24, 25);
        link(25, 26);
        link(26, 27);
        link(27, 28);
        link(28, 29);
        link(29, 12);
        for (int i = 12; i < 21; i++) {
            link(i, i + 1);
        }
        link(21, 0);
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
