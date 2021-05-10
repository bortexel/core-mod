package ru.bortexel.core.models;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import ru.ruscalworld.storagelib.DefaultModel;
import ru.ruscalworld.storagelib.annotations.DefaultGenerated;
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

    @Property(column = "original_speed")
    private double originalSpeed;

    @Property(column = "freezed_at")
    @DefaultGenerated
    private Timestamp freezedAt;

    public Freeze() {

    }

    public void apply(ServerPlayerEntity player) {
        getSpeedAttributeInstance(player).setBaseValue(0);
    }

    public void cancel(ServerPlayerEntity player) {
        getSpeedAttributeInstance(player).setBaseValue(this.getOriginalSpeed());
    }

    public static @NotNull EntityAttributeInstance getSpeedAttributeInstance(ServerPlayerEntity player) {
        EntityAttribute attribute = Registry.ATTRIBUTE.get(new Identifier("minecraft:generic.movement_speed"));
        EntityAttributeInstance attributeInstance = player.getAttributeInstance(attribute);
        assert attribute != null && attributeInstance != null;
        return attributeInstance;
    }

    public static @NotNull Freeze createForPlayer(ServerPlayerEntity player) {
        EntityAttributeInstance attributeInstance = getSpeedAttributeInstance(player);
        Freeze freeze = new Freeze();
        freeze.setPlayerId(player.getUuid());
        freeze.setOriginalSpeed(attributeInstance.getBaseValue());
        return freeze;
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

    public double getOriginalSpeed() {
        return originalSpeed;
    }

    public void setOriginalSpeed(double originalSpeed) {
        this.originalSpeed = originalSpeed;
    }
}
