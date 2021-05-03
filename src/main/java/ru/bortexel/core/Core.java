package ru.bortexel.core;

import net.fabricmc.api.ModInitializer;
import ru.bortexel.core.exceptions.InvalidModelException;
import ru.bortexel.core.exceptions.NotFoundException;
import ru.bortexel.core.models.Freeze;
import ru.bortexel.core.storage.Storage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class Core implements ModInitializer {
    private Storage storage;

    @Override
    public void onInitialize() {
        try {
            Storage storage = new Storage();
            storage.setup();
            this.setStorage(storage);
        } catch (SQLException | IOException exception) {
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
