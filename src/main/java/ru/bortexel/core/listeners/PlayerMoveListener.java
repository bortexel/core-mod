package ru.bortexel.core.listeners;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import ru.bortexel.core.Core;
import ru.bortexel.core.ModPart;
import ru.bortexel.core.events.ServerPlayerEvents;

public class PlayerMoveListener extends ModPart implements ServerPlayerEvents.PlayerMove {
    public PlayerMoveListener(Core mod) {
        super(mod);
    }

    @Override
    public ActionResult move(ServerPlayerEntity player) {
        if (this.getCoreMod().getFreezedPlayers().containsKey(player.getUuid())) return ActionResult.FAIL;
        return ActionResult.PASS;
    }
}
