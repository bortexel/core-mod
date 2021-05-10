package ru.bortexel.core.listeners;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import ru.bortexel.core.Core;
import ru.bortexel.core.ModPart;
import ru.bortexel.core.events.ServerPlayerEvents;
import ru.bortexel.core.models.Freeze;

public class PlayerJoinListener extends ModPart implements ServerPlayerEvents.PlayerJoin {
    public PlayerJoinListener(Core mod) {
        super(mod);
    }

    @Override
    public ActionResult join(ServerPlayerEntity player) {
        Freeze freeze = this.getCoreMod().getFreezedPlayers().get(player.getUuid());
        if (freeze == null) return ActionResult.PASS;
        freeze.apply(player);
        return ActionResult.PASS;
    }
}
