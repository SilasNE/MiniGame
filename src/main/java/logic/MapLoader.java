package logic;

import model.BoardGraph;
import model.FieldNode;
import model.FieldType;

import java.util.ArrayList;
import java.util.List;

/**
 * Lädt ein Spielbrett aus einer JSON-Datei.
 * <p>
 * HINWEIS: Das echte JSON-Parsing ist noch zu implementieren (z. B. mit
 * Jackson oder Gson). Aktuell liefert die Methode ein hart codiertes
 * Beispielbrett zurück, damit das Spiel lauffähig ist.
 */
public class MapLoader {

    /**
     * Lädt ein Brett aus einer JSON-Datei.
     *
     * @param path Pfad zur JSON-Datei
     * @return ein fertig verknüpftes BoardGraph-Objekt
     */
    public static BoardGraph loadFromJSON(String path) {
        // TODO: echten JSON-Parser einsetzen.
        //       Erwartetes JSON-Schema (Beispiel):
        //       {
        //         "background": "/img/board1.png",
        //         "fields": [
        //           {"id":0, "x":100, "y":400, "type":"START", "neighbors":[1]},
        //           {"id":1, "x":200, "y":400, "type":"COIN_PLUS", "neighbors":[0,2]}
        //         ]
        //       }
        return buildDemoBoard();
    }

    /** Minimal-Brett aus 12 Feldern in einer Schleife, damit man testen kann. */
    private static BoardGraph buildDemoBoard() {
        BoardGraph g = new BoardGraph();
        g.setBackgroundImagePath("/img/board_demo.png");

        FieldType[] sequence = {
                FieldType.START,
                FieldType.COIN_PLUS,
                FieldType.COIN_MINUS,
                FieldType.MINIGAME,
                FieldType.COIN_PLUS,
                FieldType.EVENT,
                FieldType.STAR_SHOP,
                FieldType.COIN_MINUS,
                FieldType.TELEPORT,
                FieldType.COIN_PLUS,
                FieldType.MINIGAME,
                FieldType.EVENT
        };

        List<FieldNode> created = new ArrayList<>();
        int n = sequence.length;
        double radius = 250;
        double cx = 400, cy = 300;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            FieldNode node = new FieldNode(
                    i,
                    cx + radius * Math.cos(angle),
                    cy + radius * Math.sin(angle),
                    sequence[i]
            );
            created.add(node);
            g.addNode(node);
        }
        // Ringförmig verknüpfen
        for (int i = 0; i < n; i++) {
            FieldNode a = created.get(i);
            FieldNode b = created.get((i + 1) % n);
            a.addNeighbor(b);
            b.addNeighbor(a);
        }
        // Einen Stern auf den Shop legen
        created.get(6).setHasStar(true);

        return g;
    }
}
