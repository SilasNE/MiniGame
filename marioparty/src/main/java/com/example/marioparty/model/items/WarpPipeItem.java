package com.example.marioparty.model.items;

import com.example.marioparty.model.Board;
import com.example.marioparty.model.Player;

/**
 * Teure Warp-Röhre — transportiert direkt zum Sternfeld (Knoten des Sterns), wenn man nicht schon dort ist.
 */
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
    public void use(Player player, Board board, ItemUseOutcome outcome) {
        int star = board.getStarKnotId();
        int here = player.getBoardKnotId();
        if (here == star) {
            outcome.clearTeleport();
            outcome.setMessage(player.getName() + " steht schon beim Stern — die Warp-Röhre wirkt nicht.");
            return;
        }
        player.getInventory().remove(this);
        outcome.setTeleportToKnotId(star);
        outcome.setMessage(player.getName() + " springt in die Warp-Röhre — raus beim Stern!");
    }
}
