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

    private final List<Player> players;
    private static final int ROWS = 4;
    private static final int COLS = 4;
    private static final int PAIRS = (ROWS * COLS) / 2;

    private final MemoryCard[][] board = new MemoryCard[ROWS][COLS];
    private int currentPlayerIndex = 0;
    private Text turnText;

    private MemoryCard firstCard = null;
    private MemoryCard secondCard = null;

    private double waitTimer = 0;
    private boolean isWaiting = false;
    private boolean isMatchSuccess = false;
    private double botThinkTimer = 1.0;

    private int matchesFound = 0;
    private final Map<Player, Integer> scores = new HashMap<>();
    private final Map<Player, Text> scoreTexts = new HashMap<>();
    private final Map<Player, MemoryBotAI> botMinds = new HashMap<>();

    public MemoryGame(List<Player> players, Pane pane) {
        super(pane);
        this.players = players.size() > 2 ? new ArrayList<>(players.subList(0, 2)) : new ArrayList<>(players);
        this.participants = new ArrayList<>(this.players);
    }

    @Override
    public String getName() { return "Memory"; }

    @Override
    public String getDescription() { return "Finde die meisten Paare in diesem 1v1 Duell!"; }

    @Override
    protected void onStart() {
        initializeBoard();
        drawBoard();

        for (Player p : players) {
            scores.put(p, 0);
            if (!p.isHuman()) {
                botMinds.put(p, new MemoryBotAI(determineBotDifficulty(p)));
            }
        }
    }

    private MemoryBotAI.Difficulty determineBotDifficulty(Player bot) {
        Player human = players.stream().filter(Player::isHuman).findFirst().orElse(null);
        if (human == null) return MemoryBotAI.Difficulty.MEDIUM;

        int diff = human.getCoins() - bot.getCoins();
        if (diff > 3) return MemoryBotAI.Difficulty.HARD;
        if (diff < -3) return MemoryBotAI.Difficulty.EASY;
        return MemoryBotAI.Difficulty.MEDIUM;
    }

    private void initializeBoard() {
        List<String> symbols = new ArrayList<>();
        for (char c = 'A'; c < 'A' + PAIRS; c++) {
            symbols.add(String.valueOf(c));
            symbols.add(String.valueOf(c));
        }
        Collections.shuffle(symbols);

        int symbolIndex = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                board[row][col] = new MemoryCard(symbols.get(symbolIndex), this::handleCardClick);
                symbolIndex++;
            }
        }
    }

    private void drawBoard() {
        turnText = new Text(50, 50, "Am Zug: " + players.get(currentPlayerIndex).getName());
        turnText.setFont(Font.font("Arial", 24));
        turnText.setFill(Color.WHITE);
        pane.getChildren().add(turnText);

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            Text sText = new Text(820, 150 + (i * 100), p.getName() + ": 0");
            sText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
            sText.setFill(p.getColor());
            scoreTexts.put(p, sText);
            pane.getChildren().add(sText);
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane cardUI = board[row][col].createUI(100);
                cardUI.setLayoutX(250 + col * 120);
                cardUI.setLayoutY(150 + row * 120);
                pane.getChildren().add(cardUI);
            }
        }
    }

    public void handleCardClick(MemoryCard clickedCard) {
        if (isWaiting || clickedCard.isFlipped() || clickedCard.isMatched()) return;

        clickedCard.flip();
        botMinds.values().forEach(ai -> ai.observeCard(clickedCard));

        if (firstCard == null) {
            firstCard = clickedCard;
        } else if (secondCard == null) {
            secondCard = clickedCard;
            isWaiting = true;

            if (firstCard.getSymbol().equals(secondCard.getSymbol())) {
                firstCard.setSuccessColor();
                secondCard.setSuccessColor();
                isMatchSuccess = true;
                waitTimer = 0.8;
            } else {
                isMatchSuccess = false;
                waitTimer = 1.5;
            }
        }
    }

    private List<MemoryCard> getPlayableCards() {
        List<MemoryCard> list = new ArrayList<>();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (!board[row][col].isMatched() && !board[row][col].isFlipped()) {
                    list.add(board[row][col]);
                }
            }
        }
        return list;
    }

    @Override
    public void update(double dt, InputHandler input) {
        if (isWaiting) {
            waitTimer -= dt;
            if (waitTimer <= 0) {
                if (isMatchSuccess) processSuccess();
                else processFail();
                isWaiting = false;
                botThinkTimer = 1.0;
            }
            return;
        }

        Player currentPlayer = players.get(currentPlayerIndex);
        if (!currentPlayer.isHuman()) {
            botThinkTimer -= dt;
            if (botThinkTimer <= 0) {
                botThinkTimer = 1.0;
                List<MemoryCard> playable = getPlayableCards();
                if (!playable.isEmpty()) {
                    handleCardClick(botMinds.get(currentPlayer).chooseNextCard(firstCard, playable));
                }
            }
        }
    }

    private void processSuccess() {
        firstCard.setMatched(true);
        secondCard.setMatched(true);
        matchesFound++;

        Player currentPlayer = players.get(currentPlayerIndex);
        int newScore = scores.get(currentPlayer) + 1;
        scores.put(currentPlayer, newScore);
        scoreTexts.get(currentPlayer).setText(currentPlayer.getName() + ": " + newScore);

        if (matchesFound == PAIRS) {
            finished = true;
            winner = players.stream()
                    .max(Comparator.comparingInt(scores::get))
                    .orElse(players.get(0));
        }
        firstCard = null;
        secondCard = null;
    }

    private void processFail() {
        firstCard.unflip();
        secondCard.unflip();
        firstCard = null;
        secondCard = null;

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        turnText.setText("Am Zug: " + players.get(currentPlayerIndex).getName());
    }
}