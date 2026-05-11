package com.example.marioparty.model.items;

/**
 * Rückmeldung nach {@link GameItem#use} — Text für die Leiste und optional Teleport auf eine Knoten-Id.
 */
public final class ItemUseOutcome {

    private String message = "";
    /** Wenn gesetzt: Spielfigur nach Nutzen des Items auf diesen Knoten setzen (z.&nbsp;B. Warp-Röhre zum Stern). */
    private Integer teleportToKnotId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message != null ? message : "";
    }

    public Integer getTeleportToKnotId() {
        return teleportToKnotId;
    }

    public void setTeleportToKnotId(int knotId) {
        this.teleportToKnotId = knotId;
    }

    public void clearTeleport() {
        this.teleportToKnotId = null;
    }
}
