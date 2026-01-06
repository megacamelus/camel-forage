package io.kaoto.forage.vectordb.infinispan;

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
 * Unit test for Infinispan vector database using file-based configuration.
 *
 * <p>This test demonstrates how the Camel Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-vectordb-infinispan.properties' file
 * on the classpath.
 */
public class InfinispanFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(InfinispanFileConfigTest.class);

    private static final String PROPERTIES_FILE = "forage-vectordb-infinispan.properties";

    @BeforeAll
    public static void setupInfinispanFileConfiguration() {
        LOG.info("Setting up Infinispan file-based configuration test");
        clearInfinispanSystemProperties();
        copyPropertiesFile();
        LOG.info("Infinispan file-based configuration setup complete");
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
    public static void teardownInfinispanFileConfiguration() {
        LOG.info("Cleaning up Infinispan file-based configuration");
        clearInfinispanSystemProperties();
        removePropertiesFile();
        LOG.info("Infinispan file-based configuration cleanup complete");
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

    private static void clearInfinispanSystemProperties() {
        // Clear all possible infinispan system properties based on InfinispanConfig
        System.clearProperty("infinispan.cache.name");
        System.clearProperty("infinispan.dimension");
        System.clearProperty("infinispan.distance");
        System.clearProperty("infinispan.similarity");
        System.clearProperty("infinispan.cache.config");
        System.clearProperty("infinispan.package.name");
        System.clearProperty("infinispan.file.name");
        System.clearProperty("infinispan.langchain.item.name");
        System.clearProperty("infinispan.metadata.item.name");
        System.clearProperty("infinispan.register.schema");
        System.clearProperty("infinispan.create.cache");
        System.clearProperty("infinispan.host");
        System.clearProperty("infinispan.port");
        System.clearProperty("infinispan.username");
        System.clearProperty("infinispan.password");

        // Also clear the simple property names (without infinispan prefix)
        System.clearProperty("cache.name");
        System.clearProperty("dimension");
        System.clearProperty("distance");
        System.clearProperty("similarity");
        System.clearProperty("cache.config");
        System.clearProperty("package.name");
        System.clearProperty("file.name");
        System.clearProperty("langchain.item.name");
        System.clearProperty("metadata.item.name");
        System.clearProperty("register.schema");
        System.clearProperty("create.cache");
        System.clearProperty("host");
        System.clearProperty("port");
        System.clearProperty("username");
        System.clearProperty("password");
    }

    @Test
    public void shouldCreateInfinispanProviderInstance() {
        LOG.info("Testing Infinispan provider instantiation");
        InfinispanProvider provider = new InfinispanProvider();
        try {
            EmbeddingStore<TextSegment> inf = provider.create();
        } catch (org.infinispan.client.hotrod.exceptions.TransportException te) {
            LOG.info("Expected to catch TransportException, did successfully");
        } catch (Exception e) {
            fail("Caught exception trying to create Infinispan Embedding Store {}", e);
        }
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();
        LOG.info("Successfully created Infinispan provider");
    }
}
