package com.example.marioparty.minigames;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class BattleshipBot {

    private final Queue<int[]> targetQueue = new LinkedList<>();
    private static final Random RNG = new Random();

    public int[] findNextShot(BattleshipBoard board) {
        while (!targetQueue.isEmpty()) {
            int[] next = targetQueue.poll();
            if (board.canShoot(next[0], next[1])) {
                return next;
            }
        }

        List<int[]> candidates = new ArrayList<>();
        for (int[] cell : board.getUnshotCells()) {
            if ((cell[0] + cell[1]) % 2 == 0) {
                candidates.add(cell);
            }
        }

        if (candidates.isEmpty()) {
            candidates = board.getUnshotCells();
        }
        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(RNG.nextInt(candidates.size()));
    }

    public void onHit(int r, int c, BattleshipBoard board) {
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            if (board.canShoot(nr, nc)) {
                targetQueue.add(new int[]{nr, nc});
            }
        }
    }
}
