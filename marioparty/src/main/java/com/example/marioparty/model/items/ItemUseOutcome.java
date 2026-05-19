package com.example.marioparty.model.items;

public final class ItemUseOutcome {

    private static final int NO_TELEPORT = -1;

    private String message = "";
    private int teleportToKnotId = NO_TELEPORT;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message != null ? message : "";
    }

    public boolean hasTeleport() {
        return teleportToKnotId >= 0;
    }

    public int getTeleportToKnotId() {
        return teleportToKnotId;
    }

    public void setTeleportToKnotId(int knotId) {
        this.teleportToKnotId = knotId;
    }

    public void clearTeleport() {
        this.teleportToKnotId = NO_TELEPORT;
    }
}
