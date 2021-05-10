package ru.bortexel.core.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface ServerPlayerEvents {
    Event<PlayerJoin> PLAYER_JOIN = EventFactory.createArrayBacked(PlayerJoin.class, (listeners) -> (player) -> {
        for (PlayerJoin listener : listeners) {
            ActionResult result = listener.join(player);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    Event<PlayerMove> PLAYER_MOVE = EventFactory.createArrayBacked(PlayerMove.class, (listeners) -> (player) -> {
        for (PlayerMove listener : listeners) {
            ActionResult result = listener.move(player);

            if (result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    interface PlayerJoin {
        ActionResult join(ServerPlayerEntity player);
    }

    interface PlayerMove {
        ActionResult move(ServerPlayerEntity player);
    }
}
