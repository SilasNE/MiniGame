
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
    private static final int POINTS_TO_WIN = 3;

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

    private double computerReactionTimer = 0.32;
    private double computerTargetOffset = 0;


    private int leftScore = 0;
    private int rightScore = 0;


    public PongGame(List<Player> players, Pane pane) {
        super(pane);


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


    @Override
    protected void onStart() {
        Rectangle background = new Rectangle(Main.WIDTH, Main.HEIGHT, Color.web("#0a74cf"));

        Text title = new Text(Main.WIDTH / 2.0 - 50, 80, "Pong");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        title.setFill(Color.web("#fff6a8"));
        scoreText = new Text(Main.WIDTH / 2.0 - 30, 128, "0 : 0");
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        scoreText.setFill(Color.WHITE);


        String controlsMessage;
        if (playingAgainstComputer) {
            controlsMessage = "Steuerung: W und S";
        } else {
            controlsMessage = "Links: W/S   Rechts: Pfeil hoch/runter";
        }
        controlsText = new Text(Main.WIDTH / 2.0 - 275, Main.HEIGHT - 45, controlsMessage);
        controlsText.setFont(Font.font("Arial", 20));
        controlsText.setFill(Color.LIGHTGRAY);


        Line centerLine = new Line(Main.WIDTH / 2.0, 145, Main.WIDTH / 2.0, Main.HEIGHT - 85);
        centerLine.setStroke(Color.rgb(255, 246, 168, 0.65));
        centerLine.setStrokeWidth(3);

        Rectangle playField = new Rectangle(
                95,
                145,
                Main.WIDTH - 190,
                Main.HEIGHT - 145 - 85
        );
        playField.setFill(Color.web("#63c65f"));
        playField.setStroke(Color.rgb(255, 255, 255, 0.35));
        playField.setStrokeWidth(2);

        Rectangle playFieldBorder = new Rectangle(
                95,
                145,
                Main.WIDTH - 190,
                Main.HEIGHT - 145 - 85
        );
        playFieldBorder.setFill(Color.TRANSPARENT);
        playFieldBorder.setStroke(Color.web("#fff6a8"));
        playFieldBorder.setStrokeWidth(5);

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
            rightPaddleRectangle.setFill(Color.web("#d9d9d9"));
        } else {
            rightPaddleRectangle.setFill(players.get(1).getColor());
        }

        ball = new Circle(Main.WIDTH / 2.0, Main.HEIGHT / 2.0, BALL_RADIUS, Color.web("#fff6a8"));
        pane.getChildren().addAll(
                background,
                playField,
                title,
                scoreText,
                controlsText,
                centerLine,
                leftPaddleRectangle,
                rightPaddleRectangle,
                playFieldBorder,
                ball
        );
    }



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



    private void movePaddles(double dt, InputHandler input) {
        moveHumanPaddle(leftPaddleRectangle, input, KeyCode.W, KeyCode.S, dt);
        if (playingAgainstComputer) {
            moveComputerPaddle(dt);
        } else {
            moveHumanPaddle(rightPaddleRectangle, input, KeyCode.UP, KeyCode.DOWN, dt);
        }
    }


    private void moveHumanPaddle(Rectangle paddleRectangle, InputHandler input, KeyCode up, KeyCode down, double dt) {
        double movement = 0;


        if (input.isDown(up)) {
            movement -= PLAYER_SPEED * dt;
        }

        if (input.isDown(down)) {
            movement += PLAYER_SPEED * dt;
        }
        setPaddleY(paddleRectangle, paddleRectangle.getY() + movement);
    }


    private void moveComputerPaddle(double dt) {
        computerReactionTimer -= dt;


        if (computerReactionTimer <= 0) {
            computerReactionTimer = COMPUTER_REACTION_TIME;
            computerTargetOffset = newComputerMistake();
        }

        double targetY = ball.getCenterY() + computerTargetOffset - PADDLE_HEIGHT / 2;
        double currentY = rightPaddleRectangle.getY();
        double speed = COMPUTER_SPEED * dt;



        if (currentY < targetY) {
            currentY += speed;
        } else if (currentY > targetY) {
            currentY -= speed;
        }
        setPaddleY(rightPaddleRectangle, currentY);
    }


    private double newComputerMistake() {
        int direction = RANDOM.nextInt(2);
        int mistake = RANDOM.nextInt(81);
        if (direction == 0) {
            return mistake;
        } else {
            return -mistake;
        }
    }


    private void moveBall(double dt) {
        ball.setCenterX(ball.getCenterX() + ballSpeedX * dt);
        ball.setCenterY(ball.getCenterY() + ballSpeedY * dt);
    }



    private void checkCollisions() {

        if (ball.getCenterY() - BALL_RADIUS <= 145 || ball.getCenterY() + BALL_RADIUS >= Main.HEIGHT - 85) {
            ballSpeedY = ballSpeedY * -1;
        }

        if (ball.getBoundsInParent().intersects(leftPaddleRectangle.getBoundsInParent()) && ballSpeedX < 0) {
            bounceFromPaddle(1);
        }
        if (ball.getBoundsInParent().intersects(rightPaddleRectangle.getBoundsInParent()) && ballSpeedX > 0) {
            bounceFromPaddle(-1);
        }
    }


    private void bounceFromPaddle(int direction) {
        double speed = ballSpeedX;
        if (speed < 0) {
            speed = speed * -1;
        }
        ballSpeedX = direction * (speed + 50);
    }


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


    private void resetBall(int direction) {
        ball.setCenterX(Main.WIDTH / 2.0);
        ball.setCenterY(Main.HEIGHT / 2.0);


        ballSpeedX = 0;
        ballSpeedY = 0;


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


    private void updateScoreText() {
        scoreText.setText(leftScore + " : " + rightScore);
    }


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
