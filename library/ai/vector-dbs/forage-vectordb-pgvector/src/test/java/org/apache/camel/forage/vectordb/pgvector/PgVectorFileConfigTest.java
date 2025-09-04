package org.apache.camel.forage.vectordb.pgvector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for PgVector vector database using file-based configuration.
 *
 * <p>This test demonstrates how the Camel Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-vectordb-pgvector.properties' file
 * on the classpath.
 */
public class PgVectorFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(PgVectorFileConfigTest.class);

    @BeforeAll
    public static void setupPgVectorFileConfiguration() {
        LOG.info("Setting up PgVector file-based configuration test");
        clearPgVectorSystemProperties();
        copyPropertiesFile();
        LOG.info("PgVector file-based configuration setup complete");
    }

    private static void copyPropertiesFile() {
        try {
            Path sourceFile = Paths.get("forage-vectordb-pgvector.properties-example");
            Path targetDir = Paths.get("src/test/resources");
            Path targetFile = targetDir.resolve("forage-vectordb-pgvector.properties");

            Files.createDirectories(targetDir);
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            LOG.info("Copied {} to {}", sourceFile, targetFile);
        } catch (IOException e) {
            LOG.warn("Failed to copy properties file: {}", e.getMessage());
        }
    }

    @AfterAll
    public static void teardownPgVectorFileConfiguration() {
        LOG.info("Cleaning up PgVector file-based configuration");
        clearPgVectorSystemProperties();
        LOG.info("PgVector file-based configuration cleanup complete");
    }

    private static void clearPgVectorSystemProperties() {
        // Clear all possible pgvector system properties based on PgVectorConfig
        System.clearProperty("pgvector.host");
        System.clearProperty("pgvector.port");
        System.clearProperty("pgvector.user");
        System.clearProperty("pgvector.password");
        System.clearProperty("pgvector.database");
        System.clearProperty("pgvector.table");
        System.clearProperty("pgvector.dimension");
        System.clearProperty("pgvector.use-index");
        System.clearProperty("pgvector.index-list-size");
        System.clearProperty("pgvector.create-table");
        System.clearProperty("pgvector.drop-table-first");
        System.clearProperty("pgvector.metadata-storage-config");

        // Also clear the simple property names (without pgvector prefix)
        System.clearProperty("host");
        System.clearProperty("port");
        System.clearProperty("user");
        System.clearProperty("password");
        System.clearProperty("database");
        System.clearProperty("table");
        System.clearProperty("dimension");
        System.clearProperty("use-index");
        System.clearProperty("index-list-size");
        System.clearProperty("create-table");
        System.clearProperty("drop-table-first");
        System.clearProperty("metadata-storage-config");
    }

    @Test
    public void shouldCreatePgVectorProviderInstance() {
        LOG.info("Testing PgVector provider instantiation");
        PgVectorProvider provider = new PgVectorProvider();
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();
        LOG.info("Successfully created PgVector provider");
    }
}
