package org.apache.camel.forage.vectordb.milvus;

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
 * Unit test for Milvus vector database using file-based configuration.
 *
 * <p>This test demonstrates how the Camel Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-vectordb-milvus.properties' file
 * on the classpath.
 */
public class MilvusFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(MilvusFileConfigTest.class);

    @BeforeAll
    public static void setupMilvusFileConfiguration() {
        LOG.info("Setting up Milvus file-based configuration test");
        clearMilvusSystemProperties();
        copyPropertiesFile();
        LOG.info("Milvus file-based configuration setup complete");
    }

    private static void copyPropertiesFile() {
        try {
            Path sourceFile = Paths.get("forage-vectordb-milvus.properties-example");
            Path targetDir = Paths.get("src/test/resources");
            Path targetFile = targetDir.resolve("forage-vectordb-milvus.properties");

            Files.createDirectories(targetDir);
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            LOG.info("Copied {} to {}", sourceFile, targetFile);
        } catch (IOException e) {
            LOG.warn("Failed to copy properties file: {}", e.getMessage());
        }
    }

    @AfterAll
    public static void teardownMilvusFileConfiguration() {
        LOG.info("Cleaning up Milvus file-based configuration");
        clearMilvusSystemProperties();
        LOG.info("Milvus file-based configuration cleanup complete");
    }

    private static void clearMilvusSystemProperties() {
        // Clear all possible milvus system properties based on MilvusConfig
        System.clearProperty("milvus.host");
        System.clearProperty("milvus.port");
        System.clearProperty("milvus.uri");
        System.clearProperty("milvus.token");
        System.clearProperty("milvus.username");
        System.clearProperty("milvus.password");
        System.clearProperty("milvus.collection-name");
        System.clearProperty("milvus.dimension");
        System.clearProperty("milvus.index-type");
        System.clearProperty("milvus.metric-type");
        System.clearProperty("milvus.consistency-level");
        System.clearProperty("milvus.retrieve-embeddings-on-search");
        System.clearProperty("milvus.auto-flush-on-insert");
        System.clearProperty("milvus.database-name");
        System.clearProperty("milvus.id-field-name");
        System.clearProperty("milvus.text-field-name");
        System.clearProperty("milvus.metadata-field-name");
        System.clearProperty("milvus.vector-field-name");

        // Also clear the simple property names (without milvus prefix)
        System.clearProperty("host");
        System.clearProperty("port");
        System.clearProperty("uri");
        System.clearProperty("token");
        System.clearProperty("username");
        System.clearProperty("password");
        System.clearProperty("collection-name");
        System.clearProperty("dimension");
        System.clearProperty("index-type");
        System.clearProperty("metric-type");
        System.clearProperty("consistency-level");
        System.clearProperty("retrieve-embeddings-on-search");
        System.clearProperty("auto-flush-on-insert");
        System.clearProperty("database-name");
        System.clearProperty("id-field-name");
        System.clearProperty("text-field-name");
        System.clearProperty("metadata-field-name");
        System.clearProperty("vector-field-name");
    }

    @Test
    public void shouldCreateMilvusProviderInstance() {
        LOG.info("Testing Milvus provider instantiation");
        MilvusProvider provider = new MilvusProvider();
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();
        LOG.info("Successfully created Milvus provider");
    }
}
