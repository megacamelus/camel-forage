package io.kaoto.forage.vectordb.redis;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigTag;

public final class RedisConfigEntries extends ConfigEntries {
    public static final ConfigModule HOST = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.host",
            "Redis server host address",
            "Host",
            "localhost",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule PORT = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.port",
            "Redis server port number",
            "Port",
            "6379",
            "integer",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule USER = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.user",
            "Username for authentication",
            "User",
            null,
            "string",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule PASSWORD = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.password",
            "Password for authentication",
            "Password",
            null,
            "password",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule DIMENSION = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.dimension",
            "Vector dimension size",
            "Dimension",
            null,
            "integer",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule PREFIX = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.prefix",
            "Key prefix for vector storage",
            "Prefix",
            "embedding:",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule INDEX_NAME = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.index.name",
            "Name of the vector index",
            "Index Name",
            "embedding-index",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule METADATA_FIELDS = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.metadata.fields",
            "Comma-separated list of metadata fields",
            "Metadata Fields",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule DISTANCE_METRIC = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.distance.metric",
            "Distance metric for similarity search (COSINE, L2, IP)",
            "Distance Metric",
            "COSINE",
            "string",
            false,
            ConfigTag.ADVANCED);

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
