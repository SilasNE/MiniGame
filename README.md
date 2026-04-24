# Mario Party Mini – Projektgerüst

Ein JavaFX-Projektgerüst nach dem UML-Diagramm. Es ist **lauffähig**,
viele Teile sind aber bewusst als Stub oder `TODO` markiert, damit du
selbst ausbauen kannst.

## Projektstruktur

```
src/main/java/
├── app/
│   └── Main.java                   ← JavaFX Application-Einstieg
├── model/
│   ├── FieldType.java              ← Enum der Feldtypen
│   ├── FieldNode.java              ← Einzelnes Feld
│   ├── BoardGraph.java             ← Graph aller Felder + findPath()
│   ├── Player.java                 ← Spielerdaten
│   └── Item.java                   ← Basisklasse für Gegenstände
├── logic/
│   ├── GameEngine.java             ← Spielsteuerung (Züge, Effekte)
│   └── MapLoader.java              ← JSON-Loader (aktuell Demo-Brett)
├── view/
│   ├── MainBoardController.java    ← Hauptfenster, Animationen
│   └── HUDController.java          ← Seitliches Stats-Panel
└── minigame/
    ├── MiniGame.java               ← Interface
    ├── MiniGameResult.java         ← Rangliste & Belohnung
    ├── TicTacToe.java              ← Beispiel-Minispiel
    └── Memory.java                 ← zweites Minispiel (Stub)

src/main/resources/
├── fxml/
│   ├── MainBoard.fxml
│   └── HUD.fxml
└── maps/                           ← Platz für deine JSON-Brett-Dateien
```

## Starten

Voraussetzung: **JDK 17+** und **Maven** installiert.

```bash
mvn clean javafx:run
```

## Was funktioniert bereits

* Fenster öffnet sich, Brett wird gezeichnet (Demo-Brett aus `MapLoader`)
* Spieler werden als farbige Kreise platziert
* Würfeln → Pfadsuche → Bewegung wird animiert
* Bei Abzweigungen erscheint ein Auswahldialog
* Feldeffekte (COIN_PLUS, COIN_MINUS, STAR_SHOP) werden angewendet
* HUD aktualisiert sich nach jedem Zug
* Spielende nach 10 Runden, Gewinnermeldung

## Was du selbst noch ausbauen solltest

* `MapLoader.loadFromJSON()` – echtes JSON mit Jackson/Gson
* `TicTacToe` – Gewinnprüfung und Ergebnis-Eintrag
* `Memory` – Karten, Aufdecken, Paarfindung
* `MainBoardController.launchMiniGame()` – Callback, wenn Minispiel fertig ist
* Feldeffekte `TELEPORT` und `EVENT` in `GameEngine.executeFieldEffect`
* Stern umsetzen, wenn er im Shop gekauft wurde
* Schickere Brett-Grafik (Hintergrundbild statt Farbfläche)
* Item-Klassen (z. B. `DoubleDice extends Item`)

## UML → Code Mapping

| UML-Klasse            | Java-Pfad                               |
|-----------------------|-----------------------------------------|
| MainBoardController   | view/MainBoardController.java           |
| HUDController         | view/HUDController.java                 |
| GameEngine            | logic/GameEngine.java                   |
| MapLoader             | logic/MapLoader.java                    |
| BoardGraph            | model/BoardGraph.java                   |
| FieldNode             | model/FieldNode.java                    |
| FieldType             | model/FieldType.java                    |
| Player                | model/Player.java                       |
| Item                  | model/Item.java                         |
| MiniGame (Interface)  | minigame/MiniGame.java                  |
| MiniGameResult        | minigame/MiniGameResult.java            |
| TicTacToe             | minigame/TicTacToe.java                 |
| Memory                | minigame/Memory.java                    |
