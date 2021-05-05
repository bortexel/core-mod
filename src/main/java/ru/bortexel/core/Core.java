package ru.bortexel.core;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import ru.ruscalworld.storagelib.Storage;
import ru.ruscalworld.storagelib.impl.SQLiteStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

public class Core implements ModInitializer {
    public static final Path DATABASE_PATH = FabricLoader.getInstance().getGameDir().resolve("mods/bortexel/core.db");
    private Storage storage;

    @Override
    public void onInitialize() {
        try {
            Storage storage = new SQLiteStorage("jdbc:sqlite:" + DATABASE_PATH.toAbsolutePath());
            storage.setup();

            this.setStorage(storage);
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
}
