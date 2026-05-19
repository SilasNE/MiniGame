package com.example.marioparty.model.items;

import com.example.marioparty.model.Board;
import com.example.marioparty.model.Player;

public final class WarpPipeItem implements GameItem {

    public static final String ID = "warp_pipe";
    private static final int PRICE = 38;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDisplayName() {
        return "Warp-Röhre";
    }

    @Override
    public int getShopPrice() {
        return PRICE;
    }

    @Override
    public GameItem copyForInventory() {
        return new WarpPipeItem();
    }

    @Override
    public String use(Player player, Board board) {
        int starKnotId = board.getStarKnotId();
        int playerKnotId = player.getBoardKnotId();
        if (playerKnotId == starKnotId) {
            return player.getName() + " steht schon beim Stern — die Warp-Röhre wirkt nicht.";
        }
        player.getInventory().remove(this);
        player.setBoardKnotId(starKnotId);
        return player.getName() + " springt in die Warp-Röhre — raus beim Stern!";
    }
}
