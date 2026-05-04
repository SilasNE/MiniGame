package game.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Spielbrett als rechteckiger Ring aus 16 Feldern.
 * Position 0 ist Start, danach im Uhrzeigersinn.
 */
public class Board {

    private final List<Field> fields = new ArrayList<>();

    public Board() {
        double cx = 512, cy = 410;
        double w = 760, h = 380;
        int perSide = 4;

        // Oben: links → rechts
        for (int i = 0; i < perSide; i++) {
            double x = cx - w / 2 + i * (w / perSide);
            fields.add(new Field(pickType(i), x, cy - h / 2));
        }
        // Rechts: oben → unten
        for (int i = 0; i < perSide; i++) {
            double y = cy - h / 2 + i * (h / perSide);
            fields.add(new Field(pickType(i + 4), cx + w / 2, y));
        }
        // Unten: rechts → links
        for (int i = 0; i < perSide; i++) {
            double x = cx + w / 2 - i * (w / perSide);
            fields.add(new Field(pickType(i + 8), x, cy + h / 2));
        }
        // Links: unten → oben
        for (int i = 0; i < perSide; i++) {
            double y = cy + h / 2 - i * (h / perSide);
            fields.add(new Field(pickType(i + 12), cx - w / 2, y));
        }

        // Position 0 ist immer Start
        Field f0 = fields.get(0);
        fields.set(0, new Field(Field.Type.START, f0.getX(), f0.getY()));
    }

    /** Pseudo-zufällige Verteilung der Feldtypen. */
    private Field.Type pickType(int index) {
        return switch (index % 5) {
            case 0, 1 -> Field.Type.BLUE;
            case 2    -> Field.Type.RED;
            case 3    -> Field.Type.STAR;
            case 4    -> Field.Type.EVENT;
            default   -> Field.Type.BLUE;
        };
    }

    public Field getField(int index) {
        return fields.get(((index % fields.size()) + fields.size()) % fields.size());
    }

    public int size()              { return fields.size(); }
    public List<Field> getFields() { return fields; }
}
