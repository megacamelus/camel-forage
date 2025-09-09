package org.apache.camel.forage.vectordb.neo4j;

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
 * Unit test for Neo4j vector database using file-based configuration.
 *
 * <p>This test demonstrates how the Camel Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-vectordb-neo4j.properties' file
 * on the classpath.
 */
public class Neo4jFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4jFileConfigTest.class);

    private static final String PROPERTIES_FILE = "forage-vectordb-neo4j.properties";

    @BeforeAll
    public static void setupNeo4jFileConfiguration() {
        LOG.info("Setting up Neo4j file-based configuration test");
        clearNeo4jSystemProperties();
        copyPropertiesFile();
        LOG.info("Neo4j file-based configuration setup complete");
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
    public static void teardownNeo4jFileConfiguration() {
        LOG.info("Cleaning up Neo4j file-based configuration");
        clearNeo4jSystemProperties();
        removePropertiesFile();
        LOG.info("Neo4j file-based configuration cleanup complete");
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

    private static void clearNeo4jSystemProperties() {
        // Clear all possible neo4j system properties based on Neo4jConfig
        System.clearProperty("neo4j.uri");
        System.clearProperty("neo4j.user");
        System.clearProperty("neo4j.password");
        System.clearProperty("neo4j.database.name");
        System.clearProperty("neo4j.index.name");
        System.clearProperty("neo4j.label");
        System.clearProperty("neo4j.embedding.property");
        System.clearProperty("neo4j.text.property");
        System.clearProperty("neo4j.id.property");
        System.clearProperty("neo4j.metadata.prefix");
        System.clearProperty("neo4j.dimension");
        System.clearProperty("neo4j.with.encryption");
        System.clearProperty("neo4j.connection.timeout");
        System.clearProperty("neo4j.max.connection.lifetime");
        System.clearProperty("neo4j.max.connection.pool.size");
        System.clearProperty("neo4j.connection.acquisition.timeout");
        System.clearProperty("neo4j.await.index.timeout");
        System.clearProperty("neo4j.retrieval.query");
        System.clearProperty("neo4j.entity.creation.query");
        System.clearProperty("neo4j.full.text.index.name");
        System.clearProperty("neo4j.full.text.query");
        System.clearProperty("neo4j.full.text.retrieval.query");
        System.clearProperty("neo4j.auto.create.full.text");

        // Also clear the simple property names (without neo4j prefix)
        System.clearProperty("uri");
        System.clearProperty("user");
        System.clearProperty("password");
        System.clearProperty("database.name");
        System.clearProperty("index.name");
        System.clearProperty("label");
        System.clearProperty("embedding.property");
        System.clearProperty("text.property");
        System.clearProperty("id.property");
        System.clearProperty("metadata.prefix");
        System.clearProperty("dimension");
        System.clearProperty("with.encryption");
        System.clearProperty("connection.timeout");
        System.clearProperty("max.connection.lifetime");
        System.clearProperty("max.connection.pool.size");
        System.clearProperty("connection.acquisition.timeout");
        System.clearProperty("await.index.timeout");
        System.clearProperty("retrieval.query");
        System.clearProperty("entity.creation.query");
        System.clearProperty("full.text.index.name");
        System.clearProperty("full.text.query");
        System.clearProperty("full.text.retrieval.query");
        System.clearProperty("auto.create.full.text");
    }

    @Test
    public void shouldCreateNeo4jProviderInstance() {
        LOG.info("Testing Neo4j provider instantiation");
        Neo4jProvider provider = new Neo4jProvider();
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();
        try {
            EmbeddingStore<TextSegment> neo4jboo = provider.create();
        } catch (org.neo4j.driver.exceptions.ServiceUnavailableException sue) {
            LOG.info("Expected to catch RuntimeException creating Neo4j Embedding Store and did successfully...");
        } catch (Exception e) {
            fail("Caught exception trying to create Neo4j Embedding Store {}", e);
        }
        LOG.info("Successfully created Neo4j provider");
    }
}
