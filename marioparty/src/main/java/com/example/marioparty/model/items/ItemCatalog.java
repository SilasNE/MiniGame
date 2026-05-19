package com.example.marioparty.model.items;

import java.util.List;

public final class ItemCatalog {

    private ItemCatalog() {}

    public static List<GameItem> shopTemplates() {
        return List.of(
                new WarpPipeItem(),
                new TripleMushroomItem(),
                new CoinBlockItem()
        );
    }
}
