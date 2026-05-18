package com.example.marioparty.model.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;




public final class ItemCatalog {

    private ItemCatalog() {}

    public static List<GameItem> shopTemplates() {
        List<Supplier<GameItem>> suppliers = List.of(
                WarpPipeItem::new,
                TripleMushroomItem::new,
                CoinBlockItem::new
        );
        List<GameItem> out = new ArrayList<>();
        for (Supplier<GameItem> s : suppliers) {
            out.add(s.get());
        }
        return out;
    }
}
