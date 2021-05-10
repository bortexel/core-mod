package ru.bortexel.core.mixin;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bortexel.core.events.ServerPlayerEvents;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    public void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo info) {
        if (player.prevX == Double.MAX_VALUE) return;
        ActionResult result = ServerPlayerEvents.PLAYER_MOVE.invoker().move(player);
        if (result == ActionResult.FAIL) {
            info.cancel();
            player.requestTeleport(player.getX(), player.getY(), player.getZ());
        }
    }
}
