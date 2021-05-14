package ru.bortexel.core.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bortexel.core.events.ServerPlayerEvents;

import java.nio.charset.StandardCharsets;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Shadow public abstract void disconnect(Text reason);

    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    public void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo info) {
        if (player.prevX == Double.MAX_VALUE) return;
        ActionResult result = ServerPlayerEvents.PLAYER_MOVE.invoker().move(player);
        if (result == ActionResult.FAIL) try {
            info.cancel();
            player.requestTeleport(player.getX(), player.getY(), player.getZ());
        } catch (Exception ignored) { }
    }

    @Inject(method = "onBookUpdate", at = @At("HEAD"))
    public void onBookUpdate(BookUpdateC2SPacket packet, CallbackInfo info) {
        ItemStack book = packet.getBook();
        CompoundTag tag = book.getTag();
        if (tag == null) return;

        ListTag pages = tag.getList("pages", 8);
        if (pages.size() > 100) {
            disconnect(new LiteralText("Книга слишком большая"));
            return;
        }

        long total = 0;
        for (int i = 0; i < pages.size(); i++) {
            String string = pages.getString(i);
            int length = string.getBytes(StandardCharsets.UTF_8).length;
            if (length > 1024) {
                disconnect(new LiteralText("Книга слишком большая"));
                return;
            }

            total += length;
        }

        if (total > 65546) disconnect(new LiteralText("Книга слишком большая"));
    }
}
