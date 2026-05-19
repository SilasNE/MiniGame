package com.example.marioparty.minigames;

import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Player;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.*;

public class MemoryGame extends MiniGame {

    private static final int ROWS = 3;
    private static final int COLS = 6;
    private static final int PAIRS = 9;

    private final MemoryCard[][] board = new MemoryCard[ROWS][COLS];
    private int currentPlayerIndex = 0;
    private Text turnText;

    private MemoryCard firstCard = null;
    private MemoryCard secondCard = null;

    private double waitTimer = 0;
    private boolean isWaiting = false;
    private boolean isMatchSuccess = false;
    private double botThinkTimer = 1.0;
    private MemoryBotAI botMind = null;

    private int matchesFound = 0;
    private final Map<Player, Integer> scores = new HashMap<>();
    private final Map<Player, Text> scoreTexts = new HashMap<>();

    public MemoryGame(List<Player> players, Pane pane) {
        super(pane);
        this.participants = new ArrayList<>(players);
    }

    @Override
    public String getName() { return "Memory"; }

    @Override
    public String getDescription() { return "Finde 9 Paare im 1v1 Duell!"; }

    @Override
    protected void onStart() {
        initializeBoard();
        drawBoard();

        for (Player p : participants) {
            scores.put(p, 0);
            if (!p.isHuman()) {
                botMind = new MemoryBotAI(determineBotDifficulty(p));
            }
        }
    }

    private MemoryBotAI.Difficulty determineBotDifficulty(Player bot) {
        Player human = participants.stream().filter(Player::isHuman).findFirst().get();

        int diff = human.getCoins() - bot.getCoins();
        if (-3 < diff && 3 > diff) return MemoryBotAI.Difficulty.MEDIUM;
        if (diff < -3) return MemoryBotAI.Difficulty.EASY;
        return MemoryBotAI.Difficulty.HARD;
    }

    private void initializeBoard() {
        String[] imagePool = {
                "/images/banane.png", "/images/cheepcheep.png", "/images/goomba.png",
                "/images/stern.png", "/images/goldenerpilz.png", "/images/pilz.png",
                "/images/roehre.png", "/images/muenzblock.png", "/images/würfel_memory.png"
        };

        List<String> symbols = new ArrayList<>();
        for (int i = 0; i < PAIRS; i++) {
            symbols.add(imagePool[i]);
            symbols.add(imagePool[i]);
        }
        Collections.shuffle(symbols);

        int index = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                board[row][col] = new MemoryCard(symbols.get(index++), this::handleCardClick);
            }
        }
    }

    private void drawBoard() {
        turnText = new Text(50, 50, "Am Zug: " + participants.get(currentPlayerIndex).getName());
        turnText.setFont(Font.font("Arial", 24));
        turnText.setFill(Color.WHITE);
        pane.getChildren().add(turnText);

        for (int i = 0; i < participants.size(); i++) {
            Player p = participants.get(i);
            double xPos = 350 + (i * 400);
            double yPos = 580;

            Text sText = new Text(xPos, yPos, p.getName() + ": 0");
            sText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
            sText.setFill(p.getColor());
            scoreTexts.put(p, sText);
            pane.getChildren().add(sText);
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane cardUI = board[row][col].createUI(110);
                cardUI.setLayoutX(180 + col * 130);
                cardUI.setLayoutY(150 + row * 130);
                pane.getChildren().add(cardUI);
            }
        }
    }

    public void handleCardClick(MemoryCard clickedCard) {
        if (isWaiting || clickedCard.isFlipped() || clickedCard.isMatched()) return;
        if (!participants.get(currentPlayerIndex).isHuman()) return;

        clickedCard.flip();
        if (botMind != null) {
            botMind.observeCard(clickedCard);
        }
        if (firstCard == null) {
            firstCard = clickedCard;
        } else if (secondCard == null) {
            secondCard = clickedCard;
            isWaiting = true;
            checkMatch();
        }
    }

    private void checkMatch() {
        if (firstCard.getImagePath().equals(secondCard.getImagePath())) {
            firstCard.setSuccessColor();
            secondCard.setSuccessColor();
            isMatchSuccess = true;
            waitTimer = 0.8;
        } else {
            isMatchSuccess = false;
            waitTimer = 1.2;
        }
    }

    private List<MemoryCard> getPlayableCards() {
        List<MemoryCard> list = new ArrayList<>();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (!board[r][c].isMatched() && !board[r][c].isFlipped()) list.add(board[r][c]);
            }
        }
        return list;
    }

        @Override
        public void update(double deltatime, InputHandler input) {
            if (isWaiting) {
                waitTimer -= deltatime;
                if (waitTimer <= 0) {
                    if (isMatchSuccess) processSuccess();
                    else processFail();
                    isWaiting = false;
                    botThinkTimer = 0.8;
                }
                return;
            }

            Player current = participants.get(currentPlayerIndex);
            if (!current.isHuman()) {
                botThinkTimer -= deltatime;
                if (botThinkTimer <= 0) {
                    List<MemoryCard> playable = getPlayableCards();
                    if (!playable.isEmpty()) {
                        MemoryCard chosen = botMind.chooseNextCard(firstCard, playable);
                        chosen.flip();

                        if (botMind != null) {
                            botMind.observeCard(chosen);
                        }

                        if (firstCard == null) {
                            firstCard = chosen;
                            botThinkTimer = 0.6;
                        } else {
                            secondCard = chosen;
                            isWaiting = true;
                            checkMatch();
                        }
                    }
                }
            }
        }

    private void processSuccess() {
        firstCard.setMatched(true);
        secondCard.setMatched(true);
        matchesFound++;
        Player current = participants.get(currentPlayerIndex);
        int newScore = scores.get(current) + 1;
        scores.put(current, newScore);
        scoreTexts.get(current).setText(current.getName() + ": " + newScore);

        if (matchesFound == PAIRS) {
            finished = true;
            winner = participants.stream().max(Comparator.comparingInt(scores::get)).orElse(participants.get(0));
        }
        firstCard = null;
        secondCard = null;
    }

    private void processFail() {
        firstCard.unflip();
        secondCard.unflip();
        firstCard = null;
        secondCard = null;
        currentPlayerIndex = (currentPlayerIndex + 1) % participants.size();
        turnText.setText("Am Zug: " + participants.get(currentPlayerIndex).getName());
    }
}