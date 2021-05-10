package ru.bortexel.core.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ru.bortexel.core.Core;
import ru.bortexel.core.commands.DefaultCommand;
import ru.bortexel.core.models.Freeze;
import ru.bortexel.core.util.PermissionUtil;
import ru.ruscalworld.storagelib.Storage;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class FreezeCommand extends DefaultCommand {
    private final boolean unfreeze;

    protected FreezeCommand(Core mod, boolean unfreeze) {
        super(mod);
        this.unfreeze = unfreeze;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        Core mod = this.getCoreMod();
        Storage storage = mod.getStorage();
        ServerCommandSource source = context.getSource();
        HashMap<UUID, Freeze> freezedPlayers = mod.getFreezedPlayers();

        if (this.isUnfreeze()) {
            if (!freezedPlayers.containsKey(player.getUuid())) {
                throw new CommandException(new LiteralText("Этот игрок не заморожен"));
            }

            Freeze freeze = freezedPlayers.get(player.getUuid());
            freeze.cancel(player);

            CompletableFuture.runAsync(() -> {
                try {
                    storage.delete(freeze);
                } catch (Exception ignored) { }
            });

            freezedPlayers.remove(player.getUuid());
            source.sendFeedback(new LiteralText("Игрок ").append(player.getDisplayName()).append(" был разморожен"), true);
        } else {
            if (freezedPlayers.containsKey(player.getUuid())) {
                throw new CommandException(new LiteralText("Этот игрок уже заморожен"));
            }

            Freeze freeze = Freeze.createForPlayer(player);
            freeze.setAdminId(source.getPlayer().getUuid());
            freeze.apply(player);

            CompletableFuture.runAsync(() -> {
                try {
                    long id = storage.save(freeze);
                    Freeze savedFreeze = storage.retrieve(Freeze.class, id);
                    freezedPlayers.put(player.getUuid(), savedFreeze);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });

            source.sendFeedback(new LiteralText("Игрок ").append(player.getDisplayName()).append(" был заморожен"), true);
        }

        return 0;
    }

    public static void registerFreeze(CommandDispatcher<ServerCommandSource> dispatcher, Core mod) {
        dispatcher.register(literal("freeze").requires(source -> PermissionUtil.hasNamespacedPermission(source, "freeze")).then(
                argument("player", EntityArgumentType.player()).executes(new FreezeCommand(mod, false))
        ));
    }

    public static void registerUnfreeze(CommandDispatcher<ServerCommandSource> dispatcher, Core mod) {
        dispatcher.register(literal("unfreeze").requires(source -> PermissionUtil.hasNamespacedPermission(source, "unfreeze")).then(
                argument("player", EntityArgumentType.player()).executes(new FreezeCommand(mod, true))
        ));
    }

    public boolean isUnfreeze() {
        return unfreeze;
    }
}
