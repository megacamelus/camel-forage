package io.kaoto.forage.vectordb.mariadb;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigTag;

public final class MariaDbConfigEntries extends ConfigEntries {
    public static final ConfigModule URL = ConfigModule.of(
            MariaDbConfig.class,
            "forage.mariadb.url",
            "MariaDB JDBC URL (e.g., jdbc:mariadb://localhost:3306/vectordb)",
            "URL",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule USER = ConfigModule.of(
            MariaDbConfig.class,
            "forage.mariadb.user",
            "Database username for authentication",
            "User",
            null,
            "string",
            true,
            ConfigTag.SECURITY);
    public static final ConfigModule PASSWORD = ConfigModule.of(
            MariaDbConfig.class,
            "forage.mariadb.password",
            "Database password for authentication",
            "Password",
            null,
            "password",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule TABLE = ConfigModule.of(
            MariaDbConfig.class,
            "forage.mariadb.table",
            "Name of the table to store embeddings",
            "Table",
            "embeddings",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule ID_FIELD_NAME = ConfigModule.of(
            MariaDbConfig.class,
            "forage.mariadb.id.field.name",
            "Name of the ID field in the embeddings table",
            "ID Field Name",
            "id",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule EMBEDDING_FIELD_NAME = ConfigModule.of(
            MariaDbConfig.class,
            "forage.mariadb.embedding.field.name",
            "Name of the embedding vector field",
            "Embedding Field Name",
            "embedding",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule CONTENT_FIELD_NAME = ConfigModule.of(
            MariaDbConfig.class,
            "forage.mariadb.content.field.name",
            "Name of the content text field",
            "Content Field Name",
            "content",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule DISTANCE_TYPE = ConfigModule.of(
            MariaDbConfig.class,
            "forage.mariadb.distance.type",
            "Distance calculation method: COSINE or EUCLIDEAN",
            "Distance Type",
            "COSINE",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule DIMENSION = ConfigModule.of(
            MariaDbConfig.class,
            "forage.mariadb.dimension",
            "Vector dimension size",
            "Dimension",
            "384",
            "integer",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule CREATE_TABLE = ConfigModule.of(
            MariaDbConfig.class,
            "forage.mariadb.create.table",
            "Whether to create the table if it doesn't exist",
            "Create Table",
            "true",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule DROP_TABLE_FIRST = ConfigModule.of(
            MariaDbConfig.class,
            "forage.mariadb.drop.table.first",
            "Whether to drop the table before creating it",
            "Drop Table First",
            "false",
            "boolean",
            false,
            ConfigTag.ADVANCED);

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
