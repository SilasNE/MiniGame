package com.example.marioparty.model.items;

import com.example.marioparty.model.Board;
import com.example.marioparty.model.Player;


public final class TripleMushroomItem implements GameItem {

    public static final String ID = "triple_mushroom";
    private static final int PRICE = 12;
    private static final int BONUS = 3;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDisplayName() {
        return "Dreifach-Pilz";
    }

    @Override
    public int getShopPrice() {
        return PRICE;
    }

    @Override
    public GameItem copyForInventory() {
        return new TripleMushroomItem();
    }

    @Override
    public void use(Player player, Board board, ItemUseOutcome outcome) {
        player.addRollBonus(BONUS);
        player.getInventory().remove(this);
        outcome.clearTeleport();
        outcome.setMessage(player.getName() + " isst einen Dreifach-Pilz: +" + BONUS
                + " auf den nächsten Wurf!");
    }
}
