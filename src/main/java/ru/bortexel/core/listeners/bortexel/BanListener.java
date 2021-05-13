package ru.bortexel.core.listeners.bortexel;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import ru.bortexel.core.Core;
import ru.ruscalworld.bortexel4j.listening.events.EventListener;
import ru.ruscalworld.bortexel4j.listening.events.ban.GenericBanEvent;
import ru.ruscalworld.bortexel4j.models.ban.Ban;

public class BanListener extends EventListener {
    private final Core mod;

    public BanListener(Core mod) {
        this.mod = mod;
    }

    @Override
    public void onBanCreated(GenericBanEvent event) {
        Ban ban = event.getPayload();
        String username = ban.getUsername();

        PlayerManager playerManager = this.getMod().getServer().getPlayerManager();
        ServerPlayerEntity player = playerManager.getPlayer(username);
        if (player == null || !ban.isActual()) return;

        LiteralText reason = new LiteralText("Аккаунт заблокирован. Перезайдите на сервер, чтобы узнать подробности.");
        reason.formatted(Formatting.RED);
        player.networkHandler.disconnect(reason);
    }

    public Core getMod() {
        return mod;
    }
}
