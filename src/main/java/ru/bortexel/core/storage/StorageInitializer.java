package ru.bortexel.core.storage;

import ru.bortexel.core.util.DatabaseUtil;
import ru.bortexel.core.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StorageInitializer {
    private final Connection connection;
    private final List<String> migrations = new ArrayList<String>() {{
        add("freezes");
    }};

    public StorageInitializer(Connection connection) {
        this.connection = connection;
    }

    public void actualizeSchema() throws SQLException, IOException {
        for (String migration : this.getMigrations()) {
            InputStream stream = getClass().getResourceAsStream("/schema/" + migration + ".sql");
            List<String> script = FileUtil.getLinesFromStream(stream);
            DatabaseUtil.executeScript(this.getConnection(), script);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public List<String> getMigrations() {
        return migrations;
    }
}
