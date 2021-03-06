package ru.bortexel.core.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class PermissionUtil {
    private final static String PERMISSION_NAMESPACE = "bortexel";

    public static boolean hasPermission(ServerPlayerEntity player, String permission) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        PlayerAdapter<ServerPlayerEntity> playerAdapter = luckPerms.getPlayerAdapter(ServerPlayerEntity.class);
        return playerAdapter.getPermissionData(player).checkPermission(permission).asBoolean();
    }

    public static boolean hasPermission(ServerCommandSource source, String permission) {
        try {
            return hasPermission(source.getPlayer(), permission);
        } catch (CommandSyntaxException ignored) { }
        return false;
    }

    public static String getNamespacedPermission(String name) {
        return PERMISSION_NAMESPACE + "." + name;
    }

    public static boolean hasNamespacedPermission(ServerPlayerEntity player, String permission) {
        return hasPermission(player, getNamespacedPermission(permission));
    }

    public static boolean hasNamespacedPermission(ServerCommandSource source, String permission) {
        return hasPermission(source, getNamespacedPermission(permission));
    }
}
