package test;/*
 * MarioPartyMini.java
 * ====================
 * Eine minimale Mario-Party-artige Anwendung mit JavaFX.
 *
 *  - Spielfeld: gerichteter Graph mit 14 Feldern, einer Kreuzung (Junction)
 *    und einer Abkürzung
 *  - 4 Spieler (Mario, Luigi, Peach, Yoshi), Münzen + Sterne
 *  - 3 Minispiele:
 *      1) Schnellklicker (5 Sek so oft wie möglich klicken)
 *      2) Zahlen-Gedächtnis (5er-Zahlenfolge merken & wiedergeben)
 *      3) Glücksdice (3 Würfel, Summe = Münzen)
 *  - Sieger: meiste Sterne (Tiebreak: meiste Münzen) nach 5 Runden
 *
 * Kompilieren & Ausführen (mit lokaler JavaFX SDK, z. B. Version 21):
 *
 *   javac --module-path "PFAD/zu/javafx-sdk/lib" \
 *         --add-modules javafx.controls MarioPartyMini.java
 *
 *   java  --module-path "PFAD/zu/javafx-sdk/lib" \
 *         --add-modules javafx.controls MarioPartyMini
 *
 * (Auf Maven/Gradle-Projekten reicht das Hinzufügen der javafx.controls
 *  Abhängigkeit.)
 */

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Consumer;

public class MarioPartyMini extends Application {

    // =====================================================================
    // ENUMS / DATENSTRUKTUREN
    // =====================================================================

    enum FieldType {
        START   (Color.LIGHTGRAY,    "S"),
        NORMAL  (Color.WHITE,        ""),
        BLUE    (Color.LIGHTSKYBLUE, "+3"),
        RED     (Color.LIGHTCORAL,   "-3"),
        STAR    (Color.GOLD,         "★"),
        MINI    (Color.LIGHTGREEN,   "MG"),
        JUNCTION(Color.ORANGE,       "?");

        final Color color;
        final String label;
        FieldType(Color c, String l) { this.color = c; this.label = l; }
    }

    /** Knoten im Spielfeldgraphen. */
    static class Field {
        final int id;
        final double x, y;
        final FieldType type;
        final List<Field> next = new ArrayList<>();
        Circle circle;
        Text  labelText;

        Field(int id, double x, double y, FieldType type) {
            this.id = id; this.x = x; this.y = y; this.type = type;
        }
    }

    static class Player {
        final String name;
        final Color  color;
        int   coins  = 10;
        int   stars  = 0;
        Field currentField;
        Circle token;

        Player(String name, Color color) { this.name = name; this.color = color; }
    }

    // =====================================================================
    // SPIELZUSTAND
    // =====================================================================

    private final List<Field>  board   = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private int activePlayerIndex = 0;
    private int round             = 1;
    private final int MAX_ROUNDS  = 5;
    private final int STAR_COST   = 20;
    private final Random rng = new Random();

    // UI-Referenzen
    private Pane     boardPane;
    private Label    statusLabel;
    private Label    diceLabel;
    private Button   rollButton;
    private TextArea logArea;
    private VBox     playerInfoBox;
    private Stage    primaryStage;

    // =====================================================================
    // EINSTIEGSPUNKT
    // =====================================================================

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        initPlayers();
        buildBoard();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b1d4a;");

        // ---------- Top: Status + Würfelanzeige ----------
        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        statusLabel.setTextFill(Color.WHITE);

        diceLabel = new Label("Würfel: -");
        diceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        diceLabel.setTextFill(Color.YELLOW);

        HBox topBox = new HBox(30, statusLabel, diceLabel);
        topBox.setPadding(new Insets(12));
        topBox.setAlignment(Pos.CENTER);
        root.setTop(topBox);

        // ---------- Center: Spielfeld ----------
        boardPane = new Pane();
        boardPane.setPrefSize(780, 660);
        boardPane.setStyle("-fx-background-color: #cfe8a4; -fx-background-radius: 12;");
        drawBoard();
        StackPane centerWrap = new StackPane(boardPane);
        centerWrap.setPadding(new Insets(10));
        root.setCenter(centerWrap);

        // ---------- Right: Spielerinfos ----------
        playerInfoBox = new VBox(8);
        playerInfoBox.setPadding(new Insets(12));
        playerInfoBox.setPrefWidth(240);
        playerInfoBox.setStyle("-fx-background-color: rgba(255,255,255,0.92);"
                + " -fx-background-radius: 12;");
        VBox rightWrap = new VBox(playerInfoBox);
        rightWrap.setPadding(new Insets(10));
        root.setRight(rightWrap);

        // ---------- Bottom: Steuerung + Log ----------
        rollButton = new Button("Würfeln");
        rollButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        rollButton.setPrefWidth(160);
        rollButton.setOnAction(e -> rollDice());

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(5);
        logArea.setStyle("-fx-control-inner-background: #1a1530;"
                + " -fx-text-fill: white; -fx-font-family: 'Consolas','monospace';");

        VBox bottom = new VBox(8, rollButton, logArea);
        bottom.setPadding(new Insets(10));
        bottom.setAlignment(Pos.CENTER);
        root.setBottom(bottom);

        // ---------- Initialisierung ----------
        placeAllTokensAtStart();
        updatePlayerInfo();
        updateStatus();

        Scene scene = new Scene(root, 1120, 880);
        stage.setScene(scene);
        stage.setTitle("Mini Mario Party");
        stage.show();

        log("Willkommen bei Mini Mario Party!");
        log("Ziel: Sammle Sterne. " + MAX_ROUNDS + " Runden, dann gewinnt der "
                + "Spieler mit den meisten Sternen.");
        log(activePlayer().name + " ist am Zug. Klicke 'Würfeln'.");
    }

    private Player activePlayer() { return players.get(activePlayerIndex); }

    // =====================================================================
    // INITIALISIERUNG
    // =====================================================================

    private void initPlayers() {
        players.add(new Player("Mario", Color.RED));
        players.add(new Player("Luigi", Color.LIMEGREEN));
        players.add(new Player("Peach", Color.HOTPINK));
        players.add(new Player("Yoshi", Color.LIGHTSEAGREEN));
    }

    /**
     * Baut den Graphen.
     *
     * Hauptring (0..11) im Uhrzeigersinn. An Feld 3 verzweigt sich der Weg:
     *   - Hauptpfad:   3 -> 4 -> 5 -> 6(★) -> 7 -> 8 -> 9
     *   - Abkürzung:   3 -> 12 -> 13 -> 9
     */
    private void buildBoard() {
        double[][] coords = {
                {  90, 380 },   // 0  START
                { 170, 240 },   // 1  BLUE
                { 290, 160 },   // 2  NORMAL
                { 410, 140 },   // 3  JUNCTION
                { 530, 160 },   // 4  RED
                { 650, 240 },   // 5  BLUE
                { 720, 380 },   // 6  STAR
                { 650, 510 },   // 7  NORMAL
                { 530, 580 },   // 8  BLUE
                { 410, 600 },   // 9  MINI
                { 290, 580 },   // 10 NORMAL
                { 170, 510 },   // 11 RED
                { 410, 280 },   // 12 NORMAL  (Abkürzung)
                { 410, 440 }    // 13 BLUE    (Abkürzung)
        };
        FieldType[] types = {
                FieldType.START,
                FieldType.BLUE,
                FieldType.NORMAL,
                FieldType.JUNCTION,
                FieldType.RED,
                FieldType.BLUE,
                FieldType.STAR,
                FieldType.NORMAL,
                FieldType.BLUE,
                FieldType.MINI,
                FieldType.NORMAL,
                FieldType.RED,
                FieldType.NORMAL,
                FieldType.BLUE
        };
        for (int i = 0; i < coords.length; i++) {
            board.add(new Field(i, coords[i][0], coords[i][1], types[i]));
        }

        // Kanten (gerichtet)
        connect(0, 1);
        connect(1, 2);
        connect(2, 3);
        connect(3, 4);    // Hauptpfad
        connect(3, 12);   // Abkürzung
        connect(4, 5);
        connect(5, 6);
        connect(6, 7);
        connect(7, 8);
        connect(8, 9);
        connect(9, 10);
        connect(10, 11);
        connect(11, 0);
        connect(12, 13);
        connect(13, 9);   // Abkürzung mündet bei 9
    }

    private void connect(int from, int to) {
        board.get(from).next.add(board.get(to));
    }

    // =====================================================================
    // SPIELFELD ZEICHNEN
    // =====================================================================

    private void drawBoard() {
        // Kanten
        Set<String> drawn = new HashSet<>();
        for (Field f : board) {
            for (Field n : f.next) {
                String key = Math.min(f.id, n.id) + "_" + Math.max(f.id, n.id);
                if (drawn.contains(key)) continue;
                drawn.add(key);
                Line line = new Line(f.x, f.y, n.x, n.y);
                line.setStroke(Color.web("#6b4f2a"));
                line.setStrokeWidth(7);
                line.setStrokeLineCap(StrokeLineCap.ROUND);
                boardPane.getChildren().add(line);
            }
        }
        // Felder
        for (Field f : board) {
            Circle c = new Circle(f.x, f.y, 26);
            c.setFill(f.type.color);
            c.setStroke(Color.BLACK);
            c.setStrokeWidth(2);
            f.circle = c;
            boardPane.getChildren().add(c);

            Text t = new Text(f.type.label);
            t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            // zentrieren
            t.setX(f.x - t.getLayoutBounds().getWidth() / 2);
            t.setY(f.y + 5);
            f.labelText = t;
            boardPane.getChildren().add(t);
        }
    }

    private void placeAllTokensAtStart() {
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            p.currentField = board.get(0);
            double[] off = tokenOffset(i);
            Circle token = new Circle(p.currentField.x + off[0],
                    p.currentField.y + off[1], 10);
            token.setFill(p.color);
            token.setStroke(Color.BLACK);
            token.setStrokeWidth(2);
            p.token = token;
            boardPane.getChildren().add(token);
        }
    }

    /** Liefert Offset, damit sich Spielfiguren auf demselben Feld nicht überlappen. */
    private double[] tokenOffset(int playerIdx) {
        double angle = playerIdx * (Math.PI / 2);
        return new double[] { Math.cos(angle) * 16, Math.sin(angle) * 16 };
    }

    // =====================================================================
    // WÜRFELN UND BEWEGEN
    // =====================================================================

    private void rollDice() {
        rollButton.setDisable(true);
        final int finalRoll = rng.nextInt(6) + 1;

        // Animation: Würfel zappelt 0.7s
        Timeline anim = new Timeline();
        for (int i = 0; i < 10; i++) {
            final int frame = i;
            anim.getKeyFrames().add(new KeyFrame(Duration.millis(60.0 * frame),
                    e -> diceLabel.setText("Würfel: " + (rng.nextInt(6) + 1))));
        }
        anim.getKeyFrames().add(new KeyFrame(Duration.millis(700), e -> {
            diceLabel.setText("Würfel: " + finalRoll);
            log(activePlayer().name + " würfelt eine " + finalRoll + ".");
            movePlayer(activePlayer(), finalRoll);
        }));
        anim.play();
    }

    /** Bewegt den Spieler rekursiv Schritt für Schritt. */
    private void movePlayer(Player p, int steps) {
        if (steps <= 0) {
            onLandedOn(p, p.currentField);
            return;
        }
        Field current = p.currentField;
        if (current.next.size() == 1) {
            stepTo(p, current.next.get(0), () -> movePlayer(p, steps - 1));
        } else if (current.next.size() > 1) {
            askDirection(p, current.next,
                    chosen -> stepTo(p, chosen, () -> movePlayer(p, steps - 1)));
        } else {
            // Sackgasse (sollte nicht vorkommen)
            onLandedOn(p, p.currentField);
        }
    }

    private void stepTo(Player p, Field target, Runnable after) {
        p.currentField = target;
        int idx = players.indexOf(p);
        double[] off = tokenOffset(idx);
        double newX = target.x + off[0];
        double newY = target.y + off[1];

        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(p.token.centerXProperty(), p.token.getCenterX()),
                        new KeyValue(p.token.centerYProperty(), p.token.getCenterY())),
                new KeyFrame(Duration.millis(280),
                        new KeyValue(p.token.centerXProperty(), newX),
                        new KeyValue(p.token.centerYProperty(), newY))
        );
        tl.setOnFinished(e -> after.run());
        tl.play();
    }

    private void askDirection(Player p, List<Field> options, Consumer<Field> onChoose) {
        Stage dlg = new Stage();
        dlg.setTitle("Kreuzung");
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setOnCloseRequest(Event::consume);

        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER);

        Label l = new Label(p.name + " ist an einer Kreuzung.\nWelchen Weg willst du gehen?");
        l.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        l.setWrapText(true);
        box.getChildren().add(l);

        for (Field opt : options) {
            Button b = new Button("→ Feld " + opt.id + "  (" + opt.type.name() + ")");
            b.setMaxWidth(Double.MAX_VALUE);
            b.setFont(Font.font("Arial", 13));
            b.setOnAction(e -> { dlg.close(); onChoose.accept(opt); });
            box.getChildren().add(b);
        }

        dlg.setScene(new Scene(box, 320, 200));
        dlg.show();
    }

    // =====================================================================
    // FELDEFFEKT
    // =====================================================================

    private void onLandedOn(Player p, Field f) {
        log(p.name + " landet auf Feld " + f.id + " (" + f.type.name() + ").");
        switch (f.type) {
            case BLUE:
                p.coins += 3;
                log(p.name + " bekommt 3 Münzen (gesamt: " + p.coins + ").");
                break;
            case RED:
                int loss = Math.min(3, p.coins);
                p.coins -= loss;
                log(p.name + " verliert " + loss + " Münzen (gesamt: " + p.coins + ").");
                break;
            case STAR:
                if (p.coins >= STAR_COST) {
                    p.coins -= STAR_COST;
                    p.stars++;
                    log("*** " + p.name + " kauft einen STERN für " + STAR_COST
                            + " Münzen! Sterne: " + p.stars + " ***");
                } else {
                    log(p.name + " hat nur " + p.coins + " Münzen — zu wenig "
                            + "für einen Stern (" + STAR_COST + ").");
                }
                break;
            case MINI:
                log(">>> Minispiel-Feld! " + p.name + " darf ein Minispiel wählen.");
                updatePlayerInfo();
                startMiniGameMenu();
                return; // Zugende erst nach dem Minispiel
            default:
                break; // START, NORMAL, JUNCTION: passiert nichts
        }
        endTurn();
    }

    // =====================================================================
    // ZUG-/RUNDENWECHSEL
    // =====================================================================

    private void endTurn() {
        updatePlayerInfo();
        activePlayerIndex++;
        if (activePlayerIndex >= players.size()) {
            activePlayerIndex = 0;
            round++;
            log("================ Runde " + round + " ================");
            if (round > MAX_ROUNDS) {
                endGame();
                return;
            }
        }
        updateStatus();
        rollButton.setDisable(false);
    }

    private void endGame() {
        Player winner = players.stream()
                .max(Comparator.<Player>comparingInt(pp -> pp.stars)
                        .thenComparingInt(pp -> pp.coins))
                .orElseThrow();

        StringBuilder sb = new StringBuilder("Endstand:\n\n");
        for (Player p : players) {
            sb.append(p.name).append(":  ")
                    .append(p.stars).append(" Stern(e),  ")
                    .append(p.coins).append(" Münzen\n");
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Spielende!");
        a.setHeaderText("Gewinner: " + winner.name + "!");
        a.setContentText(sb.toString());
        a.showAndWait();

        rollButton.setDisable(true);
        log("Spiel beendet. Sieger: " + winner.name + ".");
    }

    // =====================================================================
    // MINISPIEL-AUSWAHL
    // =====================================================================

    private void startMiniGameMenu() {
        Stage dlg = new Stage();
        dlg.setTitle("Minispiel wählen");
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setOnCloseRequest(Event::consume);

        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER);

        Label l = new Label(activePlayer().name + ", wähle ein Minispiel:");
        l.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        box.getChildren().add(l);

        Button b1 = new Button("1) Schnellklicker  (5 Sek)");
        Button b2 = new Button("2) Zahlen-Gedächtnis  (5 Zahlen)");
        Button b3 = new Button("3) Glücksdice  (3 Würfel)");
        for (Button b : new Button[]{b1, b2, b3}) {
            b.setMaxWidth(Double.MAX_VALUE);
            b.setFont(Font.font("Arial", 14));
        }
        b1.setOnAction(e -> { dlg.close(); playClickRace();  });
        b2.setOnAction(e -> { dlg.close(); playMemory();     });
        b3.setOnAction(e -> { dlg.close(); playLuckyDice();  });

        box.getChildren().addAll(b1, b2, b3);
        dlg.setScene(new Scene(box, 320, 230));
        dlg.show();
    }

    // =====================================================================
    // MINISPIEL 1: SCHNELLKLICKER
    // =====================================================================

    private void playClickRace() {
        Stage dlg = new Stage();
        dlg.setTitle("Schnellklicker");
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setOnCloseRequest(Event::consume);

        VBox box = new VBox(12);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER);

        Label info     = new Label("Klicke in 5 Sekunden so oft wie möglich!\n"
                + "1 Klick = 1 Münze.");
        info.setFont(Font.font("Arial", 14));
        info.setWrapText(true);
        info.setTextAlignment(TextAlignment.CENTER);

        Label timeLbl   = new Label("Zeit: 5.0s");
        Label clicksLbl = new Label("Klicks: 0");
        timeLbl.setFont(Font.font("Arial", 16));
        clicksLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        final int[] clicks = {0};
        Button clickBtn = new Button("KLICK!");
        clickBtn.setPrefSize(200, 90);
        clickBtn.setStyle("-fx-font-size: 22; -fx-font-weight: bold;"
                + " -fx-background-color: #ffcc33;");
        clickBtn.setDisable(true);
        clickBtn.setOnAction(e -> {
            clicks[0]++;
            clicksLbl.setText("Klicks: " + clicks[0]);
        });

        Button startBtn = new Button("Start");
        startBtn.setFont(Font.font("Arial", 14));
        startBtn.setOnAction(e -> {
            startBtn.setDisable(true);
            clickBtn.setDisable(false);
            long startTime = System.currentTimeMillis();

            Timeline timer = new Timeline(new KeyFrame(Duration.millis(50), ev -> {
                double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
                double remain  = Math.max(0, 5.0 - elapsed);
                timeLbl.setText(String.format(Locale.GERMAN, "Zeit: %.1fs", remain));
            }));
            timer.setCycleCount(Animation.INDEFINITE);
            timer.play();

            PauseTransition end = new PauseTransition(Duration.seconds(5));
            end.setOnFinished(ev -> {
                timer.stop();
                clickBtn.setDisable(true);
                int reward = clicks[0];
                activePlayer().coins += reward;
                log(">> Schnellklicker: " + activePlayer().name + " = "
                        + clicks[0] + " Klicks → +" + reward + " Münzen");

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setHeaderText("Fertig!");
                a.setContentText(activePlayer().name + " hat " + clicks[0]
                        + " Klicks geschafft und bekommt " + reward + " Münzen.");
                a.showAndWait();
                dlg.close();
                updatePlayerInfo();
                endTurn();
            });
            end.play();
        });

        box.getChildren().addAll(info, timeLbl, clicksLbl, clickBtn, startBtn);
        dlg.setScene(new Scene(box, 360, 340));
        dlg.show();
    }

    // =====================================================================
    // MINISPIEL 2: ZAHLEN-GEDÄCHTNIS
    // =====================================================================

    private void playMemory() {
        Stage dlg = new Stage();
        dlg.setTitle("Zahlen-Gedächtnis");
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setOnCloseRequest(Event::consume);

        // Erzeuge 5 verschiedene Zahlen aus 1..9
        List<Integer> pool = new ArrayList<>();
        for (int i = 1; i <= 9; i++) pool.add(i);
        Collections.shuffle(pool, rng);
        final List<Integer> sequence = new ArrayList<>(pool.subList(0, 5));

        Label info = new Label("Merke dir diese Zahlenfolge (5 Sek)!");
        info.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label seqLbl = new Label(sequence.toString());
        seqLbl.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        seqLbl.setTextFill(Color.DARKBLUE);

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        Label progress = new Label("Eingabe: -");
        progress.setFont(Font.font("Arial", 14));

        final List<Integer> userInput = new ArrayList<>();
        Button[] btns = new Button[9];
        for (int i = 1; i <= 9; i++) {
            final int v = i;
            Button b = new Button(String.valueOf(v));
            b.setPrefSize(55, 55);
            b.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
            b.setDisable(true);
            b.setOnAction(e -> {
                userInput.add(v);
                progress.setText("Eingabe: " + userInput);
                b.setDisable(true);

                if (userInput.size() == sequence.size()) {
                    // Auswertung
                    int correct = 0;
                    for (int k = 0; k < sequence.size(); k++) {
                        if (sequence.get(k).equals(userInput.get(k))) correct++;
                        else break;
                    }
                    int reward = correct * 4;
                    if (correct == sequence.size()) reward += 5; // Perfekt-Bonus
                    activePlayer().coins += reward;
                    log(">> Gedächtnis: " + activePlayer().name + " = "
                            + correct + "/" + sequence.size() + " richtig → +"
                            + reward + " Münzen");

                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setHeaderText("Auflösung");
                    a.setContentText("Korrekte Folge: " + sequence
                            + "\nDeine Eingabe:  " + userInput
                            + "\nRichtig (in Reihenfolge): " + correct + "/" + sequence.size()
                            + "\nBelohnung: " + reward + " Münzen");
                    a.showAndWait();
                    dlg.close();
                    updatePlayerInfo();
                    endTurn();
                }
            });
            btns[i - 1] = b;
            grid.add(b, (i - 1) % 3, (i - 1) / 3);
        }

        VBox box = new VBox(12, info, seqLbl, grid, progress);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER);
        dlg.setScene(new Scene(box, 380, 420));
        dlg.show();

        // Nach 5 Sekunden Folge verstecken & Buttons freischalten
        PauseTransition pt = new PauseTransition(Duration.seconds(5));
        pt.setOnFinished(e -> {
            seqLbl.setText("? ? ? ? ?");
            seqLbl.setTextFill(Color.DARKRED);
            info.setText("Klicke die Zahlen jetzt in der richtigen Reihenfolge!");
            for (Button b : btns) b.setDisable(false);
        });
        pt.play();
    }

    // =====================================================================
    // MINISPIEL 3: GLÜCKSDICE
    // =====================================================================

    private void playLuckyDice() {
        Stage dlg = new Stage();
        dlg.setTitle("Glücksdice");
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setOnCloseRequest(Event::consume);

        Label info = new Label("Würfle 3 Würfel — die Summe ist deine Belohnung!");
        info.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        info.setWrapText(true);
        info.setTextAlignment(TextAlignment.CENTER);

        HBox dice = new HBox(15);
        dice.setAlignment(Pos.CENTER);
        Label[] dArr = new Label[3];
        for (int i = 0; i < 3; i++) {
            Label d = new Label("?");
            d.setFont(Font.font("Arial", FontWeight.BOLD, 38));
            d.setStyle("-fx-background-color: white; -fx-border-color: black;"
                    + " -fx-border-width: 2; -fx-padding: 10;");
            d.setMinSize(70, 70);
            d.setAlignment(Pos.CENTER);
            dArr[i] = d;
            dice.getChildren().add(d);
        }
        Label sumLbl = new Label("Summe: -");
        sumLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Button rollBtn = new Button("Würfeln!");
        rollBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        rollBtn.setOnAction(e -> {
            rollBtn.setDisable(true);
            final int v1 = rng.nextInt(6) + 1;
            final int v2 = rng.nextInt(6) + 1;
            final int v3 = rng.nextInt(6) + 1;

            // Animation
            Timeline anim = new Timeline();
            for (int i = 0; i < 12; i++) {
                anim.getKeyFrames().add(new KeyFrame(Duration.millis(60.0 * i), ev -> {
                    dArr[0].setText("" + (rng.nextInt(6) + 1));
                    dArr[1].setText("" + (rng.nextInt(6) + 1));
                    dArr[2].setText("" + (rng.nextInt(6) + 1));
                }));
            }
            anim.getKeyFrames().add(new KeyFrame(Duration.millis(800), ev -> {
                dArr[0].setText("" + v1);
                dArr[1].setText("" + v2);
                dArr[2].setText("" + v3);
                int sum = v1 + v2 + v3;
                sumLbl.setText("Summe: " + sum + "  →  +" + sum + " Münzen");
                activePlayer().coins += sum;
                log(">> Glücksdice: " + activePlayer().name + " = "
                        + v1 + "+" + v2 + "+" + v3 + " = " + sum
                        + " → +" + sum + " Münzen");

                PauseTransition wait = new PauseTransition(Duration.seconds(1.6));
                wait.setOnFinished(ev2 -> {
                    dlg.close();
                    updatePlayerInfo();
                    endTurn();
                });
                wait.play();
            }));
            anim.play();
        });

        VBox box = new VBox(14, info, dice, sumLbl, rollBtn);
        box.setPadding(new Insets(18));
        box.setAlignment(Pos.CENTER);
        dlg.setScene(new Scene(box, 360, 280));
        dlg.show();
    }

    // =====================================================================
    // UI-AKTUALISIERUNG
    // =====================================================================

    private void updateStatus() {
        statusLabel.setText("Runde " + round + "/" + MAX_ROUNDS
                + "  —  " + activePlayer().name + " ist am Zug");
        // Aktuellen Spieler hervorheben
        for (Player p : players) {
            if (p.token != null) {
                p.token.setStrokeWidth(p == activePlayer() ? 4 : 2);
                p.token.setStroke(p == activePlayer() ? Color.YELLOW : Color.BLACK);
            }
        }
    }

    private void updatePlayerInfo() {
        playerInfoBox.getChildren().clear();
        Label title = new Label("Spieler");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        playerInfoBox.getChildren().add(title);

        Label legend = new Label("Stern kostet " + STAR_COST + " Münzen");
        legend.setFont(Font.font("Arial", FontPosture.ITALIC, 11));
        playerInfoBox.getChildren().add(legend);
        playerInfoBox.getChildren().add(new Separator());

        for (Player p : players) {
            HBox row = new HBox(8);
            Circle dot = new Circle(9, p.color);
            dot.setStroke(Color.BLACK);
            Label name = new Label(p.name);
            name.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            row.getChildren().addAll(dot, name);
            row.setAlignment(Pos.CENTER_LEFT);
            if (p == activePlayer()) {
                name.setStyle("-fx-text-fill: #146d2c;");
                row.setStyle("-fx-background-color: #ffffaa; -fx-background-radius: 6;");
            }

            Label stats = new Label("   Münzen: " + p.coins
                    + "    Sterne: " + p.stars
                    + "    Feld: " + p.currentField.id);
            stats.setFont(Font.font("Arial", 12));

            VBox playerBox = new VBox(2, row, stats);
            playerBox.setPadding(new Insets(4));
            playerInfoBox.getChildren().add(playerBox);
        }

        Separator sep = new Separator();
        playerInfoBox.getChildren().add(sep);

        Label legend2 = new Label("Legende:\n"
                + "  S = Start\n"
                + "  +3 = Münzen\n"
                + "  -3 = Münzen\n"
                + "  ★ = Stern kaufen\n"
                + "  MG = Minispiel\n"
                + "  ? = Kreuzung");
        legend2.setFont(Font.font("Arial", 12));
        playerInfoBox.getChildren().add(legend2);
    }

    private void log(String msg) {
        logArea.appendText(msg + "\n");
    }

    // =====================================================================
    public static void main(String[] args) { launch(args); }
}
