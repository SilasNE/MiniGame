package com.example.marioparty.minigames;

/**
 * Minimax-Bot fuer TicTacToe. Spielt perfekt.
 * Bezug zur Vorlesung: Rekursion, Tiefensuche, Backtracking.
 */
public class TicTacToeAi {

    private final int aiMark;
    private final int humanMark;

    public TicTacToeAi(int aiMark) {
        this.aiMark    = aiMark;
        this.humanMark = (aiMark == TicTacToeBoard.X) ? TicTacToeBoard.O : TicTacToeBoard.X;
    }

    public int[] findBestMove(TicTacToeBoard board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;
        for (int[] move : board.getEmptyCells()) {
            board.place(move[0], move[1], aiMark);
            int score = minimax(board, 0, false);
            board.undo(move[0], move[1]);
            if (score > bestScore) { bestScore = score; bestMove = move; }
        }
        return bestMove;
    }

    private int minimax(TicTacToeBoard board, int depth, boolean isMaximizing) {
        int winner = board.getWinner();
        if (winner == aiMark)    return 10 - depth;
        if (winner == humanMark) return depth - 10;
        if (board.isFull())      return 0;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int[] m : board.getEmptyCells()) {
                board.place(m[0], m[1], aiMark);
                best = Math.max(best, minimax(board, depth + 1, false));
                board.undo(m[0], m[1]);
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int[] m : board.getEmptyCells()) {
                board.place(m[0], m[1], humanMark);
                best = Math.min(best, minimax(board, depth + 1, true));
                board.undo(m[0], m[1]);
            }
            return best;
        }
    }
}
