package ru.bortexel.core.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.bortexel.core.Core;
import ru.bortexel.core.util.PrefixUtil;

import java.util.HashMap;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "getPlayerListName", at = @At("RETURN"), cancellable = true)
    public void getPlayerListName(CallbackInfoReturnable<Text> info) {
        if (!Core.getInstance().getConfig().shouldHandlePrefixes()) return;
        HashMap<String, String> prefixes = PrefixUtil.getPrefixMap();
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        String prefix = PrefixUtil.getPrefix(player, prefixes);
        MutableText text = new LiteralText(prefix);
        if (info.getReturnValue() != null) text = text.append(info.getReturnValue());
        else text = text.append(player.getEntityName());
        info.setReturnValue(text);
    }
}
