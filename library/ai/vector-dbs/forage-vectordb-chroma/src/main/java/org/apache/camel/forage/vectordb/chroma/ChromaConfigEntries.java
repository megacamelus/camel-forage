package org.apache.camel.forage.vectordb.chroma;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;

public final class ChromaConfigEntries extends ConfigEntries {
    public static final ConfigModule URL = ConfigModule.of(ChromaConfig.class, "chroma.url");
    public static final ConfigModule COLLECTION_NAME = ConfigModule.of(ChromaConfig.class, "chroma.collection.name");
    public static final ConfigModule TIMEOUT = ConfigModule.of(ChromaConfig.class, "chroma.timeout");
    public static final ConfigModule LOG_REQUESTS = ConfigModule.of(ChromaConfig.class, "chroma.log.requests");
    public static final ConfigModule LOG_RESPONSES = ConfigModule.of(ChromaConfig.class, "chroma.log.responses");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(URL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(COLLECTION_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TIMEOUT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(LOG_REQUESTS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(LOG_RESPONSES, ConfigEntry.fromModule());
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
