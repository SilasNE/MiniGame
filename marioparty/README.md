# Mini Mario Party (JavaFX + Maven)

Ein einfaches Brettspiel mit eigenem Mini-Game-Engine-Skelett, aufgesetzt auf
der Standard-Maven-Struktur, wie sie auch IntelliJ IDEAs JavaFX-Wizard erzeugt.

## Projektstruktur

```
marioparty/
├── pom.xml                                       ← Maven-Build + Dependencies
├── .gitignore
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java                  ← Modul-Definition (JPMS)
        │   └── com/example/marioparty/
        │       ├── Main.java                     ← Application-Einstieg
        │       │
        │       ├── engine/                       ← Wiederverwendbare Engine
        │       │   ├── GameEngine.java           ← AnimationTimer + Scene-Mgmt
        │       │   ├── GameScene.java            ← Abstrakte Szenenbasis
        │       │   └── InputHandler.java         ← Maus, Tastatur
        │       │
        │       ├── model/                        ← Spielzustand (UI-frei)
        │       │   ├── GameState.java            ← Spieler, Runde, Board
        │       │   ├── Player.java               ← Sterne, Münzen, Position
        │       │   ├── Board.java                ← Anordnung der Felder
        │       │   ├── Field.java                ← Einzelfeld mit Typ
        │       │   └── Dice.java                 ← Würfel (1..6)
        │       │
        │       ├── scenes/                       ← Konkrete Szenen
        │       │   ├── MenuScene.java            ← Startbildschirm
        │       │   ├── BoardScene.java           ← Hauptspiel
        │       │   └── MiniGameScene.java        ← Hülle für Minispiele
        │       │
        │       └── minigames/
        │           ├── MiniGame.java             ← Abstrakte Basis
        │           └── ButtonMashGame.java       ← Beispiel-Minispiel
        │
        └── resources/
            └── com/example/marioparty/           ← später: Bilder, Sounds, FXML
```

## Voraussetzungen

- **JDK 21** (oder 17+, dann `<source>` und `<target>` in der pom.xml anpassen)
- **Maven 3.8+** (oder einfach IntelliJ benutzen — bringt Maven mit)

JavaFX selbst musst du **nicht** separat installieren — Maven zieht es als
Dependency automatisch.

## Starten

### Variante A — IntelliJ IDEA (empfohlen)

1. **File → Open** → den Ordner `marioparty` auswählen
2. IntelliJ erkennt das Maven-Projekt und lädt Dependencies automatisch
3. `Main.java` öffnen → grünen Play-Knopf neben `public static void main` klicken
4. Fertig. Falls IntelliJ über fehlende JavaFX-Module meckert: rechts im
   Maven-Tab einmal **Reload All Maven Projects**.

### Variante B — Kommandozeile mit Maven

```bash
cd marioparty
mvn clean javafx:run
```

Beim ersten Aufruf lädt Maven JavaFX einmalig herunter (~30 MB).

## Spielablauf

1. **Menü** → Leertaste startet das Spiel
2. **Board**: 4 Spieler reihum, Leertaste zum Würfeln
   - Blau: +3 Münzen   |   Rot: −3 Münzen   |   Stern/Event: Erweiterungspunkte
3. Nach jeder Runde: **Minispiel** (Button-Mash, jeder Spieler eine Taste)
4. Sieger des Minispiels: +1 Stern, +10 Münzen
5. Nach 5 Runden: zurück ins Menü

## Tasten

| Aktion          | Taste                                          |
|-----------------|------------------------------------------------|
| Würfeln / Start | `LEERTASTE`                                    |
| Minispiel       | `A` (Mario), `L` (Luigi), `G` (Peach), `↑` (Bowser) |

## Architektur in Kürze

- **Engine** (`engine/`) ist generisch — du könntest damit jedes 2D-Spiel bauen.
- **Model** (`model/`) ist UI-frei — leicht testbar, leicht erweiterbar.
- **Scenes** (`scenes/`) sind die "Bildschirme" und werden via
  `engine.setScene(new XYZ(engine))` gewechselt.
- **MiniGames** sind eigene austauschbare Module innerhalb der MiniGameScene.

## Erweiterungsideen

- **Neues Minispiel**: Klasse anlegen, `MiniGame` erben, in `MiniGameScene`
  zufällig auswählen.
- **Sternfeld-Logik**: in `BoardScene.FIELD_ACTION` → "Stern für 20 Münzen kaufen?"
- **Items**: `Player` um `List<Item>` erweitern (Pilz = +1 Würfel, Bowser-Item, ...)
- **Sound**: `requires javafx.media;` ergänzen, dann `AudioClip` für SFX
- **Asset-Manager**: Bilder per `Image`/`ImageView` statt Primitiven
- **Animation**: Spielfigur smooth zwischen Feldern interpolieren
- **ResultScene**: schöne Siegerehrung statt Rücksprung ins Menü
