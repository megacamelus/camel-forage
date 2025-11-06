package org.apache.camel.forage.cloud.eventhubs;

import static org.apache.camel.forage.cloud.eventhubs.AzureEventHubsConfigEntries.CONSUMER_GROUP;
import static org.apache.camel.forage.cloud.eventhubs.AzureEventHubsConfigEntries.CUSTOM_ENDPOINT_ADDRESS;
import static org.apache.camel.forage.cloud.eventhubs.AzureEventHubsConfigEntries.EVENTHUB_NAME;
import static org.apache.camel.forage.cloud.eventhubs.AzureEventHubsConfigEntries.FULLY_QUALIFIED_NAMESPACE;
import static org.apache.camel.forage.cloud.eventhubs.AzureEventHubsConfigEntries.PREFETCH_COUNT;

import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

public class AzureEventHubsConfig implements Config {

    private final String prefix;

    public AzureEventHubsConfig() {
        this(null);
    }

    public AzureEventHubsConfig(String prefix) {
        this.prefix = prefix;

        AzureEventHubsConfigEntries.register(prefix);

        ConfigStore.getInstance().load(AzureEventHubsConfig.class, this, this::register);

        AzureEventHubsConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-azure-eventhubs";
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = AzureEventHubsConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    public String consumerGroup() {
        return ConfigStore.getInstance().get(CONSUMER_GROUP.asNamed(prefix)).orElse(CONSUMER_GROUP.defaultValue());
    }

    public String eventHubName() {
        return ConfigStore.getInstance()
                .get(EVENTHUB_NAME.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Event Hub name is required but not configured"));
    }

    public String fullyQualifiedNamespace() {
        return ConfigStore.getInstance()
                .get(FULLY_QUALIFIED_NAMESPACE.asNamed(prefix))
                .orElseThrow(
                        () -> new MissingConfigException("Fully qualified namespace is required but not configured"));
    }

    public int prefetchCount() {
        return ConfigStore.getInstance()
                .get(PREFETCH_COUNT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(PREFETCH_COUNT.defaultValue()));
    }

    public String customEndpointAddress() {
        return ConfigStore.getInstance()
                .get(CUSTOM_ENDPOINT_ADDRESS.asNamed(prefix))
                .orElse(null);
    }
}
