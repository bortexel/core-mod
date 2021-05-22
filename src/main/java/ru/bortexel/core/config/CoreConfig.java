package ru.bortexel.core.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class CoreConfig {
    private final boolean logCartographerTrades;

    public CoreConfig() throws IOException {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("bortexel/core.properties");
        Properties properties = new Properties();
        if (!Files.exists(path)) Files.createFile(path);
        properties.load(Files.newInputStream(path));

        this.logCartographerTrades = properties.getProperty("log-cartographer-trades", "false").equalsIgnoreCase("true");
    }

    public boolean shouldLogCartographerTrades() {
        return logCartographerTrades;
    }
}
