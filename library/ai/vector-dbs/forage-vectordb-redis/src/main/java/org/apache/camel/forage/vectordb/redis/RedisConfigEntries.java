package org.apache.camel.forage.vectordb.redis;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;

public final class RedisConfigEntries extends ConfigEntries {
    public static final ConfigModule HOST = ConfigModule.of(RedisConfig.class, "redis.host");
    public static final ConfigModule PORT = ConfigModule.of(RedisConfig.class, "redis.port");
    public static final ConfigModule USER = ConfigModule.of(RedisConfig.class, "redis.user");
    public static final ConfigModule PASSWORD = ConfigModule.of(RedisConfig.class, "redis.password");
    public static final ConfigModule DIMENSION = ConfigModule.of(RedisConfig.class, "redis.dimension");
    public static final ConfigModule PREFIX = ConfigModule.of(RedisConfig.class, "redis.prefix");
    public static final ConfigModule INDEX_NAME = ConfigModule.of(RedisConfig.class, "redis.index.name");
    public static final ConfigModule METADATA_FIELDS = ConfigModule.of(RedisConfig.class, "redis.metadata.fields");
    public static final ConfigModule DISTANCE_METRIC = ConfigModule.of(RedisConfig.class, "redis.distance.metric");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(HOST, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PORT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USER, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PASSWORD, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DIMENSION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PREFIX, ConfigEntry.fromModule());
        CONFIG_MODULES.put(INDEX_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(METADATA_FIELDS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DISTANCE_METRIC, ConfigEntry.fromModule());
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
