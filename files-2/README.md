# Mini Mario Party (JavaFX)

Ein einfaches Brettspiel mit eigenem Game-Engine-Skelett — als Grundgerüst gedacht,
das du beliebig erweitern kannst (mehr Minispiele, Items, Animationen, Sound …).

## Projektstruktur

```
src/
└── game/
    ├── Main.java                    ← Einstiegspunkt (extends Application)
    │
    ├── engine/                      ← Wiederverwendbare Engine-Bausteine
    │   ├── GameEngine.java          ← AnimationTimer + Scene-Verwaltung
    │   ├── GameScene.java           ← Abstrakte Basis für alle Szenen
    │   └── InputHandler.java        ← Tastatur (isDown / wasJustPressed)
    │
    ├── model/                       ← Spielzustand (UI-frei!)
    │   ├── GameState.java           ← Globaler Zustand: Spieler, Runde, Board
    │   ├── Player.java              ← Spieler mit Sternen, Münzen, Position
    │   ├── Board.java               ← Anordnung der Felder
    │   ├── Field.java               ← Einzelnes Feld mit Typ (BLAU, ROT, STERN ...)
    │   └── Dice.java                ← Würfel (1..6)
    │
    ├── scenes/                      ← Konkrete Szenen
    │   ├── MenuScene.java           ← Startbildschirm
    │   ├── BoardScene.java          ← Hauptspiel (würfeln + bewegen)
    │   └── MiniGameScene.java       ← Hülle für jedes Minispiel
    │
    └── minigames/                   ← Minispiel-Sammlung
        ├── MiniGame.java            ← Abstrakte Basis
        └── ButtonMashGame.java      ← Beispiel: Taste schnell drücken
```

## Architekturidee

**Engine** stellt die Spielschleife (60 FPS via `AnimationTimer`) und die
Scene-Verwaltung. Jede Szene implementiert nur `update(dt, input)` und
`render(gc)` — die Engine kümmert sich um Timing und Rendering-Aufruf.

**Model** ist UI-frei: `Player`, `Board`, `Field` etc. wissen nichts von
JavaFX-Rendering. So kannst du die Logik isoliert testen.

**Scenes** verbinden Model und Darstellung. Wechsel via
`engine.setScene(new BoardScene(engine))`.

**MiniGame** ist eine eigene kleine Abstraktion innerhalb der MiniGameScene —
neue Minispiele = einfach neue Klasse, die `MiniGame` erbt.

## Spielablauf

1. **Menü** → Leertaste startet das Spiel
2. **Board**: 4 Spieler (Mario, Luigi, Peach, Bowser) sind reihum dran und drücken
   Leertaste zum Würfeln. Spielfigur läuft Schritt für Schritt vor.
   - **Blaues Feld:** +3 Münzen
   - **Rotes Feld:** −3 Münzen
   - **Sternfeld (gold):** Bonusmechanik (Erweiterungspunkt)
   - **Eventfeld (lila):** Erweiterungspunkt
3. Nach jeder vollen Runde → **Minispiel** (Button-Mash). Sieger bekommt
   +1 Stern und +10 Münzen.
4. Nach 5 Runden zurück ins Menü. Sieger = meiste Sterne, dann Münzen.

## Tasten

- **Leertaste** — Würfeln / Menü starten
- **Im Minispiel:** A (Mario), L (Luigi), G (Peach), Pfeil ↑ (Bowser)

## Voraussetzungen & Start

- **Java 17+** (wegen Switch-Expressions)
- **JavaFX 21+** als Library (z. B. via Maven, Gradle oder als IDE-SDK)

In IntelliJ IDEA: Neues Projekt mit JavaFX-SDK anlegen, `src/`-Inhalt
hineinkopieren, `Main` als Run-Konfiguration. VM-Options:

```
--module-path "<pfad-zu-javafx>/lib" --add-modules javafx.controls
```

## Erweiterungsideen

- **Neues Minispiel:** Klasse anlegen, `MiniGame` erben, in `MiniGameScene` zufällig auswählen.
- **Sternfeld-Logik:** in `BoardScene.FIELD_ACTION` Abfrage „Stern für 20 Münzen kaufen?" einbauen.
- **Items:** `Player` um `List<Item>` erweitern, Pilz = +1 Würfel, etc.
- **Sound:** `AudioClip` einbauen (kurze SFX), `MediaPlayer` für Musik.
- **Asset-Manager:** Bilder/Spritesheets statt Primitiven zeichnen.
- **Animationen:** Spielfigur smooth zwischen Feldern interpolieren statt teleportieren.
