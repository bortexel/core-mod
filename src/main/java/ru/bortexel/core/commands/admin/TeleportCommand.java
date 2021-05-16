package ru.bortexel.core.commands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import ru.bortexel.core.util.PermissionUtil;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        PosArgument positionArgument = Vec3ArgumentType.getPosArgument(context, "position");
        Vec3d position = positionArgument.toAbsolutePos(context.getSource());
        ServerWorld world = DimensionArgumentType.getDimensionArgument(context, "world");
        if (world == null) world = player.getServerWorld();

        player.teleport(world, position.x, position.y, position.z, 0, 0);
        return 0;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> command = dispatcher.register(literal("teleport").requires(source -> PermissionUtil.hasNamespacedPermission(source, "teleport")).then(
                argument("position", Vec3ArgumentType.vec3()).then(
                        argument("world", DimensionArgumentType.dimension()).executes(new TeleportCommand())
                )
        ));
        dispatcher.register(literal("tp").requires(source -> PermissionUtil.hasNamespacedPermission(source, "teleport")).redirect(command));
    }
}
