package model;

/**
 * Feldtypen auf dem Spielbrett.
 * Jeder Typ löst einen anderen Effekt aus (siehe GameEngine.executeFieldEffect).
 */
public enum FieldType {
    START,
    COIN_PLUS,
    COIN_MINUS,
    MINIGAME,
    STAR_SHOP,
    TELEPORT,
    EVENT
}
