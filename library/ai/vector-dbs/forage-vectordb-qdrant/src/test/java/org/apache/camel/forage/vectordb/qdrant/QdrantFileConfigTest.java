package org.apache.camel.forage.vectordb.qdrant;

import static org.assertj.core.api.Fail.fail;

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
 * Unit test for Qdrant vector database using file-based configuration.
 *
 * <p>This test demonstrates how the Camel Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-vectordb-qdrant.properties' file
 * on the classpath.
 */
public class QdrantFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(QdrantFileConfigTest.class);

    private static final String PROPERTIES_FILE = "forage-vectordb-qdrant.properties";

    @BeforeAll
    public static void setupQdrantFileConfiguration() {
        LOG.info("Setting up Qdrant file-based configuration test");
        clearQdrantSystemProperties();
        copyPropertiesFile();
        LOG.info("Qdrant file-based configuration setup complete");
    }

    private static void copyPropertiesFile() {
        try {
            Path sourceFile = Paths.get("test-configuration", PROPERTIES_FILE);
            Path targetDir = Paths.get("src/test/resources");
            Path targetFile = targetDir.resolve(PROPERTIES_FILE);

            Files.createDirectories(targetDir);
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            LOG.info("Copied {} to {}", sourceFile, targetFile);
        } catch (IOException e) {
            fail("Failed to copy properties file: {}", e.getMessage());
        }
    }

    @AfterAll
    public static void teardownQdrantFileConfiguration() {
        LOG.info("Cleaning up Qdrant file-based configuration");
        clearQdrantSystemProperties();
        removePropertiesFile();
        LOG.info("Qdrant file-based configuration cleanup complete");
    }

    private static void removePropertiesFile() {
        try {
            Path targetFile = Paths.get("src/test/resources/" + PROPERTIES_FILE);
            if (Files.exists(targetFile)) {
                Files.delete(targetFile);
                LOG.info("Removed properties file: {}", targetFile);
            }
        } catch (IOException e) {
            fail("Failed to remove properties file: {}", e.getMessage());
        }
    }

    private static void clearQdrantSystemProperties() {
        // Clear all possible qdrant system properties based on QdrantConfig
        System.clearProperty("qdrant.collection.name");
        System.clearProperty("qdrant.host");
        System.clearProperty("qdrant.port");
        System.clearProperty("qdrant.use.tls");
        System.clearProperty("qdrant.payload.text.key");
        System.clearProperty("qdrant.api.key");

        // Also clear the simple property names (without qdrant prefix)
        System.clearProperty("collection.name");
        System.clearProperty("host");
        System.clearProperty("port");
        System.clearProperty("use.tls");
        System.clearProperty("payload.text.key");
        System.clearProperty("api.key");
    }

    @Test
    public void shouldCreateQdrantProviderInstance() {
        LOG.info("Testing Qdrant provider instantiation");
        QdrantProvider provider = new QdrantProvider();
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();
        LOG.info("Successfully created Qdrant provider");
    }
}
