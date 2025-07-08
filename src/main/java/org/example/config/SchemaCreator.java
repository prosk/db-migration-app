package org.example.config;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class SchemaCreator {
    private final DataSource dataSource;

    public SchemaCreator(DataSource dataSource) {
        this.dataSource = dataSource;
        createSchema();
    }

    private void createSchema() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("CREATE SCHEMA IF NOT EXISTS liquibase_schema");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create schema", e);
        }
    }
}
