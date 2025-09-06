package org.apache.camel.forage.vectordb.infinispan;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;

public final class InfinispanConfigEntries extends ConfigEntries {
    public static final ConfigModule CACHE_NAME = ConfigModule.of(InfinispanConfig.class, "infinispan.cache.name");
    public static final ConfigModule DIMENSION = ConfigModule.of(InfinispanConfig.class, "infinispan.dimension");
    public static final ConfigModule DISTANCE = ConfigModule.of(InfinispanConfig.class, "infinispan.distance");
    public static final ConfigModule SIMILARITY = ConfigModule.of(InfinispanConfig.class, "infinispan.similarity");
    public static final ConfigModule CACHE_CONFIG = ConfigModule.of(InfinispanConfig.class, "infinispan.cache.config");
    public static final ConfigModule PACKAGE_NAME = ConfigModule.of(InfinispanConfig.class, "infinispan.package.name");
    public static final ConfigModule FILE_NAME = ConfigModule.of(InfinispanConfig.class, "infinispan.file.name");
    public static final ConfigModule LANGCHAIN_ITEM_NAME =
            ConfigModule.of(InfinispanConfig.class, "infinispan.langchain.item.name");
    public static final ConfigModule METADATA_ITEM_NAME =
            ConfigModule.of(InfinispanConfig.class, "infinispan.metadata.item.name");
    public static final ConfigModule REGISTER_SCHEMA =
            ConfigModule.of(InfinispanConfig.class, "infinispan.register.schema");
    public static final ConfigModule CREATE_CACHE = ConfigModule.of(InfinispanConfig.class, "infinispan.create.cache");
    public static final ConfigModule HOST = ConfigModule.of(InfinispanConfig.class, "infinispan.host");
    public static final ConfigModule PORT = ConfigModule.of(InfinispanConfig.class, "infinispan.port");
    public static final ConfigModule USERNAME = ConfigModule.of(InfinispanConfig.class, "infinispan.username");
    public static final ConfigModule PASSWORD = ConfigModule.of(InfinispanConfig.class, "infinispan.password");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(CACHE_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DIMENSION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DISTANCE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(SIMILARITY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CACHE_CONFIG, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PACKAGE_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(FILE_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(LANGCHAIN_ITEM_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(METADATA_ITEM_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(REGISTER_SCHEMA, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CREATE_CACHE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(HOST, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PORT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USERNAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PASSWORD, ConfigEntry.fromModule());
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
