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

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
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
