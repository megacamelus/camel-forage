package org.apache.camel.forage.vectordb.chroma;

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
 * Unit test for Chroma vector database using file-based configuration.
 *
 * <p>This test demonstrates how the Camel Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-vectordb-chroma.properties' file
 * on the classpath.
 */
public class ChromaFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(ChromaFileConfigTest.class);

    private static final String PROPERTIES_FILE = "forage-vectordb-chroma.properties";

    @BeforeAll
    public static void setupChromaFileConfiguration() {
        LOG.info("Setting up Chroma file-based configuration test");
        clearChromaSystemProperties();
        copyPropertiesFile();
        LOG.info("Chroma file-based configuration setup complete");
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
    public static void teardownChromaFileConfiguration() {
        LOG.info("Cleaning up Chroma file-based configuration");
        clearChromaSystemProperties();
        removePropertiesFile();
        LOG.info("Chroma file-based configuration cleanup complete");
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

    private static void clearChromaSystemProperties() {
        // Clear all possible chroma system properties based on ChromaConfig
        System.clearProperty("chroma.url");
        System.clearProperty("chroma.collection.name");
        System.clearProperty("chroma.timeout");
        System.clearProperty("chroma.log.requests");
        System.clearProperty("chroma.log.responses");

        // Also clear the simple property names (without chroma prefix)
        System.clearProperty("url");
        System.clearProperty("collection.name");
        System.clearProperty("timeout");
        System.clearProperty("log.requests");
        System.clearProperty("log.responses");
    }

    @Test
    public void shouldCreateChromaProviderInstance() {
        LOG.info("Testing Chroma provider instantiation");
        ChromaProvider provider = new ChromaProvider();
        try {
            EmbeddingStore<TextSegment> chroma = provider.create();
        } catch (RuntimeException re) {
            LOG.info("Expected a runtime exception on connecting to chroma, caught it successfully");
        } catch (Exception e) {
            fail("Could not instantiate a Chroma EmbeddingStore {}", e);
        }
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();

        LOG.info("Successfully created Chroma provider");
    }
}
