
package com.example.marioparty.minigames;

import com.example.marioparty.Main;
import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Player;
import javafx.animation.PauseTransition;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;

public class PongGame extends MiniGame {

    private static final double PADDLE_WIDTH = 18;
    private static final double PADDLE_HEIGHT = 110;
    private static final double BALL_RADIUS = 10;

    private static final double PLAYER_SPEED = 390;
    private static final double COMPUTER_SPEED = 245;
    private static final double COMPUTER_REACTION_TIME = 0.35;

    private static final double BALL_START_SPEED_X = 340;
    private static final double BALL_START_SPEED_Y = 190;
    private static final int POINTS_TO_WIN = 5;

    private static final Random RANDOM = new Random();

    private final List<Player> players;
    private final boolean playingAgainstComputer;
    private final Player computerPlayer;

    private Rectangle leftPaddleRectangle;
    private Rectangle rightPaddleRectangle;
    private Circle ball;
    private Text scoreText;
    private Text controlsText;

    private double ballSpeedX = BALL_START_SPEED_X;
    private double ballSpeedY = BALL_START_SPEED_Y;

    private double computerReactionTimer = 0.32;   // Timer, bis der Computer reagiert
    private double computerTargetOffset = 0;    // Ziel Verfehlunng, damit Bot Fehler macht

    // Punktestand
    private int leftScore = 0;
    private int rightScore = 0;


    public PongGame(List<Player> players, Pane pane) {
        super(pane);

        // Fehlermeldung wenn Spieleranzahl nicht 1 oder 2 ist
        if (players == null || players.size() < 1 || players.size() > 2) {
            throw new IllegalArgumentException("Pong braucht 1 oder 2 Spieler");
        }
        this.players = players;
        this.participants = List.copyOf(players);
        if (players.size() == 1) {
            playingAgainstComputer = true;
            computerPlayer = new Player("Computer", Color.GRAY, false);
        } else {
            playingAgainstComputer = false;
            computerPlayer = null;
        }
    }

    @Override
    public String getName() {
        return "Pong";
    }

    @Override
    public String getDescription() {
        if (playingAgainstComputer) {
            return "Allein gegen den Computer: W/S bewegt den Schlaeger";
        } else {
            return "Zwei Spieler gegeneinander: Links W/S, rechts Pfeil hoch/runter";
        }
    }

    // UI wird aufgebaut: Titel, Punktestand, Steuerungshinweis, Mittellinie, Schlaeger und Ball
    @Override
    protected void onStart() {
        // Titel und Punktestand
        Text title = new Text(Main.WIDTH / 2.0 - 50, 80, "Pong");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        title.setFill(Color.WHITE);
        scoreText = new Text(Main.WIDTH / 2.0 - 30, 128, "0 : 0");
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        scoreText.setFill(Color.WHITE);

        // Hinweis wie Steuerung funktioniert
        String controlsMessage;
        if (playingAgainstComputer) {
            controlsMessage = "Steuerung: W und S";
        } else {
            controlsMessage = "Links: W/S   Rechts: Pfeil hoch/runter";
        }
        controlsText = new Text(Main.WIDTH / 2.0 - 275, Main.HEIGHT - 45, controlsMessage);
        controlsText.setFont(Font.font("Arial", 20));
        controlsText.setFill(Color.LIGHTGRAY);

        // Mittellinie
        Line centerLine = new Line(Main.WIDTH / 2.0, 145, Main.WIDTH / 2.0, Main.HEIGHT - 85);
        centerLine.setStroke(Color.rgb(255, 255, 255, 0.35));
        centerLine.setStrokeWidth(3);
        // Schlaeger Rechtecke
        leftPaddleRectangle = new Rectangle(
                95,
                Main.HEIGHT / 2.0 - PADDLE_HEIGHT / 2,
                PADDLE_WIDTH,
                PADDLE_HEIGHT
        );
        leftPaddleRectangle.setFill(players.get(0).getColor());

        rightPaddleRectangle = new Rectangle(
                Main.WIDTH - 113,
                Main.HEIGHT / 2.0 - PADDLE_HEIGHT / 2,
                PADDLE_WIDTH,
                PADDLE_HEIGHT
        );
        if (playingAgainstComputer) {
            rightPaddleRectangle.setFill(Color.GRAY);
        } else {
            rightPaddleRectangle.setFill(players.get(1).getColor());
        }
        // Ball
        ball = new Circle(Main.WIDTH / 2.0, Main.HEIGHT / 2.0, BALL_RADIUS, Color.WHITE);
        pane.getChildren().addAll(
                title,
                scoreText,
                controlsText,
                centerLine,
                leftPaddleRectangle,
                rightPaddleRectangle,
                ball
        );
    }

    // wird in jedem Frame aufgerufen über gameEngine
    // dt = deltaTime: Zeit seit letztem Frame
    @Override
    public void update(double dt, InputHandler input) {
        if (finished) {
            return;
        }
        movePaddles(dt, input);
        moveBall(dt);
        checkCollisions();
        checkPointScored();
        updateScoreText();
        checkWinner();
    }


    // Bewegt den linken und rechten Schlaeger
    private void movePaddles(double dt, InputHandler input) {
        moveHumanPaddle(leftPaddleRectangle, input, KeyCode.W, KeyCode.S, dt);
        if (playingAgainstComputer) {
            moveComputerPaddle(dt);
        } else {
            moveHumanPaddle(rightPaddleRectangle, input, KeyCode.UP, KeyCode.DOWN, dt);
        }
    }

    // Bewegt einen Schlaeger anhand von zwei Tasten
    private void moveHumanPaddle(Rectangle paddleRectangle, InputHandler input, KeyCode up, KeyCode down, double dt) {
        double movement = 0;

        // Bei w oder Pfeil hoch wird Bewegung nach oben berechnet
        if (input.isDown(up)) {
            movement -= PLAYER_SPEED * dt;
        }
        // Bei s oder Pfeil runter wird Bewegung nach unten berechnet
        if (input.isDown(down)) {
            movement += PLAYER_SPEED * dt;
        }
        setPaddleY(paddleRectangle, paddleRectangle.getY() + movement);
    }

    // Bewegt den Bot Rechteck mit Verzögerung und Fehler
    private void moveComputerPaddle(double dt) {
        computerReactionTimer -= dt; // Timer bis Reaktion von Computer

        // Timer abgelaufen -> neue Zielabweichung berechnen für Fehler
        if (computerReactionTimer <= 0) {
            computerReactionTimer = COMPUTER_REACTION_TIME;
            computerTargetOffset = newComputerMistake();
        }

        double targetY = ball.getCenterY() + computerTargetOffset - PADDLE_HEIGHT / 2; // Y Position für obere Kante Schlaeger
        double currentY = rightPaddleRectangle.getY(); // aktuelle Y Pos.
        double speed = COMPUTER_SPEED * dt; // Bewegung in diesem Frame


        // überprüfen ob Schläger über oder unter Ziel ist und ggf  zum Ziel bewegen nach geschwindigkeit
        if (currentY < targetY) {
            currentY += speed;
        } else if (currentY > targetY) {
            currentY -= speed;
        }
        setPaddleY(rightPaddleRectangle, currentY);
    }

    // Erzeugt Y Abweichung zwischen -80 bis + 80
    private double newComputerMistake() {
        int direction = RANDOM.nextInt(2); // 0 oder 1
        int mistake = RANDOM.nextInt(81);
        if (direction == 0) {
            return mistake;
        } else {
            return -mistake;
        }
    }

    // Bewegt den Ball
    private void moveBall(double dt) {
        ball.setCenterX(ball.getCenterX() + ballSpeedX * dt);
        ball.setCenterY(ball.getCenterY() + ballSpeedY * dt);
    }


    // Prueft Kollisionen mit Wand und Schlaegern.
    private void checkCollisions() {
        // Bei Kollision oben/unten wird Flugrichtung gespiegelt
        if (ball.getCenterY() - BALL_RADIUS <= 145 || ball.getCenterY() + BALL_RADIUS >= Main.HEIGHT - 85) {
            ballSpeedY = ballSpeedY * -1;
        }
        // Schlaeger Kollision
        if (ball.getBoundsInParent().intersects(leftPaddleRectangle.getBoundsInParent()) && ballSpeedX < 0) {
            bounceFromPaddle(1); // direction 1 -> rechts
        }
        if (ball.getBoundsInParent().intersects(rightPaddleRectangle.getBoundsInParent()) && ballSpeedX > 0) {
            bounceFromPaddle(-1); // direction -1 -> links
        }
    }

    // Laesst den Ball vom Schlaeger abprallen
    private void bounceFromPaddle(int direction) {
        double speed = ballSpeedX;
        if (speed < 0) {
            speed = speed * -1;
        }
        ballSpeedX = direction * (speed + 20);
    }

    // Schlager innerhalb des Spielbereichs halten
    private void setPaddleY(Rectangle paddleRectangle, double y) {
        double top = 145;
        double bottom = Main.HEIGHT - 85 - PADDLE_HEIGHT;
        if (y < top) {
            y = top;
        }
        if (y > bottom) {
            y = bottom;
        }
        paddleRectangle.setY(y);
    }

    // Prueft, ob Punkt gemacht wurde, aktualisiert Punktestand und setzt Ball zurueck
    private void checkPointScored() {
        if (ball.getCenterX() < 0) {
            rightScore++;
            resetBall(-1);
        }
        if (ball.getCenterX() > Main.WIDTH) {
            leftScore++;
            resetBall(1);
        }
    }

    // Setzt den Ball in die Mitte des Spiel und bewegt ihn  in die Richtung des Spielers, der Punkt gemacht hat
    private void resetBall(int direction) {
        ball.setCenterX(Main.WIDTH / 2.0);
        ball.setCenterY(Main.HEIGHT / 2.0);


        ballSpeedX = 0;
        ballSpeedY = 0;

        // 1 Sekunde warten bevor Ball sich wieder bewegt
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e ->
                {
                    ballSpeedX = direction * BALL_START_SPEED_X;
                    if (RANDOM.nextBoolean()) {
                        ballSpeedY = BALL_START_SPEED_Y;
                    } else {
                        ballSpeedY = -BALL_START_SPEED_Y;
                    }
                    computerReactionTimer = 0;
                    computerTargetOffset = 0;
                }

        );
        pause.play();

    }

    // Aktualisiert die Punkteanzeige
    private void updateScoreText() {
        scoreText.setText(leftScore + " : " + rightScore);
    }

    // Prueft, ob ein Spieler gewonnen hat
    private void checkWinner() {
        if (leftScore < POINTS_TO_WIN && rightScore < POINTS_TO_WIN) {
            return;
        }
        if (leftScore > rightScore) {
            winner = players.get(0);
        } else {
            if (playingAgainstComputer) {
                winner = computerPlayer;
            } else {
                winner = players.get(1);
            }
        }
        finished = true;
    }
}

