package ru.bortexel.core.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bortexel.core.events.ServerPlayerEvents;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        ServerPlayerEvents.PLAYER_JOIN.invoker().join(player);
    }
}
