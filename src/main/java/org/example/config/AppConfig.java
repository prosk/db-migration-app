package org.example.config;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public DataSource dataSource(Environment environment) {
        // String jdbcUrl = getCommandLineParam(environment, "jdbcUrl");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUsername("postgres");
        dataSource.setPassword("qwe123");
        return dataSource;
    }

    @Bean
    public SchemaCreator schemaCreator(DataSource dataSource) {
        return new SchemaCreator(dataSource);
    }

    @Bean
    @DependsOn("schemaCreator")
    public SpringLiquibase liquibase(DataSource dataSource, Environment environment) {
        // String schemaName = getCommandLineParam(environment, "schemaName");
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/changelog-master.xml");
        liquibase.setDefaultSchema("liquibase_schema");
        liquibase.setLiquibaseSchema("liquibase_schema");
        liquibase.setContexts("development,test");
        liquibase.setShouldRun(true);
        return liquibase;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
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
