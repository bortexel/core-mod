package ru.bortexel.core;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import okhttp3.OkHttpClient;
import ru.bortexel.core.commands.admin.FreezeCommand;
import ru.bortexel.core.commands.admin.SpectateCommand;
import ru.bortexel.core.commands.admin.TeleportCommand;
import ru.bortexel.core.config.BortexelConfig;
import ru.bortexel.core.config.CoreConfig;
import ru.bortexel.core.events.ServerPlayerEvents;
import ru.bortexel.core.listeners.PlayerJoinListener;
import ru.bortexel.core.listeners.PlayerMoveListener;
import ru.bortexel.core.listeners.bortexel.BanListener;
import ru.bortexel.core.models.Freeze;
import ru.bortexel.core.util.Location;
import ru.ruscalworld.bortexel4j.Bortexel4J;
import ru.ruscalworld.bortexel4j.listening.BroadcastingServer;
import ru.ruscalworld.storagelib.Storage;
import ru.ruscalworld.storagelib.impl.SQLiteStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Core implements ModInitializer {
    private static Core INSTANCE;
    public static final Path DATABASE_PATH = FabricLoader.getInstance().getGameDir().resolve("mods/bortexel/core.db");

    private Storage storage;
    private Bortexel4J bortexelClient;
    private CoreConfig config;
    private BortexelConfig bortexelConfig;
    private OkHttpClient httpClient;
    private MinecraftServer server;

    private final HashMap<UUID, Freeze> freezedPlayers = new HashMap<>();
    private final HashMap<UUID, Location> spectatorLocations = new HashMap<>();

    @Override
    public void onInitialize() {
        this.loadConfig();

        try {
            SQLiteStorage storage = new SQLiteStorage("jdbc:sqlite:" + DATABASE_PATH.toAbsolutePath());
            storage.registerMigration("freezes");
            storage.setup();

            this.setStorage(storage);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ServerLifecycleEvents.SERVER_STARTING.register(this::setServer);

        this.setHttpClient(new OkHttpClient());
        this.setBortexelClient(Bortexel4J.login(this.getBortexelConfig().getApiToken(), this.getBortexelConfig().getApiUrl(), this.getHttpClient()));

        BroadcastingServer broadcastingServer = this.getBortexelClient().getBroadcastingServer(this.getBortexelConfig().getBcsUrl());
        broadcastingServer.registerListener(new BanListener(this));
        broadcastingServer.connect();

        ServerPlayerEvents.PLAYER_JOIN.register(new PlayerJoinListener(this));
        ServerPlayerEvents.PLAYER_MOVE.register(new PlayerMoveListener(this));

        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            if (!dedicated) return;
            FreezeCommand.registerFreeze(dispatcher, this);
            FreezeCommand.registerUnfreeze(dispatcher, this);
            SpectateCommand.register(dispatcher, this);
            TeleportCommand.register(dispatcher);
        }));

        try {
            List<Freeze> freezes = storage.retrieveAll(Freeze.class);
            for (Freeze freeze : freezes) this.getFreezedPlayers().put(freeze.getPlayerId(), freeze);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> broadcastingServer.disconnect());
        INSTANCE = this;
    }

    protected void loadConfig() {
        try {
            this.config = new CoreConfig();
            this.bortexelConfig = new BortexelConfig();
        } catch (IOException e) {
            e.printStackTrace();
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

    public Bortexel4J getBortexelClient() {
        return bortexelClient;
    }

    public void setBortexelClient(Bortexel4J bortexelClient) {
        this.bortexelClient = bortexelClient;
    }

    public BortexelConfig getBortexelConfig() {
        return bortexelConfig;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public HashMap<UUID, Location> getSpectatorLocations() {
        return spectatorLocations;
    }

    public CoreConfig getConfig() {
        return config;
    }

    public static Core getInstance() {
        return INSTANCE;
    }
}
