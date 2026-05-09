package com.example.marioparty.model;

import com.example.marioparty.model.graph.BoardEdge;
import com.example.marioparty.model.graph.BoardKnot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Brett als <strong>gerichteter Graph</strong>. Die <strong>Topologie</strong> (Knoten-Positionen und
 * Kanten inkl. Gabelungen) ist <strong>fest</strong> definiert; die <strong>Feldtypen</strong> (Blau/Rot/Event)
 * werden pro Partie <strong>zufällig</strong> vergeben (Start bleibt Start, Stern weiter dynamisch).
 */
public class Board {

    public static final int STAR_COIN_COST = 20;

    /** Anzahl Knoten der festen Großkarte (Ids 0 … KNOT_COUNT-1). */
    public static final int KNOT_COUNT = 25;

    /**
     * Gleichmäßiges Layout: Hauptweg mit konstantem horizontalen Abstand, Gabel mit zwei symmetrischen
     * Schräg-Beinen gleicher Schrittlänge, Zusammenlauf bei Knoten 12, Rückweg mit gleichem Raster.
     */
    private static final double[] KNOT_X = new double[KNOT_COUNT];
    private static final double[] KNOT_Y = new double[KNOT_COUNT];

    static {
        initUniformMapLayout();
    }

    private static void initUniformMapLayout() {
        final double dx = 91;
        final double x0 = 34;
        final double yMain = 406;
        for (int i = 0; i <= 8; i++) {
            KNOT_X[i] = x0 + i * dx;
            KNOT_Y[i] = yMain;
        }
        double x8 = KNOT_X[8];
        double y8 = KNOT_Y[8];
        final double leg = 92;
        double hx = Math.cos(Math.toRadians(50));
        double hy = -Math.sin(Math.toRadians(50));
        double px = x8;
        double py = y8;
        for (int i = 0; i < 4; i++) {
            px += leg * hx;
            py += leg * hy;
            KNOT_X[9 + i] = px;
            KNOT_Y[9 + i] = py;
        }
        double lx = hx;
        double ly = -hy;
        px = x8;
        py = y8;
        for (int i = 0; i < 3; i++) {
            px += leg * lx;
            py += leg * ly;
            KNOT_X[22 + i] = px;
            KNOT_Y[22 + i] = py;
        }
        double ddx = KNOT_X[12] - KNOT_X[23];
        double ddy = KNOT_Y[12] - KNOT_Y[23];
        double rem = Math.hypot(ddx, ddy);
        if (rem > 1e-3) {
            double t = Math.min(leg / rem, 1.0);
            KNOT_X[24] = KNOT_X[23] + ddx * t;
            KNOT_Y[24] = KNOT_Y[23] + ddy * t;
        }
        for (int i = 13; i <= 21; i++) {
            KNOT_X[i] = KNOT_X[i - 1] - dx;
            KNOT_Y[i] = KNOT_Y[12];
        }
        KNOT_X[21] = KNOT_X[0] + dx * 0.85;
        KNOT_Y[21] = yMain - 96;
    }

    private final List<BoardKnot> knots = new ArrayList<>();
    private final Random random = new Random();
    private int starKnotId;

    public Board() {
        buildLargeFixedTopology();
        randomizePlayfieldTypes();
        placeInitialStar();
    }

    /**
     * Topologie (nur Struktur + Layout): 0 Start → … → 8, dann Gabel 8→9 bzw. 8→22,
     * Zusammenlauf bei 12, weiter 12→13→…→21→0.
     */
    private void buildLargeFixedTopology() {
        knots.clear();
        for (int id = 0; id < KNOT_COUNT; id++) {
            Field.Type initial = (id == 0) ? Field.Type.START : Field.Type.BLUE;
            addKnot(id, KNOT_X[id], KNOT_Y[id], initial);
        }

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
        link(24, 12);
        for (int i = 12; i < 21; i++) {
            link(i, i + 1);
        }
        link(21, 0);
    }

    private void randomizePlayfieldTypes() {
        for (int id = 1; id < KNOT_COUNT; id++) {
            k(id).setFieldType(randomNonStartFieldType());
        }
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
        k(fromKnotId).addOutgoingEdge(new BoardEdge(fromKnotId, toKnotId));
    }

    public List<Integer> getTargetKnotIds(int knotId) {
        return k(knotId).getTargetKnotIds();
    }

    public List<BoardEdge> getOutgoingEdges(int knotId) {
        return k(knotId).getOutgoingEdges();
    }

    /** Alle gerichteten Kanten (für Darstellung unter den Feldern). */
    public List<BoardEdge> getAllEdges() {
        List<BoardEdge> all = new ArrayList<>();
        for (int i = 0; i < knots.size(); i++) {
            all.addAll(k(i).getOutgoingEdges());
        }
        return Collections.unmodifiableList(all);
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
            if (knot.getFieldType() != Field.Type.START) {
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
}
