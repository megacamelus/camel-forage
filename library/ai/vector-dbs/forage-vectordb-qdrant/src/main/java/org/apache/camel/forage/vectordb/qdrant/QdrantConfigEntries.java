package org.apache.camel.forage.vectordb.qdrant;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigTag;

public final class QdrantConfigEntries extends ConfigEntries {
    public static final ConfigModule COLLECTION_NAME = ConfigModule.of(
            QdrantConfig.class,
            "forage.qdrant.collection.name",
            "Name of the Qdrant collection",
            "Collection Name",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule HOST = ConfigModule.of(
            QdrantConfig.class,
            "forage.qdrant.host",
            "Qdrant server host address",
            "Host",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule PORT = ConfigModule.of(
            QdrantConfig.class,
            "forage.qdrant.port",
            "Qdrant server port number",
            "Port",
            null,
            "integer",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule USE_TLS = ConfigModule.of(
            QdrantConfig.class,
            "forage.qdrant.use.tls",
            "Enable TLS for secure connections",
            "Use TLS",
            "false",
            "boolean",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule PAYLOAD_TEXT_KEY = ConfigModule.of(
            QdrantConfig.class,
            "forage.qdrant.payload.text.key",
            "Payload key for storing text content",
            "Payload Text Key",
            "text_segment",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule API_KEY = ConfigModule.of(
            QdrantConfig.class,
            "forage.qdrant.api.key",
            "API key for authentication",
            "API Key",
            null,
            "password",
            false,
            ConfigTag.SECURITY);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(COLLECTION_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(HOST, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PORT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USE_TLS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PAYLOAD_TEXT_KEY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(API_KEY, ConfigEntry.fromModule());
    }

    public static Map<ConfigModule, ConfigEntry> entries() {
        return Collections.unmodifiableMap(CONFIG_MODULES);
    }

    public static Optional<ConfigModule> find(String prefix, String name) {
        return find(CONFIG_MODULES, prefix, name);
    }

    /**
     * Registers new known configuration if a prefix is provided (otherwise is ignored)
     * @param prefix the prefix to register
     */
    public static void register(String prefix) {
        if (prefix != null) {
            for (Map.Entry<ConfigModule, ConfigEntry> entry : entries().entrySet()) {
                ConfigModule configModule = entry.getKey().asNamed(prefix);
                CONFIG_MODULES.put(configModule, ConfigEntry.fromModule());
            }
        }
    }

    /**
     * Load override configurations (which are defined via environment variables and/or system properties)
     * @param prefix and optional prefix to use
     */
    public static void loadOverrides(String prefix) {
        load(CONFIG_MODULES, prefix);
    }
}
