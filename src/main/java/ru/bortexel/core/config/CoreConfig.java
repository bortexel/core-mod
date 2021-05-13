package ru.bortexel.core.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class CoreConfig {
    private final String apiToken;
    private final String apiUrl;
    private final String bcsUrl;

    public CoreConfig() throws IOException {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("bortexel/api.properties");
        Properties properties = new Properties();
        properties.load(Files.newInputStream(path));

        this.apiToken = properties.getProperty("api-token", "");
        this.apiUrl = properties.getProperty("api-url", "https://api.bortexel.ru/v3");
        this.bcsUrl = properties.getProperty("bcs-url", "wss://bcs.bortexel.ru/v1/websocket");
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getBcsUrl() {
        return bcsUrl;
    }
}
