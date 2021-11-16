package ru.bortexel.core.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.bortexel.core.util.PrefixUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    public void getPlayerListName(CallbackInfoReturnable<Text> info) {
        HashMap<String, String> prefixes = PrefixUtil.getPrefixMap();
        Optional<ServerPlayerEntity> player = this.getThis();
        if (player.isEmpty()) return;
        String prefix = PrefixUtil.getPrefix(player.get(), prefixes);
        info.setReturnValue(new LiteralText(prefix + player.get().getEntityName()));
    }

    private Optional<ServerPlayerEntity> getThis() {
        try {
            return Optional.ofNullable(networkHandler.player);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }
}
