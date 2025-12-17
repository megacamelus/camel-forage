package org.apache.camel.forage.memory.chat.redis;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigTag;

public final class RedisConfigEntries extends ConfigEntries {
    public static final ConfigModule HOST = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.host",
            "Redis server hostname or IP address",
            "Host",
            "localhost",
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule PORT = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.port",
            "Redis server port number",
            "Port",
            "6379",
            "integer",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule PASSWORD = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.password",
            "Redis authentication password (optional)",
            "Password",
            null,
            "password",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule DATABASE = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.database",
            "Redis database number to connect to",
            "Database",
            "0",
            "integer",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule TIMEOUT = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.timeout",
            "Connection timeout in milliseconds",
            "Timeout",
            "2000",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule POOL_MAX_TOTAL = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.pool.max-total",
            "Maximum number of connections in the pool",
            "Pool Max Total",
            "10",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule POOL_MAX_IDLE = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.pool.max-idle",
            "Maximum number of idle connections in the pool",
            "Pool Max Idle",
            "5",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule POOL_MIN_IDLE = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.pool.min-idle",
            "Minimum number of idle connections in the pool",
            "Pool Min Idle",
            "1",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule POOL_TEST_ON_BORROW = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.pool.test-on-borrow",
            "Test connections when borrowing from pool",
            "Test On Borrow",
            "true",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule POOL_TEST_ON_RETURN = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.pool.test-on-return",
            "Test connections when returning to pool",
            "Test On Return",
            "true",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule POOL_TEST_WHILE_IDLE = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.pool.test-while-idle",
            "Test idle connections periodically",
            "Test While Idle",
            "true",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule POOL_MAX_WAIT_MILLIS = ConfigModule.of(
            RedisConfig.class,
            "forage.redis.pool.max-wait-millis",
            "Maximum time to wait for a connection from the pool in milliseconds",
            "Pool Max Wait",
            "2000",
            "integer",
            false,
            ConfigTag.ADVANCED);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(HOST, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PORT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PASSWORD, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DATABASE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TIMEOUT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(POOL_MAX_TOTAL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(POOL_MAX_IDLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(POOL_MIN_IDLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(POOL_TEST_ON_BORROW, ConfigEntry.fromModule());
        CONFIG_MODULES.put(POOL_TEST_ON_RETURN, ConfigEntry.fromModule());
        CONFIG_MODULES.put(POOL_TEST_WHILE_IDLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(POOL_MAX_WAIT_MILLIS, ConfigEntry.fromModule());
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
