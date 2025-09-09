package org.apache.camel.forage.vectordb.redis;

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
 * Unit test for Redis vector database using file-based configuration.
 *
 * <p>This test demonstrates how the Camel Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-vectordb-redis.properties' file
 * on the classpath.
 */
public class RedisFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(RedisFileConfigTest.class);

    private static final String PROPERTIES_FILE = "forage-vectordb-redis.properties";

    @BeforeAll
    public static void setupRedisFileConfiguration() {
        LOG.info("Setting up Redis file-based configuration test");
        clearRedisSystemProperties();
        copyPropertiesFile();
        LOG.info("Redis file-based configuration setup complete");
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
    public static void teardownRedisFileConfiguration() {
        LOG.info("Cleaning up Redis file-based configuration");
        clearRedisSystemProperties();
        removePropertiesFile();
        LOG.info("Redis file-based configuration cleanup complete");
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

    private static void clearRedisSystemProperties() {
        // Clear all possible redis system properties based on RedisConfig
        System.clearProperty("redis.host");
        System.clearProperty("redis.port");
        System.clearProperty("redis.user");
        System.clearProperty("redis.password");
        System.clearProperty("redis.dimension");
        System.clearProperty("redis.prefix");
        System.clearProperty("redis.index.name");
        System.clearProperty("redis.metadata.fields");
        System.clearProperty("redis.distance.metric");

        // Also clear the simple property names (without redis prefix)
        System.clearProperty("host");
        System.clearProperty("port");
        System.clearProperty("user");
        System.clearProperty("password");
        System.clearProperty("dimension");
        System.clearProperty("prefix");
        System.clearProperty("index.name");
        System.clearProperty("metadata.fields");
        System.clearProperty("distance.metric");
    }

    @Test
    public void shouldCreateRedisProviderInstance() {
        LOG.info("Testing Redis provider instantiation");
        RedisProvider provider = new RedisProvider();
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();
        try {
            EmbeddingStore<TextSegment> redisfoo = provider.create();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException jce) {
            LOG.info("Expected to catch RuntimeException creating Redis Embedding Store and did successfully...");
        } catch (Exception e) {
            fail("Caught exception trying to create Redis Embedding Store {}", e);
        }
        LOG.info("Successfully created Redis provider");
    }
}
