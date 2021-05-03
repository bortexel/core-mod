package ru.bortexel.core.storage;

import net.fabricmc.loader.api.FabricLoader;
import org.sqlite.JDBC;
import ru.bortexel.core.exceptions.InvalidModelException;
import ru.bortexel.core.exceptions.NotFoundException;
import ru.bortexel.core.storage.annotations.Model;
import ru.bortexel.core.util.DatabaseUtil;
import ru.bortexel.core.util.ReflectUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;

public class Storage {
    public static final Path DATABASE_PATH = FabricLoader.getInstance().getGameDir().resolve("mods/bortexel/core.db");

    private Connection connection;

    public void setup() throws SQLException, IOException {
        // Create connection
        DriverManager.registerDriver(new JDBC());
        this.connection = getConnection();

        // Create tables if they doesn't exist
        new StorageInitializer(this.getConnection()).actualizeSchema();
    }

    public <T> T retrieve(Class<T> clazz, int id) throws SQLException, InvalidModelException, NotFoundException {
        if (!clazz.isAnnotationPresent(Model.class)) throw new InvalidModelException(clazz);
        Model model = clazz.getAnnotation(Model.class);

        String query = String.format("SELECT * FROM `%s` WHERE `id` = ?", model.table());
        PreparedStatement statement = this.getConnection().prepareStatement(query);
        statement.setInt(1, id);

        try {
            T instance = clazz.getConstructor().newInstance();
            final HashMap<String, Field> fields = ReflectUtil.getClassFields(instance.getClass());

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) throw new NotFoundException("id", id);
            for (String column : DatabaseUtil.getColumnNames(resultSet)) {
                if (!fields.containsKey(column)) continue;
                Object value = resultSet.getString(column);

                Field field = fields.get(column);
                field.setAccessible(true);
                ReflectUtil.setFieldValue(instance, field, value);
            }

            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T extends DefaultModel> int save(T model) throws InvalidModelException, SQLException {
        Class<? extends DefaultModel> clazz = model.getClass();
        if (!clazz.isAnnotationPresent(Model.class)) throw new InvalidModelException(clazz);

        HashMap<String, Field> fields = ReflectUtil.getClassFields(clazz);
        HashMap<String, String> values = new HashMap<>();

        for (String name : fields.keySet()) {
            Field field = fields.get(name);
            Object value = null;

            try {
                field.setAccessible(true);
                value = field.get(model);
            } catch (IllegalAccessException ignored) { }

            values.put(name, value == null ? null : value.toString());
        }

        Model modelInfo = clazz.getAnnotation(Model.class);
        String table = modelInfo.table();
        PreparedStatement statement;

        if (model.getId() == 0) {
            values.remove("id");
            statement = DatabaseUtil.makeInsertStatement(values, table, this.getConnection());
        } else statement = DatabaseUtil.makeUpdateStatement("id", "" + model.getId(), values, table, this.getConnection());

        return statement.executeUpdate();
    }

    private Connection getConnection() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) return this.connection;
        return DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH.toAbsolutePath());
    }
}
