package com.example.marioparty.minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleshipBoard {

    public static final int SIZE = 6;
    public static final int WATER = 0;
    public static final int SHIP = 1;
    public static final int MISS = 2;
    public static final int HIT = 3;

    private final int[][] grid = new int[SIZE][SIZE];
    private final int[][] shots = new int[SIZE][SIZE];
    private int totalShipCells = 0;
    private int hitCount = 0;

    public void placeShipsRandomly(int[] shipLengths) {
        Random rng = new Random();
        for (int len : shipLengths) {
            boolean placed = false;
            while (!placed) {
                boolean horiz = rng.nextBoolean();
                int maxR;
                int maxC;
                if (horiz) {
                    maxR = SIZE - 1;
                    maxC = SIZE - len;
                } else {
                    maxR = SIZE - len;
                    maxC = SIZE - 1;
                }
                int r = rng.nextInt(maxR + 1);
                int c = rng.nextInt(maxC + 1);
                boolean ok = true;
                for (int i = 0; i < len; i++) {
                    int row;
                    int col;
                    if (horiz) {
                        row = r;
                        col = c + i;
                    } else {
                        row = r + i;
                        col = c;
                    }
                    if (grid[row][col] != WATER) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    for (int i = 0; i < len; i++) {
                        int row;
                        int col;
                        if (horiz) {
                            row = r;
                            col = c + i;
                        } else {
                            row = r + i;
                            col = c;
                        }
                        grid[row][col] = SHIP;
                        totalShipCells++;
                    }
                    placed = true;
                }
            }
        }
    }

    public boolean canShoot(int r, int c) {
        return r >= 0 && r < SIZE && c >= 0 && c < SIZE && shots[r][c] == WATER;
    }

    public boolean shoot(int r, int c) {
        if (!canShoot(r, c)) {
            return false;
        }
        if (grid[r][c] == SHIP) {
            shots[r][c] = HIT;
            hitCount++;
            return true;
        }
        shots[r][c] = MISS;
        return false;
    }

    public boolean isDefeated() {
        return totalShipCells > 0 && hitCount >= totalShipCells;
    }

    public int getGrid(int r, int c) {
        return grid[r][c];
    }

    public int getShot(int r, int c) {
        return shots[r][c];
    }

    public boolean canPlaceShip(int r, int c, int len, boolean horiz) {
        for (int i = 0; i < len; i++) {
            int row;
            int col;
            if (horiz) {
                row = r;
                col = c + i;
            } else {
                row = r + i;
                col = c;
            }
            if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
                return false;
            }
            if (grid[row][col] != WATER) {
                return false;
            }
        }
        return true;
    }

    public void placeShip(int r, int c, int len, boolean horiz) {
        for (int i = 0; i < len; i++) {
            int row;
            int col;
            if (horiz) {
                row = r;
                col = c + i;
            } else {
                row = r + i;
                col = c;
            }
            grid[row][col] = SHIP;
            totalShipCells++;
        }
    }

    public List<int[]> getUnshotCells() {
        List<int[]> result = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (shots[r][c] == WATER) {
                    result.add(new int[]{r, c});
                }
            }
        }
        return result;
    }
}
