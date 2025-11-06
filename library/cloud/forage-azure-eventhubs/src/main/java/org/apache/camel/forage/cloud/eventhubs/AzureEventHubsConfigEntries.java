package org.apache.camel.forage.cloud.eventhubs;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigTag;

public final class AzureEventHubsConfigEntries extends ConfigEntries {

    public static final ConfigModule CONSUMER_GROUP = ConfigModule.of(
            AzureEventHubsConfig.class,
            "azure.eventhubs.consumer.group",
            "The consumer group name for Event Hubs",
            "Consumer Group",
            EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME,
            "string",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule EVENTHUB_NAME = ConfigModule.of(
            AzureEventHubsConfig.class,
            "azure.eventhubs.eventhub.name",
            "The Event Hub name",
            "Event Hub Name",
            null,
            "string",
            true,
            ConfigTag.COMMON);

    public static final ConfigModule FULLY_QUALIFIED_NAMESPACE = ConfigModule.of(
            AzureEventHubsConfig.class,
            "azure.eventhubs.fully.qualified.namespace",
            "The fully qualified namespace for Event Hubs (e.g., <namespace>.servicebus.windows.net)",
            "Fully Qualified Namespace",
            null,
            "string",
            true,
            ConfigTag.COMMON);

    public static final ConfigModule PREFETCH_COUNT = ConfigModule.of(
            AzureEventHubsConfig.class,
            "azure.eventhubs.prefetch.count",
            "The number of events to prefetch from Event Hubs",
            "Prefetch Count",
            "100",
            "integer",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule CUSTOM_ENDPOINT_ADDRESS = ConfigModule.of(
            AzureEventHubsConfig.class,
            "azure.eventhubs.custom.endpoint.address",
            "Custom endpoint address for Event Hubs (optional, for custom Azure environments)",
            "Custom Endpoint",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(CONSUMER_GROUP, ConfigEntry.fromModule());
        CONFIG_MODULES.put(EVENTHUB_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(FULLY_QUALIFIED_NAMESPACE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PREFETCH_COUNT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CUSTOM_ENDPOINT_ADDRESS, ConfigEntry.fromModule());
    }

    public static Map<ConfigModule, ConfigEntry> entries() {
        return Collections.unmodifiableMap(CONFIG_MODULES);
    }

    public static Optional<ConfigModule> find(String prefix, String name) {
        return find(CONFIG_MODULES, prefix, name);
    }

    public static void register(String prefix) {
        if (prefix != null) {
            for (Map.Entry<ConfigModule, ConfigEntry> entry : entries().entrySet()) {
                ConfigModule configModule = entry.getKey().asNamed(prefix);
                CONFIG_MODULES.put(configModule, ConfigEntry.fromModule());
            }
        }
    }

    public static void loadOverrides(String prefix) {
        load(CONFIG_MODULES, prefix);
    }
}
