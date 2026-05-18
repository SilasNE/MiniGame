package com.example.marioparty.model.items;

import com.example.marioparty.model.Board;
import com.example.marioparty.model.Player;

/**
 * Spiel-Item: im Shop kaufbar, im Inventar nutzbar. Neue Typen = neue Implementierung + Eintrag in
 * {@link ItemCatalog}.
 */
public interface GameItem {

    String getId();

    String getDisplayName();

    int getShopPrice();

    /** Eine frische Instanz fürs Inventar (nach Kauf). */
    GameItem copyForInventory();

    /**
     * Effekt beim Benutzen. Implementierung entfernt sich typischerweise selbst aus
     * {@link Player#getInventory()} (Einweg-Items).
     */
    void use(Player player, Board board, ItemUseOutcome outcome);
}
