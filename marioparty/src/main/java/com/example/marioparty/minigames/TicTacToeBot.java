package com.example.marioparty.minigames;

import java.util.List;
import java.util.Random;

public class TicTacToeBot {

    private final int aiMark;
    private final int humanMark;

    private final double errorRate;
    private static final Random RNG = new Random();

    public TicTacToeBot(int aiMark, double errorRate) {
        this.aiMark     = aiMark;
        this.humanMark  = (aiMark == TicTacToeBoard.X) ? TicTacToeBoard.O : TicTacToeBoard.X;
        this.errorRate  = Math.clamp(errorRate, 0.0, 1.0);
    }

    public int[] findBestMove(TicTacToeBoard board) {
        List<int[]> empty = board.getEmptyCells();
        if (empty.isEmpty()) return null;

        if (RNG.nextDouble() < errorRate) {
            return empty.get(RNG.nextInt(empty.size()));
        }

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;
        for (int[] move : empty) {
            board.place(move[0], move[1], aiMark);
            int score = minimax(board, false);
            board.undo(move[0], move[1]);
            if (score > bestScore) { bestScore = score; bestMove = move; }
        }
        return bestMove;
    }

    private int minimax(TicTacToeBoard board, boolean isMaximizing) {
        int winner = board.getWinner();
        if (winner == aiMark)    return 10;
        if (winner == humanMark) return -10;
        if (board.isFull())      return 0;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int[] m : board.getEmptyCells()) {
                board.place(m[0], m[1], aiMark);
                best = Math.max(best, minimax(board, false));
                board.undo(m[0], m[1]);
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int[] m : board.getEmptyCells()) {
                board.place(m[0], m[1], humanMark);
                best = Math.min(best, minimax(board, true));
                board.undo(m[0], m[1]);
            }
            return best;
        }
    }
}
