package org.apache.camel.forage.jdbc.common;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;

/**
 * Configuration entries for data source factory with JDBC connection and pool settings.
 */
public class DataSourceFactoryConfigEntries extends ConfigEntries {
    // Database connection configuration
    public static final ConfigModule DB_KIND = ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.db.kind");
    public static final ConfigModule JDBC_URL = ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.url");
    public static final ConfigModule CONNECTION_PROVIDER_CLASS_NAME =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.connection.provider.class.name");
    public static final ConfigModule USERNAME = ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.username");
    public static final ConfigModule PASSWORD = ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.password");
    // Connection pool configuration
    public static final ConfigModule INITIAL_SIZE =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.pool.initial.size");
    public static final ConfigModule MIN_SIZE = ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.pool.min.size");
    public static final ConfigModule MAX_SIZE = ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.pool.max.size");
    public static final ConfigModule ACQUISITION_TIMEOUT_SECONDS =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.pool.acquisition.timeout.seconds");
    public static final ConfigModule VALIDATION_TIMEOUT_SECONDS =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.pool.validation.timeout.seconds");
    public static final ConfigModule LEAK_TIMEOUT_MINUTES =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.pool.leak.timeout.minutes");
    public static final ConfigModule IDLE_VALIDATION_TIMEOUT_MINUTES =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.pool.idle.validation.timeout.minutes");
    // Transaction configuration
    public static final ConfigModule TRANSACTION_TIMEOUT_SECONDS =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.timeout.seconds");
    // Provider configuration
    public static final ConfigModule PROVIDER_DATASOURCE_CLASS =
            ConfigModule.of(DataSourceFactoryConfig.class, "provider.datasource.class");

    public static final ConfigModule TRANSACTION_ENABLED =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.enabled");
    public static final ConfigModule TRANSACTION_NODE_ID =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.node.id");
    public static final ConfigModule TRANSACTION_OBJECT_STORE_ID =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.object.store.id");
    public static final ConfigModule TRANSACTION_ENABLE_RECOVERY =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.enable.recovery");
    public static final ConfigModule TRANSACTION_RECOVERY_MODULES =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.recovery.modules");
    public static final ConfigModule TRANSACTION_EXPIRY_SCANNERS =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.expiry.scanners");
    public static final ConfigModule TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.xa.resource.orphan.filters");
    public static final ConfigModule TRANSACTION_OBJECT_STORE_DIRECTORY =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.object.store.directory");
    public static final ConfigModule TRANSACTION_OBJECT_STORE_TYPE =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.object.store.type");
    public static final ConfigModule TRANSACTION_OBJECT_STORE_DATASOURCE =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.object.store.datasource");
    public static final ConfigModule TRANSACTION_OBJECT_STORE_CREATE_TABLE =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.object.store.create.table");
    public static final ConfigModule TRANSACTION_OBJECT_STORE_DROP_TABLE =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.object.store.drop.table");
    public static final ConfigModule TRANSACTION_OBJECT_STORE_TABLE_PREFIX =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.transaction.object.store.table.prefix");
    public static final ConfigModule AGGREGATION_REPOSITORY_NAME =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.aggregation.repository.name");
    public static final ConfigModule AGGREGATION_REPOSITORY_HEADERS_TO_STORE =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.aggregation.repository.headers.to.store");
    public static final ConfigModule AGGREGATION_REPOSITORY_STORE_BODY =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.aggregation.repository.store.body");
    public static final ConfigModule AGGREGATION_REPOSITORY_DEAD_LETTER_URI =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.aggregation.repository.dead.letter.uri");
    public static final ConfigModule AGGREGATION_REPOSITORY_ALLOW_SERIALIZED_HEADERS =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.aggregation.repository.allow.serialized.headers");
    public static final ConfigModule AGGREGATION_REPOSITORY_MAXIMUM_REDELIVERIES =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.aggregation.repository.maximum.redeliveries");
    public static final ConfigModule AGGREGATION_REPOSITORY_USE_RECOVERY =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.aggregation.repository.use.recovery");
    public static final ConfigModule AGGREGATION_REPOSITORY_PROPAGATION_BEHAVIOUR_NAME =
            ConfigModule.of(DataSourceFactoryConfig.class, "jdbc.aggregation.repository.propagation.behaviour.name");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(DB_KIND, ConfigEntry.fromModule());
        CONFIG_MODULES.put(JDBC_URL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CONNECTION_PROVIDER_CLASS_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USERNAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PASSWORD, ConfigEntry.fromModule());
        CONFIG_MODULES.put(INITIAL_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MIN_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ACQUISITION_TIMEOUT_SECONDS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(VALIDATION_TIMEOUT_SECONDS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(LEAK_TIMEOUT_MINUTES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(IDLE_VALIDATION_TIMEOUT_MINUTES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_TIMEOUT_SECONDS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PROVIDER_DATASOURCE_CLASS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_ENABLED, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_NODE_ID, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_ID, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_ENABLE_RECOVERY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_RECOVERY_MODULES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_EXPIRY_SCANNERS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_DIRECTORY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_TYPE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_DATASOURCE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_CREATE_TABLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_DROP_TABLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_TABLE_PREFIX, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_HEADERS_TO_STORE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_STORE_BODY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_DEAD_LETTER_URI, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_ALLOW_SERIALIZED_HEADERS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_MAXIMUM_REDELIVERIES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_USE_RECOVERY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_PROPAGATION_BEHAVIOUR_NAME, ConfigEntry.fromModule());
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
