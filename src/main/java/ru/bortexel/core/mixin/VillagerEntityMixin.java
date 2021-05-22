package ru.bortexel.core.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bortexel.core.Core;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {
    @Shadow
    public VillagerData getVillagerData() {
        return null;
    }

    @Shadow
    @Nullable
    private PlayerEntity lastCustomer;

    @Inject(method = "afterUsing", at = @At("TAIL"))
    protected void afterUsing(TradeOffer offer, CallbackInfo ci) {
        Core core = Core.getInstance();
        if (!core.getConfig().shouldLogCartographerTrades()) return;

        VillagerData villagerData = this.getVillagerData();
        if (villagerData.getProfession() != VillagerProfession.CARTOGRAPHER) return;

        PlayerEntity player = this.lastCustomer;
        if (player == null) return;

        Logger logger = LogManager.getLogger("CartographerTradeLogger");
        logger.info("Player {} has just traded with cartographer with level {} at {}", player.getEntityName(), villagerData.getLevel(), player.getPos());
    }
}
