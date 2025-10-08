package org.apache.camel.forage.jms.common;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;

/**
 * Configuration entries for JMS connection factory with connection and pool settings.
 */
public class ConnectionFactoryConfigEntries extends ConfigEntries {
    // JMS connection configuration
    public static final ConfigModule JMS_KIND = ConfigModule.of(ConnectionFactoryConfig.class, "jms.kind");
    public static final ConfigModule BROKER_URL = ConfigModule.of(ConnectionFactoryConfig.class, "jms.broker.url");
    public static final ConfigModule USERNAME = ConfigModule.of(ConnectionFactoryConfig.class, "jms.username");
    public static final ConfigModule PASSWORD = ConfigModule.of(ConnectionFactoryConfig.class, "jms.password");
    public static final ConfigModule CLIENT_ID = ConfigModule.of(ConnectionFactoryConfig.class, "jms.client.id");

    // Connection pool configuration
    public static final ConfigModule POOL_ENABLED = ConfigModule.of(ConnectionFactoryConfig.class, "jms.pool.enabled");
    public static final ConfigModule MAX_CONNECTIONS =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.pool.max.connections");
    public static final ConfigModule MAX_SESSIONS_PER_CONNECTION =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.pool.max.sessions.per.connection");
    public static final ConfigModule IDLE_TIMEOUT_MILLIS =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.pool.idle.timeout.millis");
    public static final ConfigModule EXPIRY_TIMEOUT_MILLIS =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.pool.expiry.timeout.millis");
    public static final ConfigModule CONNECTION_TIMEOUT_MILLIS =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.pool.connection.timeout.millis");
    public static final ConfigModule BLOCK_IF_FULL =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.pool.block.if.full");
    public static final ConfigModule BLOCK_IF_FULL_TIMEOUT_MILLIS =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.pool.block.if.full.timeout.millis");

    // Transaction configuration
    public static final ConfigModule TRANSACTION_ENABLED =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.transaction.enabled");
    public static final ConfigModule TRANSACTION_TIMEOUT_SECONDS =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.transaction.timeout.seconds");
    public static final ConfigModule TRANSACTION_NODE_ID =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.transaction.node.id");
    public static final ConfigModule TRANSACTION_OBJECT_STORE_ID =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.transaction.object.store.id");
    public static final ConfigModule TRANSACTION_ENABLE_RECOVERY =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.transaction.enable.recovery");
    public static final ConfigModule TRANSACTION_RECOVERY_MODULES =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.transaction.recovery.modules");
    public static final ConfigModule TRANSACTION_EXPIRY_SCANNERS =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.transaction.expiry.scanners");
    public static final ConfigModule TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.transaction.xa.resource.orphan.filters");
    public static final ConfigModule TRANSACTION_OBJECT_STORE_DIRECTORY =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.transaction.object.store.directory");
    public static final ConfigModule TRANSACTION_OBJECT_STORE_TYPE =
            ConfigModule.of(ConnectionFactoryConfig.class, "jms.transaction.object.store.type");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(JMS_KIND, ConfigEntry.fromModule());
        CONFIG_MODULES.put(BROKER_URL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USERNAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PASSWORD, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CLIENT_ID, ConfigEntry.fromModule());
        CONFIG_MODULES.put(POOL_ENABLED, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_CONNECTIONS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_SESSIONS_PER_CONNECTION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(IDLE_TIMEOUT_MILLIS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(EXPIRY_TIMEOUT_MILLIS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CONNECTION_TIMEOUT_MILLIS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(BLOCK_IF_FULL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(BLOCK_IF_FULL_TIMEOUT_MILLIS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_ENABLED, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_TIMEOUT_SECONDS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_NODE_ID, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_ID, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_ENABLE_RECOVERY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_RECOVERY_MODULES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_EXPIRY_SCANNERS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_DIRECTORY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_TYPE, ConfigEntry.fromModule());
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
