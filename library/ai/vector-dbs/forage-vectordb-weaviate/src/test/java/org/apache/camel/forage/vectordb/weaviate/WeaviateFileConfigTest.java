package org.apache.camel.forage.vectordb.weaviate;

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
 * Unit test for Weaviate vector database using file-based configuration.
 *
 * <p>This test demonstrates how the Camel Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-vectordb-weaviate.properties' file
 * on the classpath.
 */
public class WeaviateFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(WeaviateFileConfigTest.class);

    private static final String PROPERTIES_FILE = "forage-vectordb-weaviate.properties";

    @BeforeAll
    public static void setupWeaviateFileConfiguration() {
        LOG.info("Setting up Weaviate file-based configuration test");
        clearWeaviateSystemProperties();
        removePropertiesFile();
        copyPropertiesFile();
        LOG.info("Weaviate file-based configuration setup complete");
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
    public static void teardownWeaviateFileConfiguration() {
        LOG.info("Cleaning up Weaviate file-based configuration");
        clearWeaviateSystemProperties();
        removePropertiesFile();
        LOG.info("Weaviate file-based configuration cleanup complete");
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

    private static void clearWeaviateSystemProperties() {
        // Clear all possible weaviate system properties based on WeaviateConfig
        System.clearProperty("weaviate.api-key");
        System.clearProperty("weaviate.scheme");
        System.clearProperty("weaviate.host");
        System.clearProperty("weaviate.port");
        System.clearProperty("weaviate.use-grpc-for-inserts");
        System.clearProperty("weaviate.secured-grpc");
        System.clearProperty("weaviate.grpc-port");
        System.clearProperty("weaviate.object-class");
        System.clearProperty("weaviate.avoid-dups");
        System.clearProperty("weaviate.consistency-level");
        System.clearProperty("weaviate.metadata-keys");
        System.clearProperty("weaviate.text-field-name");
        System.clearProperty("weaviate.metadata-field-name");

        // Also clear the simple property names (without weaviate prefix)
        System.clearProperty("api-key");
        System.clearProperty("scheme");
        System.clearProperty("host");
        System.clearProperty("port");
        System.clearProperty("use-grpc-for-inserts");
        System.clearProperty("secured-grpc");
        System.clearProperty("grpc-port");
        System.clearProperty("object-class");
        System.clearProperty("avoid-dups");
        System.clearProperty("consistency-level");
        System.clearProperty("metadata-keys");
        System.clearProperty("text-field-name");
        System.clearProperty("metadata-field-name");
    }

    @Test
    public void shouldCreateWeaviateProviderInstance() {
        LOG.info("Testing Weaviate provider instantiation");
        WeaviateProvider provider = new WeaviateProvider();
        EmbeddingStore<TextSegment> wes = provider.create();
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();
        org.assertj.core.api.Assertions.assertThat(wes).isNotNull();
        LOG.info("Successfully created Weaviate provider");
    }
}
