package ru.bortexel.core.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.GameModeCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bortexel.core.util.PermissionUtil;

import java.util.Collection;
import java.util.Collections;

@Mixin(GameModeCommand.class)
public abstract class GameModeCommandMixin {
    @Shadow
    private static int execute(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, GameMode gameMode) {
        return 0;
    }

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo info) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("gamemode").requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(2) && PermissionUtil.hasPermission(serverCommandSource, "minecraft.command.gamemode"));
        GameMode[] var2 = GameMode.values();

        for (GameMode gameMode : var2) {
            if (gameMode != GameMode.NOT_SET) {
                literalArgumentBuilder.then(
                        (CommandManager.literal(gameMode.getName()).executes((commandContext) -> execute(commandContext, Collections.singleton(commandContext.getSource().getPlayer()), gameMode))).then(
                                CommandManager.argument("target", EntityArgumentType.players()).executes((commandContext) -> execute(commandContext, EntityArgumentType.getPlayers(commandContext, "target"), gameMode))
                        )
                );
            }
        }

        dispatcher.register(literalArgumentBuilder);
        info.cancel();
    }
}
