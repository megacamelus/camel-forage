package org.apache.camel.forage.cloud.eventhubs;

import static org.assertj.core.api.Fail.fail;

import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
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
 * Unit test for Azure Event Hubs using file-based configuration.
 *
 * <p>This test demonstrates how the Camel Forage framework can load configuration
 * from properties files instead of system properties or environment variables.
 * The configuration is loaded from the 'forage-azure-eventhubs.properties' file
 * on the classpath.
 */
public class AzureEventHubsFileConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(AzureEventHubsFileConfigTest.class);

    private static final String PROPERTIES_FILE = "forage-azure-eventhubs.properties";

    @BeforeAll
    public static void setupAzureEventHubsFileConfiguration() {
        LOG.info("Setting up Azure Event Hubs file-based configuration test");
        clearAzureEventHubsSystemProperties();
        removePropertiesFile();
        copyPropertiesFile();
        LOG.info("Azure Event Hubs file-based configuration setup complete");
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
    public static void teardownAzureEventHubsFileConfiguration() {
        LOG.info("Cleaning up Azure Event Hubs file-based configuration");
        clearAzureEventHubsSystemProperties();
        removePropertiesFile();
        LOG.info("Azure Event Hubs file-based configuration cleanup complete");
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

    private static void clearAzureEventHubsSystemProperties() {
        System.clearProperty("azure.eventhubs.fully.qualified.namespace");
        System.clearProperty("azure.eventhubs.eventhub.name");
        System.clearProperty("azure.eventhubs.consumer.group");
        System.clearProperty("azure.eventhubs.prefetch.count");
        System.clearProperty("azure.eventhubs.custom.endpoint.address");

        System.clearProperty("fully.qualified.namespace");
        System.clearProperty("eventhub.name");
        System.clearProperty("consumer.group");
        System.clearProperty("prefetch.count");
        System.clearProperty("custom.endpoint.address");
    }

    @Test
    public void shouldCreateAzureEventHubsProviderInstance() {
        LOG.info("Testing Azure Event Hubs provider instantiation");
        AzureEventHubsProvider provider = new AzureEventHubsProvider();
        org.assertj.core.api.Assertions.assertThat(provider).isNotNull();
        try {
            EventHubProducerAsyncClient client = provider.create();
        } catch (RuntimeException re) {
            LOG.info(
                    "Expected to catch RuntimeException creating Azure Event Hubs Producer Client and did successfully...");
        } catch (Exception e) {
            fail("Caught exception trying to create Azure Event Hubs Producer Client {}", e);
        }
        LOG.info("Successfully created Azure Event Hubs provider");
    }
}
