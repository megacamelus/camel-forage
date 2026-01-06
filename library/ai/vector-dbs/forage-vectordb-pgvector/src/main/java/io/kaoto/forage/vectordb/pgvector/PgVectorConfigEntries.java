package io.kaoto.forage.vectordb.pgvector;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigTag;

public final class PgVectorConfigEntries extends ConfigEntries {
    public static final ConfigModule HOST = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.host",
            "PostgreSQL server host address",
            "Host",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule PORT = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.port",
            "PostgreSQL server port number",
            "Port",
            null,
            "integer",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule USER = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.user",
            "Database username",
            "User",
            null,
            "string",
            true,
            ConfigTag.SECURITY);
    public static final ConfigModule PASSWORD = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.password",
            "Database password",
            "Password",
            null,
            "password",
            true,
            ConfigTag.SECURITY);
    public static final ConfigModule DATABASE = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.database",
            "Database name",
            "Database",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule TABLE = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.table",
            "Table name for storing vectors",
            "Table",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule DIMENSION = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.dimension",
            "Vector dimension size",
            "Dimension",
            null,
            "integer",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule USE_INDEX = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.use.index",
            "Enable vector index for faster search",
            "Use Index",
            "false",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule INDEX_LIST_SIZE = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.index.list.size",
            "Index list size for IVFFlat index",
            "Index List Size",
            "100",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule CREATE_TABLE = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.create.table",
            "Automatically create table if it does not exist",
            "Create Table",
            "true",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule DROP_TABLE_FIRST = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.drop.table.first",
            "Drop table before creating (for testing)",
            "Drop Table First",
            "false",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule METADATA_STORAGE_CONFIG = ConfigModule.of(
            PgVectorConfig.class,
            "forage.pgvector.metadata.storage.config",
            "Metadata storage configuration",
            "Metadata Storage Config",
            null,
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
        CONFIG_MODULES.put(DATABASE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TABLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DIMENSION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USE_INDEX, ConfigEntry.fromModule());
        CONFIG_MODULES.put(INDEX_LIST_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CREATE_TABLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DROP_TABLE_FIRST, ConfigEntry.fromModule());
        CONFIG_MODULES.put(METADATA_STORAGE_CONFIG, ConfigEntry.fromModule());
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
