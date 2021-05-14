package ru.bortexel.core.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class Location {
    private final ServerWorld world;
    private final Vec3d location;

    public Location(ServerWorld world, Vec3d location) {
        this.world = world;
        this.location = location;
    }

    public static Location getForPlayer(ServerPlayerEntity player) {
        return new Location(player.getServerWorld(), player.getPos());
    }

    public void bring(ServerPlayerEntity player) {
        Vec3d location = this.getLocation();
        player.teleport(this.getWorld(), location.x, location.y, location.z, 0, 0);
    }

    public ServerWorld getWorld() {
        return world;
    }

    public Vec3d getLocation() {
        return location;
    }
}
