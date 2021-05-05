package ru.bortexel.core.models;

import ru.ruscalworld.storagelib.DefaultModel;
import ru.ruscalworld.storagelib.annotations.Model;
import ru.ruscalworld.storagelib.annotations.Property;

import java.sql.Timestamp;
import java.util.UUID;

@Model(table = "freezes")
public class Freeze extends DefaultModel {
    @Property(column = "player_id")
    private UUID playerId;

    @Property(column = "admin_id")
    private UUID adminId;

    @Property(column = "freezed_at")
    private Timestamp freezedAt;

    public Freeze() {

    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID uuid) {
        this.playerId = uuid;
    }

    public UUID getAdminId() {
        return adminId;
    }

    public void setAdminId(UUID adminID) {
        this.adminId = adminID;
    }

    public Timestamp getFreezedAt() {
        return freezedAt;
    }

    public void setFreezedAt(Timestamp freezedAt) {
        this.freezedAt = freezedAt;
    }
}
