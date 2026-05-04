package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein einzelnes Feld auf dem Spielbrett.
 * Felder sind in einem Graphen verknüpft (über {@link #neighbors}),
 * so dass Abzweigungen möglich sind (Mario Party typisch).
 */
public class FieldNode {

    private final int id;
    private final double xPos;
    private final double yPos;
    private FieldType type;
    private final List<FieldNode> neighbors;
    private boolean hasStar;

    public FieldNode(int id, double xPos, double yPos, FieldType type) {
        this.id = id;
        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;
        this.neighbors = new ArrayList<>();
        this.hasStar = false;
    }

    public List<FieldNode> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(FieldNode neighbor) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
        }
    }

    // --- Getter / Setter ---
    public int getId() { return id; }
    public double getxPos() { return xPos; }
    public double getyPos() { return yPos; }
    public FieldType getType() { return type; }
    public void setType(FieldType type) { this.type = type; }
    public boolean isHasStar() { return hasStar; }
    public void setHasStar(boolean hasStar) { this.hasStar = hasStar; }

    @Override
    public String toString() {
        return "Field#" + id + "(" + type + ")";
    }
}
