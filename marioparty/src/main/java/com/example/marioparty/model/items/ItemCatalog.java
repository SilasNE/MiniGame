package com.example.marioparty.model.items;

import java.util.List;

public class ItemCatalog {

    public static List<GameItem> getShopItems() {
        return List.of(
                new WarpPipeItem(),
                new TripleMushroomItem(),
                new CoinBlockItem()
        );
    }
}
