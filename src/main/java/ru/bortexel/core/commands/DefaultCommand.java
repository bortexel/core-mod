package ru.bortexel.core.commands;

import com.mojang.brigadier.Command;
import net.minecraft.server.command.ServerCommandSource;
import ru.bortexel.core.Core;
import ru.bortexel.core.ModPart;

public abstract class DefaultCommand extends ModPart implements Command<ServerCommandSource> {
    protected DefaultCommand(Core mod) {
        super(mod);
    }
}
