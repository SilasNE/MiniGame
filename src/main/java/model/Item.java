package model;

/**
 * Basisklasse für Items, die ein Spieler besitzen und einsetzen kann
 * (z. B. "Doppelwürfel", "Stern-Dieb", ...). Konkrete Items erben von
 * dieser Klasse und überschreiben use().
 */
public abstract class Item {

    protected final String name;

    protected Item(String name) {
        this.name = name;
    }

    /** Wendet den Effekt des Items auf den Spieler an. */
    public abstract void use(Player p);

    public String getName() { return name; }
}
