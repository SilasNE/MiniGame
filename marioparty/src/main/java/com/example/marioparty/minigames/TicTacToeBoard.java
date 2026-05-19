package com.example.marioparty.minigames;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeBoard {

    public static final int SIZE  = 3;
    public static final int EMPTY = 0;
    public static final int X     = 1;
    public static final int O     = 2;

    private final int[][] cells = new int[SIZE][SIZE];

    public boolean place(int row, int col, int mark) {
        if (cells[row][col] != EMPTY) {
            return false;
        }
        cells[row][col] = mark;
        return true;
    }

    public void undo(int row, int col) {
        cells[row][col] = EMPTY;
    }

    public boolean isFull() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (cells[r][c] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getWinner() {
        for (int i = 0; i < SIZE; i++) {
            if (cells[i][0] != EMPTY && cells[i][0] == cells[i][1] && cells[i][1] == cells[i][2]) {
                return cells[i][0];
            }
            if (cells[0][i] != EMPTY && cells[0][i] == cells[1][i] && cells[1][i] == cells[2][i]) {
                return cells[0][i];
            }
        }
        if (cells[0][0] != EMPTY && cells[0][0] == cells[1][1] && cells[1][1] == cells[2][2]) {
            return cells[0][0];
        }
        if (cells[0][2] != EMPTY && cells[0][2] == cells[1][1] && cells[1][1] == cells[2][0]) {
            return cells[0][2];
        }
        return EMPTY;
    }

    public boolean isGameOver() {
        return getWinner() != EMPTY || isFull();
    }

    public List<int[]> getEmptyCells() {
        List<int[]> empty = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (cells[r][c] == EMPTY) {
                    empty.add(new int[]{r, c});
                }
            }
        }
        return empty;
    }

    public int[][] getWinningLine() {
        for (int i = 0; i < SIZE; i++) {
            if (cells[i][0] != EMPTY && cells[i][0] == cells[i][1] && cells[i][1] == cells[i][2]) {
                return new int[][]{{i, 0}, {i, 1}, {i, 2}};
            }
            if (cells[0][i] != EMPTY && cells[0][i] == cells[1][i] && cells[1][i] == cells[2][i]) {
                return new int[][]{{0, i}, {1, i}, {2, i}};
            }
        }
        if (cells[0][0] != EMPTY && cells[0][0] == cells[1][1] && cells[1][1] == cells[2][2]) {
            return new int[][]{{0, 0}, {1, 1}, {2, 2}};
        }
        if (cells[0][2] != EMPTY && cells[0][2] == cells[1][1] && cells[1][1] == cells[2][0]) {
            return new int[][]{{0, 2}, {1, 1}, {2, 0}};
        }
        return null;
    }
}
