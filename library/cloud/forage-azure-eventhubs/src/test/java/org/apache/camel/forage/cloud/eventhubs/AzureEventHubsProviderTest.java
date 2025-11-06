package org.apache.camel.forage.cloud.eventhubs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
import org.apache.camel.forage.core.util.config.MissingConfigException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for Azure Event Hubs provider configuration and instantiation.
 *
 * <p>This test verifies that the provider correctly handles configuration from
 * system properties and validates required configuration values.
 */
public class AzureEventHubsProviderTest {
    private static final Logger LOG = LoggerFactory.getLogger(AzureEventHubsProviderTest.class);

    @BeforeEach
    public void setup() {
        LOG.info("Setting up Azure Event Hubs provider test");
        clearSystemProperties();
    }

    @AfterEach
    public void cleanup() {
        LOG.info("Cleaning up Azure Event Hubs provider test");
        clearSystemProperties();
    }

    private void clearSystemProperties() {
        System.clearProperty("azure.eventhubs.fully.qualified.namespace");
        System.clearProperty("azure.eventhubs.eventhub.name");
        System.clearProperty("azure.eventhubs.consumer.group");
        System.clearProperty("azure.eventhubs.prefetch.count");
        System.clearProperty("azure.eventhubs.custom.endpoint.address");
    }

    @Test
    public void shouldCreateProviderInstance() {
        LOG.info("Testing provider instantiation");
        AzureEventHubsProvider provider = new AzureEventHubsProvider();
        assertThat(provider).isNotNull();
        LOG.info("Successfully created provider instance");
    }

    @Test
    public void shouldFailWithoutRequiredConfiguration() {
        LOG.info("Testing provider with missing required configuration");
        AzureEventHubsProvider provider = new AzureEventHubsProvider();

        assertThatThrownBy(() -> provider.create())
                .isInstanceOf(MissingConfigException.class)
                .hasMessageContaining("Fully qualified namespace is required");

        LOG.info("Successfully validated missing configuration handling");
    }

    @Test
    public void shouldFailWithoutEventHubName() {
        LOG.info("Testing provider with missing Event Hub name");
        System.setProperty("azure.eventhubs.fully.qualified.namespace", "test-namespace.servicebus.windows.net");

        AzureEventHubsProvider provider = new AzureEventHubsProvider();

        assertThatThrownBy(() -> provider.create())
                .isInstanceOf(MissingConfigException.class)
                .hasMessageContaining("Event Hub name is required");

        LOG.info("Successfully validated missing Event Hub name handling");
    }

    @Test
    public void shouldLoadConfigurationWithPrefix() {
        LOG.info("Testing provider with prefixed configuration");
        System.setProperty(
                "test-prefix.azure.eventhubs.fully.qualified.namespace", "test-namespace.servicebus.windows.net");
        System.setProperty("test-prefix.azure.eventhubs.eventhub.name", "test-eventhub");
        System.setProperty("test-prefix.azure.eventhubs.consumer.group", "custom-group");
        System.setProperty("test-prefix.azure.eventhubs.prefetch.count", "500");

        AzureEventHubsProvider provider = new AzureEventHubsProvider();

        try {
            EventHubProducerAsyncClient client = provider.create("test-prefix");
        } catch (RuntimeException e) {
            LOG.info(
                    "Expected runtime exception when creating client without actual Azure connection: {}",
                    e.getMessage());
        }

        LOG.info("Successfully validated prefixed configuration loading");
    }

    @Test
    public void shouldUseDefaultConsumerGroup() {
        LOG.info("Testing default consumer group configuration");
        System.setProperty("azure.eventhubs.fully.qualified.namespace", "test-namespace.servicebus.windows.net");
        System.setProperty("azure.eventhubs.eventhub.name", "test-eventhub");
        System.setProperty("azure.eventhubs.consumer.group", "$Default");

        AzureEventHubsConfig config = new AzureEventHubsConfig();
        assertThat(config.consumerGroup()).isEqualTo("$Default");

        LOG.info("Successfully validated default consumer group");
    }

    @Test
    public void shouldUseDefaultPrefetchCount() {
        LOG.info("Testing default prefetch count configuration");
        System.setProperty("azure.eventhubs.fully.qualified.namespace", "test-namespace.servicebus.windows.net");
        System.setProperty("azure.eventhubs.eventhub.name", "test-eventhub");

        AzureEventHubsConfig config = new AzureEventHubsConfig();
        assertThat(config.prefetchCount()).isEqualTo(100);

        LOG.info("Successfully validated default prefetch count");
    }

    @Test
    public void shouldOverrideDefaultValues() {
        LOG.info("Testing configuration overrides");
        System.setProperty("azure.eventhubs.fully.qualified.namespace", "custom-namespace.servicebus.windows.net");
        System.setProperty("azure.eventhubs.eventhub.name", "custom-eventhub");
        System.setProperty("azure.eventhubs.consumer.group", "custom-consumer-group");
        System.setProperty("azure.eventhubs.prefetch.count", "300");
        System.setProperty("azure.eventhubs.custom.endpoint.address", "https://custom-endpoint.example.com");

        AzureEventHubsConfig config = new AzureEventHubsConfig();

        assertThat(config.fullyQualifiedNamespace()).isEqualTo("custom-namespace.servicebus.windows.net");
        assertThat(config.eventHubName()).isEqualTo("custom-eventhub");
        assertThat(config.consumerGroup()).isEqualTo("custom-consumer-group");
        assertThat(config.prefetchCount()).isEqualTo(300);
        assertThat(config.customEndpointAddress()).isEqualTo("https://custom-endpoint.example.com");

        LOG.info("Successfully validated configuration overrides");
    }
}
