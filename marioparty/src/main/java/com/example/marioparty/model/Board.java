package com.example.marioparty.model;

import com.example.marioparty.model.graph.BoardKnot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;





public class Board {

    public static final int STAR_COIN_COST = 20;

    public static final int KNOT_COUNT = 47;


    public static final List<Integer> ITEM_SHOP_KNOT_IDS = List.of(15, 31, 43);

    private final List<BoardKnot> knots = new ArrayList<>();
    private final Random random = new Random();
    private int starKnotId;

    public Board() {
        buildLargeFixedTopology();
        randomizeFieldTypes();
        placeInitialStar();
    }





    private void buildLargeFixedTopology() {
        knots.clear();
        addKnot(0, 125.00, 400.00, Field.Type.START);
        addKnot(1, 125.00, 330.00, Field.Type.BLUE);
        addKnot(2, 165.00, 270.00, Field.Type.BLUE);
        addKnot(3, 225.00, 225.00, Field.Type.BLUE);
        addKnot(4, 295.00, 200.00, Field.Type.BLUE);
        addKnot(5, 370.00, 205.00, Field.Type.BLUE);
        addKnot(6, 440.00, 240.00, Field.Type.BLUE);
        addKnot(7, 500.00, 285.00, Field.Type.BLUE);
        addKnot(8, 565.00, 260.00, Field.Type.BLUE);
        addKnot(9, 630.00, 215.00, Field.Type.BLUE);
        addKnot(10, 705.00, 220.00, Field.Type.BLUE);
        addKnot(11, 780.00, 210.00, Field.Type.BLUE);
        addKnot(12, 845.00, 255.00, Field.Type.BLUE);
        addKnot(13, 895.00, 315.00, Field.Type.BLUE);
        addKnot(14, 925.00, 380.00, Field.Type.BLUE);
        addKnot(15, 900.00, 460.00, Field.Type.BLUE);
        addKnot(16, 860.00, 515.00, Field.Type.BLUE);
        addKnot(17, 800.00, 555.00, Field.Type.BLUE);
        addKnot(18, 730.00, 575.00, Field.Type.BLUE);
        addKnot(19, 660.00, 565.00, Field.Type.BLUE);
        addKnot(20, 600.00, 530.00, Field.Type.BLUE);
        addKnot(21, 545.00, 485.00, Field.Type.BLUE);
        addKnot(22, 480.00, 500.00, Field.Type.BLUE);
        addKnot(23, 420.00, 545.00, Field.Type.BLUE);
        addKnot(24, 350.00, 575.00, Field.Type.BLUE);
        addKnot(25, 280.00, 560.00, Field.Type.BLUE);
        addKnot(26, 215.00, 520.00, Field.Type.BLUE);
        addKnot(27, 165.00, 465.00, Field.Type.BLUE);

        addKnot(28, 505.00, 420.00, Field.Type.BLUE);
        addKnot(29, 565.00, 390.00, Field.Type.BLUE);
        addKnot(30, 635.00, 375.00, Field.Type.BLUE);
        addKnot(31, 705.00, 398.00, Field.Type.BLUE);
        addKnot(32, 760.00, 440.00, Field.Type.BLUE);
        addKnot(33, 820.00, 500.00, Field.Type.BLUE);

        addKnot(34, 415.00, 150.00, Field.Type.BLUE);
        addKnot(35, 505.00, 135.00, Field.Type.BLUE);
        addKnot(36, 620.00, 165.00, Field.Type.BLUE);
        addKnot(37, 735.00, 170.00, Field.Type.BLUE);

        addKnot(38, 300.00, 475.00, Field.Type.BLUE);
        addKnot(39, 380.00, 445.00, Field.Type.BLUE);
        addKnot(40, 470.00, 455.00, Field.Type.BLUE);
        addKnot(41, 210.00, 375.00, Field.Type.BLUE);
        addKnot(42, 295.00, 345.00, Field.Type.BLUE);
        addKnot(43, 380.00, 330.00, Field.Type.BLUE);
        addKnot(44, 460.00, 335.00, Field.Type.BLUE);

        addKnot(45, 640.00, 330.00, Field.Type.BLUE);
        addKnot(46, 535.00, 345.00, Field.Type.BLUE);

        for (int i = 0; i < 27; i++) {
            link(i, i + 1);
        }
        link(27, 0);

        link(21, 28);
        link(28, 29);
        link(29, 30);
        link(30, 31);
        link(31, 32);
        link(32, 33);
        link(33, 17);

        link(5, 34);
        link(34, 35);
        link(35, 36);
        link(36, 37);
        link(37, 11);

        link(25, 38);
        link(38, 39);
        link(39, 40);
        link(40, 21);

        link(0, 41);
        link(41, 42);
        link(42, 43);
        link(43, 44);
        link(44, 7);

        link(31, 45);
        link(45, 46);
        link(46, 44);
    }

    private void randomizeFieldTypes() {
        for (int id = 1; id < KNOT_COUNT; id++) {
            if (ITEM_SHOP_KNOT_IDS.contains(id)) {
                getKnot(id).setFieldType(Field.Type.ITEM_SHOP);
            } else {
                getKnot(id).setFieldType(randomNonStartFieldType());
            }
        }
    }

    private Field.Type randomNonStartFieldType() {
        return switch (random.nextInt(10)) {
            case 0, 1, 2, 3, 4 -> Field.Type.BLUE;
            case 5, 6, 7 -> Field.Type.RED;
            default -> Field.Type.NEUTRAL;
        };
    }

    private void addKnot(int id, double x, double y, Field.Type type) {
        knots.add(new BoardKnot(id, x, y, type));
    }

    private void link(int fromKnotId, int toKnotId) {
        getKnot(fromKnotId).addTargetKnotId(toKnotId);
    }

    public List<Integer> getTargetKnotIds(int knotId) {
        return getKnot(knotId).getTargetKnotIds();
    }

    public BoardKnot getKnot(int knotId) {
        return knots.get(knotId);
    }

    public Field getField(int knotId) {
        BoardKnot knot = getKnot(knotId);
        return new Field(knot.getFieldType(), knot.getX(), knot.getY());
    }

    public int size() {
        return knots.size();
    }

    private boolean isValidStarKnot(int knotId) {
        if (ITEM_SHOP_KNOT_IDS.contains(knotId)) {
            return false;
        }
        return getKnot(knotId).getFieldType() != Field.Type.START;
    }

    private int randomStarKnotId() {
        int knotId;
        do {
            knotId = random.nextInt(KNOT_COUNT);
        } while (!isValidStarKnot(knotId));
        return knotId;
    }

    private void placeInitialStar() {
        starKnotId = randomStarKnotId();
    }

    public void respawnStarAfterPurchase() {
        int next;
        do {
            next = randomStarKnotId();
        } while (next == starKnotId);
        starKnotId = next;
    }

    public int getStarKnotId() {
        return starKnotId;
    }

    public boolean isStarAt(int knotId) {
        return knotId == starKnotId;
    }




    public int bsDistance(int fromKnotId, int toKnotId) {
        if (fromKnotId == toKnotId) {
            return 0;
        }
        Queue<Integer> knotQueue = new ArrayDeque<>();
        Map<Integer, Integer> distanceByKnotId = new HashMap<>();
        knotQueue.add(fromKnotId);
        distanceByKnotId.put(fromKnotId, 0);
        while (!knotQueue.isEmpty()) {
            int currentKnotId = knotQueue.poll();
            int stepsFromStart = distanceByKnotId.get(currentKnotId);
            for (int neighborKnotId : getTargetKnotIds(currentKnotId)) {
                if (distanceByKnotId.containsKey(neighborKnotId)) {
                    continue;
                }
                int stepsToNeighbor = stepsFromStart + 1;
                if (neighborKnotId == toKnotId) {
                    return stepsToNeighbor;
                }
                distanceByKnotId.put(neighborKnotId, stepsToNeighbor);
                knotQueue.add(neighborKnotId);
            }
        }
        return -1;
    }

    public int pickSuccessorTowardStar(int starKnotId, List<Integer> successors) {
        if (successors.isEmpty()) {
            return -1;
        }
        int bestWay = -1;
        int shortest = -1;
        for (int way : successors) {
            int distance = bsDistance(way, starKnotId);
            if (distance < 0) {
                continue;
            }
            if (bestWay < 0 || distance < shortest) {
                bestWay = way;
                shortest = distance;
            }
        }
        return bestWay;
    }
}
