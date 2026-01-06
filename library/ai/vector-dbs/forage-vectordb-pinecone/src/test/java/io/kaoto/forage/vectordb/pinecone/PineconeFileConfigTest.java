package io.kaoto.forage.vectordb.pinecone;

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
 * Unit test for Pinecone vector database using file-based configuration.
 *
 * <p>This test demonstrates how the Camel Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-vectordb-pinecone.properties' file
 * on the classpath.
 *
 * <p>Note: Pinecone is a cloud service. These tests verify configuration loading
 * without requiring a real API key.
 */
public class PineconeFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(PineconeFileConfigTest.class);

    private static final String PROPERTIES_FILE = "forage-vectordb-pinecone.properties";

    @BeforeAll
    public static void setupPineconeFileConfiguration() {
        LOG.info("Setting up Pinecone file-based configuration test");
        clearPineconeSystemProperties();
        copyPropertiesFile();
        LOG.info("Pinecone file-based configuration setup complete");
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
    public static void teardownPineconeFileConfiguration() {
        LOG.info("Cleaning up Pinecone file-based configuration");
        clearPineconeSystemProperties();
        removePropertiesFile();
        LOG.info("Pinecone file-based configuration cleanup complete");
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

    private static void clearPineconeSystemProperties() {
        // Clear all possible pinecone system properties based on PineconeConfig
        System.clearProperty("pinecone.api-key");
        System.clearProperty("pinecone.index");
        System.clearProperty("pinecone.environment");
        System.clearProperty("pinecone.project-id");
        System.clearProperty("pinecone.dimension");
        System.clearProperty("pinecone.cloud");
        System.clearProperty("pinecone.region");
        System.clearProperty("pinecone.name-space");
        System.clearProperty("pinecone.metadata-text-key");
        System.clearProperty("pinecone.create-index");
        System.clearProperty("pinecone.deletion-protection");

        // Also clear the simple property names (without pinecone prefix)
        System.clearProperty("api-key");
        System.clearProperty("index");
        System.clearProperty("environment");
        System.clearProperty("project-id");
        System.clearProperty("dimension");
        System.clearProperty("cloud");
        System.clearProperty("region");
        System.clearProperty("name-space");
        System.clearProperty("metadata-text-key");
        System.clearProperty("create-index");
        System.clearProperty("deletion-protection");
    }

    @Test
    public void shouldCreatePineconeProviderInstance() {
        LOG.info("Testing Pinecone provider instantiation");
        PineconeProvider provider = new PineconeProvider();
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();

        try {
            EmbeddingStore<TextSegment> pinecone = provider.create();
        } catch (io.pinecone.exceptions.PineconeAuthorizationException pae) {
            LOG.info("Successfully caught PineconeAuthorizationException");
        } catch (Exception e) {
            fail("Failed to create Pinecone EmbeddingStore {}", e);
        }

        LOG.info("Successfully created Pinecone provider");
    }
}
