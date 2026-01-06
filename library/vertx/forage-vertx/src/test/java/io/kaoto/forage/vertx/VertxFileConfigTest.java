package io.kaoto.forage.vertx;

import static org.assertj.core.api.Fail.fail;

import io.vertx.core.Vertx;
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
 * Unit test for Vert.x using file-based configuration.
 *
 * <p>This test demonstrates how the Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-vertx.properties' file
 * on the classpath.
 */
public class VertxFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(VertxFileConfigTest.class);

    private static final String PROPERTIES_FILE = "forage-vertx.properties";

    @BeforeAll
    public static void setupVertxFileConfiguration() {
        LOG.info("Setting up Vert.x file-based configuration test");
        clearVertxSystemProperties();
        copyPropertiesFile();
        LOG.info("Vert.x file-based configuration setup complete");
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
    public static void teardownVertxFileConfiguration() {
        LOG.info("Cleaning up Vert.x file-based configuration");
        clearVertxSystemProperties();
        removePropertiesFile();
        LOG.info("Vert.x file-based configuration cleanup complete");
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

    private static void clearVertxSystemProperties() {
        System.clearProperty("forage.vertx.worker.pool.size");
        System.clearProperty("forage.vertx.event.loop.pool.size");
        System.clearProperty("forage.vertx.clustered");

        System.clearProperty("forage.worker.pool.size");
        System.clearProperty("forage.event.loop.pool.size");
        System.clearProperty("forage.clustered");
    }

    @Test
    public void shouldCreateVertxProviderInstance() {
        LOG.info("Testing Vert.x provider instantiation");
        DefaultVertxProvider provider = new DefaultVertxProvider();
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();
        try {
            Vertx vertx = provider.create();
            org.assertj.core.api.Assertions.assertThat(vertx).isNotNull();
            LOG.info("Successfully created Vert.x instance");
            vertx.close();
        } catch (Exception e) {
            fail("Caught exception trying to create Vert.x instance: {}", e);
        }
        LOG.info("Successfully created Vert.x provider");
    }
}
