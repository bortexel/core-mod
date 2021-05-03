package ru.bortexel.core.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseUtil {
    /**
     * Executes given SQL script in given SQL connection
     * @param connection SQL connection to use
     * @param script Script to execute
     * @throws SQLException If some SQL operation fails
     */
    public static void executeScript(Connection connection, List<String> script) throws SQLException {
        Statement statement = connection.createStatement();
        for (String line : script) statement.executeUpdate(line);
    }

    /**
     * Retrieves list of column names from given result set
     * @param resultSet ResultSet to retrieve column names from
     * @return List of column names
     */
    public static List<String> getColumnNames(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<String> columns = new ArrayList<>();

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i);
            columns.add(columnName);
        }

        return columns;
    }

    /**
     * Makes UPDATE SQL query
     * @param keyName Primary key name
     * @param key Primary key value
     * @param values HashMap with keys and values to update
     * @param namespace Table name
     * @param connection Connection to use
     * @return UPDATE statement made with given data
     * @throws SQLException if some SQL operation fails
     */
    public static PreparedStatement makeUpdateStatement(String keyName, String key, HashMap<String, String> values, String namespace, Connection connection) throws SQLException {
        // Create list with parts of SQL query
        List<String> update = new ArrayList<>();
        for (String column : values.keySet()) update.add("`" + column + "` = ?");

        // Construct SQL query
        String query = String.format("UPDATE `%s` SET %s WHERE `%s` = ?", namespace, String.join(", ", update), keyName);
        PreparedStatement statement = connection.prepareStatement(query);

        // Set values in prepared statement
        int i = 1;
        for (String column : values.keySet()) {
            String value = values.get(column);
            statement.setString(i, value);
            i++;
        }

        // Set required value of primary key
        statement.setString(i, key);
        return statement;
    }

    /**
     * Makes INSERT SQL query
     * @param params Row data: key is column name, value is value
     * @param namespace Table name
     * @param connection Connection to use
     * @return INSERT statement made with given data
     * @throws SQLException if some SQL operation fails
     */
    public static PreparedStatement makeInsertStatement(HashMap<String, String> params, String namespace, Connection connection) throws SQLException {
        // Make list with "questions"
        List<String> placeholders = new ArrayList<>();
        for (String ignored : params.values()) placeholders.add("?");

        // Make part with values: (?,?,...)
        String values = "(" + String.join(",", placeholders) + ")";
        // Make part with column names: (name,owner,...)
        String schema = "(" + String.join(",", params.keySet()) + ")";
        // Make complete query: INSERT INTO `table` (name,owner,...) VALUES (?,?,...)
        String query = String.format("INSERT INTO `%s` %s VALUES %s", namespace, schema, values);

        PreparedStatement statement = connection.prepareStatement(query);

        // Add values to statement
        int i = 1;
        for (String column : params.keySet()) {
            String value = params.get(column);
            statement.setString(i, value);
            i++;
        }

        return statement;
    }
}
