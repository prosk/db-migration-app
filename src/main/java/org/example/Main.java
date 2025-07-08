package org.example;

import liquibase.integration.spring.SpringLiquibase;
import org.example.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting application...");

        try {
            verifyResources();

            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            SpringLiquibase liquibase = context.getBean(SpringLiquibase.class);

            logger.info("Liquibase migration completed successfully");

        } catch (Exception e) {
            logger.error("Application failed to start", e);
            System.exit(1);
        }
    }

    private static void verifyResources() throws IOException {
        logger.info("Verifying classpath resources...");

        ClassPathResource log4jConfig = new ClassPathResource("log4j2.xml");
        ClassPathResource masterChangelog = new ClassPathResource("db/changelog/changelog-master.xml");

        if (log4jConfig.exists()) {
            logger.info("Found log4j2.xml in classpath");
        } else {
            logger.warn("log4j2.xml not found in classpath");
        }

        if (masterChangelog.exists()) {
            logger.info("Found master changelog in classpath");
        } else {
            logger.error("Master changelog not found in classpath");
            throw new RuntimeException("Required changelog files not found");
        }
    }
}