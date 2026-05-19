package com.example.marioparty.model.items;

import java.util.ArrayList;
import java.util.List;

public class ItemCatalog {

    public static List<GameItem> getShopItems() {
        List<GameItem> items = new ArrayList<>();
        items.add(new WarpPipeItem());
        items.add(new TripleMushroomItem());
        items.add(new CoinBlockItem());
        return items;
    }
}
