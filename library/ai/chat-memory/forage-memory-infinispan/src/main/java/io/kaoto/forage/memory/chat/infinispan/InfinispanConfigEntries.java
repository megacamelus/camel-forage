package io.kaoto.forage.memory.chat.infinispan;

import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigTag;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InfinispanConfigEntries extends ConfigEntries {
    public static final ConfigModule SERVER_LIST = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.server-list",
            "Comma-separated list of Infinispan server addresses in format 'host1:port1,host2:port2'",
            "Server List",
            "localhost:11222",
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule CACHE_NAME = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.cache-name",
            "Name of the cache for storing chat messages",
            "Cache Name",
            "chat-memory",
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule USERNAME = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.username",
            "Username for authentication (optional)",
            "Username",
            null,
            "string",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule PASSWORD = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.password",
            "Password for authentication (optional)",
            "Password",
            null,
            "password",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule REALM = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.realm",
            "Security realm for authentication",
            "Realm",
            "default",
            "string",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule SASL_MECHANISM = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.sasl-mechanism",
            "SASL mechanism for authentication",
            "SASL Mechanism",
            "DIGEST-MD5",
            "string",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule CONNECTION_TIMEOUT = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.connection-timeout",
            "Connection timeout in milliseconds",
            "Connection Timeout",
            "60000",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule SOCKET_TIMEOUT = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.socket-timeout",
            "Socket timeout in milliseconds",
            "Socket Timeout",
            "60000",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule MAX_RETRIES = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.max-retries",
            "Maximum number of connection retries",
            "Max Retries",
            "3",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule POOL_MAX_ACTIVE = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.pool.max-active",
            "Maximum number of active connections per server",
            "Pool Max Active",
            "20",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule POOL_MIN_IDLE = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.pool.min-idle",
            "Minimum number of idle connections per server",
            "Pool Min Idle",
            "1",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule POOL_MAX_WAIT = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.pool.max-wait",
            "Maximum time to wait for a connection in milliseconds",
            "Pool Max Wait",
            "3000",
            "integer",
            false,
            ConfigTag.ADVANCED);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(SERVER_LIST, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CACHE_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USERNAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PASSWORD, ConfigEntry.fromModule());
        CONFIG_MODULES.put(REALM, ConfigEntry.fromModule());
        CONFIG_MODULES.put(SASL_MECHANISM, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CONNECTION_TIMEOUT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(SOCKET_TIMEOUT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_RETRIES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(POOL_MAX_ACTIVE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(POOL_MIN_IDLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(POOL_MAX_WAIT, ConfigEntry.fromModule());
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
