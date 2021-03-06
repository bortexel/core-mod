package ru.bortexel.core.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import ru.bortexel.core.Core;
import ru.bortexel.core.ModPart;
import ru.bortexel.core.util.Location;
import ru.bortexel.core.util.PermissionUtil;

import java.util.HashMap;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpectateCommand extends ModPart {
    protected SpectateCommand(Core mod) {
        super(mod);
    }

    private int spectate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity admin = context.getSource().getPlayer();
        this.spectate(admin, null);
        return 0;
    }

    private int spectatePlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerPlayerEntity admin = context.getSource().getPlayer();
        this.spectate(admin, target);
        return 0;
    }

    private void spectate(ServerPlayerEntity admin, @Nullable ServerPlayerEntity target) {
        HashMap<UUID, Location> locations = this.getCoreMod().getSpectatorLocations();
        MinecraftServer server = this.getCoreMod().getServer();
        PlayerManager playerManager = server.getPlayerManager();

        if (target != null) {
            // And save previous location, if the previous one doesn't exist
            if (!locations.containsKey(admin.getUuid())) locations.put(admin.getUuid(), Location.getForPlayer(admin));

            // If target is not null, teleport to the player
            admin.changeGameMode(GameMode.SPECTATOR);
            admin.teleport(target.getServerWorld(), target.getX(), target.getY(), target.getZ(), 0, 0);
        } else {
            if (locations.containsKey(admin.getUuid())) {
                // If target is null and previous location exists, then disable spectator
                Location location = locations.remove(admin.getUuid());
                location.bring(admin);
                admin.changeGameMode(GameMode.SURVIVAL);
            } else {
                // Target is not null, but there is no previous location, so enable spectator
                admin.changeGameMode(GameMode.SPECTATOR);
                locations.put(admin.getUuid(), Location.getForPlayer(admin));
            }
        }
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, Core mod) {
        dispatcher.register(literal("sp").requires(source -> PermissionUtil.hasNamespacedPermission(source, "spectate")).then(
                argument("player", EntityArgumentType.player()).executes(new SpectateCommand(mod)::spectatePlayer)
        ).executes(new SpectateCommand(mod)::spectate));
    }
}
