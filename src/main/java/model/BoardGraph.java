package model;

import java.util.*;

/**
 * Das komplette Spielbrett als Graph aus {@link FieldNode}s.
 * Kümmert sich ums Auffinden aller möglichen Pfade einer bestimmten Länge
 * (wichtig, damit der Spieler an Kreuzungen wählen kann).
 */
public class BoardGraph {

    private final Map<Integer, FieldNode> nodes;
    private String backgroundImagePath;

    public BoardGraph() {
        this.nodes = new HashMap<>();
    }

    public void addNode(FieldNode node) {
        nodes.put(node.getId(), node);
    }

    public FieldNode getNode(int id) {
        return nodes.get(id);
    }

    public Map<Integer, FieldNode> getNodes() {
        return nodes;
    }

    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    public void setBackgroundImagePath(String backgroundImagePath) {
        this.backgroundImagePath = backgroundImagePath;
    }

    /**
     * Ermittelt ALLE möglichen Pfade einer bestimmten Länge ab einem Startfeld.
     * Rückweg wird nicht erlaubt (Vorgänger wird ausgeschlossen), damit der
     * Spieler nicht rückwärts läuft.
     *
     * @param start  Feld, auf dem der Spieler aktuell steht
     * @param length Anzahl der Schritte (Würfelergebnis)
     * @return Liste aller möglichen Pfade; jeder Pfad ist eine Liste von FieldNodes
     */
    public List<List<FieldNode>> findPath(FieldNode start, int length) {
        List<List<FieldNode>> result = new ArrayList<>();
        List<FieldNode> current = new ArrayList<>();
        current.add(start);
        dfs(start, null, length, current, result);
        return result;
    }

    private void dfs(FieldNode node, FieldNode previous, int remaining,
                     List<FieldNode> current, List<List<FieldNode>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (FieldNode neighbor : node.getNeighbors()) {
            if (neighbor == previous) continue; // keinen Rückweg zulassen
            current.add(neighbor);
            dfs(neighbor, node, remaining - 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
