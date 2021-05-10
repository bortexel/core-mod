package ru.bortexel.core;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.bortexel.core.commands.admin.FreezeCommand;
import ru.bortexel.core.events.ServerPlayerEvents;
import ru.bortexel.core.listeners.PlayerJoinListener;
import ru.bortexel.core.listeners.PlayerMoveListener;
import ru.bortexel.core.models.Freeze;
import ru.ruscalworld.storagelib.Storage;
import ru.ruscalworld.storagelib.impl.SQLiteStorage;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Core implements ModInitializer {
    public static final Path DATABASE_PATH = FabricLoader.getInstance().getGameDir().resolve("mods/bortexel/core.db");
    private Storage storage;

    private final HashMap<UUID, Freeze> freezedPlayers = new HashMap<>();

    @Override
    public void onInitialize() {
        try {
            SQLiteStorage storage = new SQLiteStorage("jdbc:sqlite:" + DATABASE_PATH.toAbsolutePath());
            storage.registerMigration("freezes");
            storage.setup();

            this.setStorage(storage);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ServerPlayerEvents.PLAYER_JOIN.register(new PlayerJoinListener(this));
        ServerPlayerEvents.PLAYER_MOVE.register(new PlayerMoveListener(this));

        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            if (!dedicated) return;
            FreezeCommand.registerFreeze(dispatcher, this);
            FreezeCommand.registerUnfreeze(dispatcher, this);
        }));

        try {
            List<Freeze> freezes = storage.retrieveAll(Freeze.class);
            for (Freeze freeze : freezes) {
                this.getFreezedPlayers().put(freeze.getPlayerId(), freeze);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public HashMap<UUID, Freeze> getFreezedPlayers() {
        return freezedPlayers;
    }
}
