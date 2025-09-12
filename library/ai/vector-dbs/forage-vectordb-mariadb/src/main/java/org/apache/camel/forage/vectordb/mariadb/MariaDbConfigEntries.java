package org.apache.camel.forage.vectordb.mariadb;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;

/**
 * Configuration entries for MariaDB vector database provider.
 *
 * <p>This class defines all available configuration parameters for the MariaDB provider
 * and manages their registration and resolution. It supports both default and prefixed
 * configurations for multi-instance setups.</p>
 *
 * <p>Available configuration properties:</p>
 * <ul>
 *   <li><strong>Connection:</strong> url, user, password</li>
 *   <li><strong>Table Structure:</strong> table, id.field.name, embedding.field.name, content.field.name</li>
 *   <li><strong>Vector Configuration:</strong> distance.type, dimension</li>
 *   <li><strong>Table Management:</strong> create.table, drop.table.first</li>
 * </ul>
 *
 * @see MariaDbConfig
 * @see ConfigEntries
 */
public final class MariaDbConfigEntries extends ConfigEntries {
    // Connection configuration
    /** MariaDB JDBC URL (e.g., jdbc:mariadb://localhost:3306/vectordb) */
    public static final ConfigModule URL = ConfigModule.of(MariaDbConfig.class, "mariadb.url");
    /** Database username for authentication */
    public static final ConfigModule USER = ConfigModule.of(MariaDbConfig.class, "mariadb.user");
    /** Database password for authentication (optional) */
    public static final ConfigModule PASSWORD = ConfigModule.of(MariaDbConfig.class, "mariadb.password");

    // Table structure configuration
    /** Name of the table to store embeddings (default: "embeddings") */
    public static final ConfigModule TABLE = ConfigModule.of(MariaDbConfig.class, "mariadb.table");
    /** Name of the ID field in the embeddings table (default: "id") */
    public static final ConfigModule ID_FIELD_NAME = ConfigModule.of(MariaDbConfig.class, "mariadb.id.field.name");
    /** Name of the embedding vector field (default: "embedding") */
    public static final ConfigModule EMBEDDING_FIELD_NAME =
            ConfigModule.of(MariaDbConfig.class, "mariadb.embedding.field.name");
    /** Name of the content text field (default: "content") */
    public static final ConfigModule CONTENT_FIELD_NAME =
            ConfigModule.of(MariaDbConfig.class, "mariadb.content.field.name");

    // Vector configuration
    /** Distance calculation method: COSINE or EUCLIDEAN (default: COSINE) */
    public static final ConfigModule DISTANCE_TYPE = ConfigModule.of(MariaDbConfig.class, "mariadb.distance.type");
    /** Vector dimension size (optional, auto-detected if not specified) */
    public static final ConfigModule DIMENSION = ConfigModule.of(MariaDbConfig.class, "mariadb.dimension");

    // Table management configuration
    /** Whether to create the table if it doesn't exist (default: true) */
    public static final ConfigModule CREATE_TABLE = ConfigModule.of(MariaDbConfig.class, "mariadb.create.table");
    /** Whether to drop the table before creating it (default: false) */
    public static final ConfigModule DROP_TABLE_FIRST =
            ConfigModule.of(MariaDbConfig.class, "mariadb.drop.table.first");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(URL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USER, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PASSWORD, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TABLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DISTANCE_TYPE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ID_FIELD_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(EMBEDDING_FIELD_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CONTENT_FIELD_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CREATE_TABLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DROP_TABLE_FIRST, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DIMENSION, ConfigEntry.fromModule());
    }

    /**
     * Returns an unmodifiable view of all configuration modules.
     *
     * @return map of configuration modules to their entries
     */
    public static Map<ConfigModule, ConfigEntry> entries() {
        return Collections.unmodifiableMap(CONFIG_MODULES);
    }

    /**
     * Finds a configuration module by prefix and property name.
     *
     * @param prefix optional prefix for scoped configuration
     * @param name the property name to find
     * @return the matching configuration module, if found
     */
    public static Optional<ConfigModule> find(String prefix, String name) {
        return find(CONFIG_MODULES, prefix, name);
    }

    /**
     * Registers new prefixed configuration modules for multi-instance setups.
     *
     * <p>If a prefix is provided, creates prefixed versions of all configuration
     * modules (e.g., "agent1.mariadb.url", "agent2.mariadb.user").</p>
     *
     * @param prefix the prefix to register, or null for default configuration
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
     * Loads configuration overrides from environment variables and system properties.
     *
     * <p>This method applies the highest precedence configuration values from
     * system environment and properties, respecting the prefix if provided.</p>
     *
     * @param prefix optional prefix for scoped configuration
     */
    public static void loadOverrides(String prefix) {
        load(CONFIG_MODULES, prefix);
    }
}
