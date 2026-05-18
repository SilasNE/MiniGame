package com.example.marioparty.model.items;

import com.example.marioparty.model.Board;
import com.example.marioparty.model.Player;





public interface GameItem {

    String getId();

    String getDisplayName();

    int getShopPrice();


    GameItem copyForInventory();





    void use(Player player, Board board, ItemUseOutcome outcome);
}
