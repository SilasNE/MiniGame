package com.example.marioparty.model.items;




public final class ItemUseOutcome {

    private String message = "";

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
