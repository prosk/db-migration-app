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

    private static final int MAX_ATTEMPTS = 3;
    private static final int RETRY_DELAY_MS = 1000;

    public SchemaCreator(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
        createSchema();
    }

    private void createSchema() {
        String schemaName = getCommandLineParam(environment, "schemaName");

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            logger.info("Try to create schema {}", schemaName);
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
                logger.info("CREATE SCHEMA operator was completed successfully");
            } catch (SQLException e) {
                logger.warn("Creating schema attempt " + attempt + " failed due to error " + e.getMessage());
                if (attempt == MAX_ATTEMPTS) {
                    throw new RuntimeException("Failed to create schema", e);
                }
            }
            if (attempt < MAX_ATTEMPTS) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
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
