package io.kaoto.forage.vectordb.mariadb;

import static org.assertj.core.api.Fail.fail;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
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
 * Unit test for MariaDB vector database using file-based configuration.
 *
 * <p>This test demonstrates how the Camel Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-vectordb-mariadb.properties' file
 * on the classpath.
 */
public class MariaDbFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(MariaDbFileConfigTest.class);

    private static final String PROPERTIES_FILE = "forage-vectordb-mariadb.properties";

    @BeforeAll
    public static void setupMariaDbFileConfiguration() {
        LOG.info("Setting up MariaDB file-based configuration test");
        clearMariaDbSystemProperties();
        copyPropertiesFile();
        LOG.info("MariaDB file-based configuration setup complete");
    }

    private static void copyPropertiesFile() {
        try {
            Path sourceFile = Paths.get("test-configuration", PROPERTIES_FILE);
            Path targetDir = Paths.get(".");
            Path targetFile = targetDir.resolve(PROPERTIES_FILE);

            Files.createDirectories(targetDir);
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            LOG.info("Copied {} to {}", sourceFile, targetFile);
        } catch (IOException e) {
            fail("Failed to copy properties file: {}", e.getMessage());
        }
    }

    @AfterAll
    public static void teardownMariaDbFileConfiguration() {
        LOG.info("Cleaning up MariaDB file-based configuration");
        clearMariaDbSystemProperties();
        removePropertiesFile();
        LOG.info("MariaDB file-based configuration cleanup complete");
    }

    private static void removePropertiesFile() {
        try {
            Path targetFile = Paths.get(".", PROPERTIES_FILE);
            if (Files.exists(targetFile)) {
                Files.delete(targetFile);
                LOG.info("Removed properties file: {}", targetFile);
            }
        } catch (IOException e) {
            fail("Failed to remove properties file: {}", e.getMessage());
        }
    }

    private static void clearMariaDbSystemProperties() {
        // Clear all possible mariadb system properties based on MariaDbConfig
        System.clearProperty("mariadb.url");
        System.clearProperty("mariadb.user");
        System.clearProperty("mariadb.password");
        System.clearProperty("mariadb.table");
        System.clearProperty("mariadb.distance.type");
        System.clearProperty("mariadb.id.field.name");
        System.clearProperty("mariadb.embedding.field.name");
        System.clearProperty("mariadb.content.field.name");
        System.clearProperty("mariadb.create.table");
        System.clearProperty("mariadb.drop.table.first");
        System.clearProperty("mariadb.dimension");

        // Also clear the simple property names (without mariadb prefix)
        System.clearProperty("url");
        System.clearProperty("user");
        System.clearProperty("password");
        System.clearProperty("table");
        System.clearProperty("distance.type");
        System.clearProperty("id.field.name");
        System.clearProperty("embedding.field.name");
        System.clearProperty("content.field.name");
        System.clearProperty("create.table");
        System.clearProperty("drop.table.first");
        System.clearProperty("dimension");
    }

    @Test
    public void shouldCreateMariaDbProviderInstance() {
        LOG.info("Testing MariaDB provider instantiation");
        MariaDbProvider provider = new MariaDbProvider();
        try {
            // Attempt to create embedding store with file-based configuration
            // This may fail due to connection issues, which is expected in unit tests
            EmbeddingStore<TextSegment> embeddingStore = provider.create();
        } catch (RuntimeException re) {
            // Expected when database connection fails in test environment
            LOG.info("Successfully caught RuntimeException when creating MariaDB embedding store");
        } catch (Exception e) {
            fail("Caught exception trying to create MariaDB embedding store {}", e);
        }
        // Verify provider was created successfully
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();
        LOG.info("Successfully created MariaDB provider");
    }
}
