package com.example.marioparty.minigames;

import java.util.*;

public class MemoryBotAI {

    public enum Difficulty { EASY, MEDIUM, HARD }

    private final Difficulty difficulty;
    private final Queue<MemoryCard> queue = new LinkedList<>();
    private final Map<String, List<MemoryCard>> hashMap = new HashMap<>();

    public MemoryBotAI(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void observeCard(MemoryCard card) {
        if (difficulty == Difficulty.HARD) {
            if (Math.random() <= 0.95) {
                hashMap.putIfAbsent(card.getSymbol(), new ArrayList<>());
                if (!hashMap.get(card.getSymbol()).contains(card)) {
                    hashMap.get(card.getSymbol()).add(card);
                }
            }
        } else if (difficulty == Difficulty.MEDIUM) {
            if (!queue.contains(card)) {
                queue.add(card);
                if (queue.size() > 5) queue.poll();
            }
        } else {
            if (!queue.contains(card)) {
                queue.add(card);
                if (queue.size() > 2) queue.poll();
            }
        }
    }

    public MemoryCard chooseNextCard(MemoryCard openedCard, List<MemoryCard> playableCards) {
        if (difficulty == Difficulty.HARD) {
            if (openedCard == null) {
                for (List<MemoryCard> list : hashMap.values()) {
                    if (list.size() >= 2 && playableCards.contains(list.get(0)) && playableCards.contains(list.get(1))) {
                        return list.get(0);
                    }
                }
            } else {
                if (hashMap.containsKey(openedCard.getSymbol())) {
                    for (MemoryCard c : hashMap.get(openedCard.getSymbol())) {
                        if (c != openedCard && playableCards.contains(c)) return c;
                    }
                }
            }
        } else {
            if (openedCard == null) {
                for (MemoryCard c1 : queue) {
                    for (MemoryCard c2 : queue) {
                        if (c1 != c2 && c1.getSymbol().equals(c2.getSymbol()) && playableCards.contains(c1) && playableCards.contains(c2)) {
                            return c1;
                        }
                    }
                }
            } else {
                for (MemoryCard c : queue) {
                    if (c != openedCard && c.getSymbol().equals(openedCard.getSymbol()) && playableCards.contains(c)) {
                        return c;
                    }
                }
            }
        }

        List<MemoryCard> valid = new ArrayList<>(playableCards);
        if (openedCard != null) valid.remove(openedCard);
        return valid.get(new Random().nextInt(valid.size()));
    }
}