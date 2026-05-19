package com.example.marioparty.model.items;

import com.example.marioparty.model.Board;
import com.example.marioparty.model.Player;

public final class CoinBlockItem implements GameItem {

    public static final String ID = "coin_block";
    private static final int PRICE = 7;
    private static final int PAYOUT = 12;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDisplayName() {
        return "Münzblock";
    }

    @Override
    public int getShopPrice() {
        return PRICE;
    }

    @Override
    public GameItem copyForInventory() {
        return new CoinBlockItem();
    }

    @Override
    public String use(Player player, Board board) {
        player.addCoins(PAYOUT);
        player.getInventory().remove(this);
        return player.getName() + " schlägt einen Münzblock: +" + PAYOUT + " Münzen!";
    }
}
