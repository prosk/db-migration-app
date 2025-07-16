package org.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class SchemaCreator {
    private static final Logger logger = LoggerFactory.getLogger(SchemaCreator.class);

    private final DataSource dataSource;
    private final Environment environment;

    public SchemaCreator(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
        createSchema();
    }

    private void createSchema() {
        String schemaName = getCommandLineParam(environment, "schemaName");
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("CREATE SCHEMA IF NOT EXISTS liquibase_schema");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create schema", e);
        }
    }

    private String getCommandLineParam(Environment environment, String paramName) {
        String paramValue = environment.getProperty(paramName);
        logger.info("The value of param {} is {}", paramName, paramValue);
        if (paramValue == null) {
            throw new IllegalArgumentException(paramName + " param must be defined!");
        }
        return paramValue;
    }
}
