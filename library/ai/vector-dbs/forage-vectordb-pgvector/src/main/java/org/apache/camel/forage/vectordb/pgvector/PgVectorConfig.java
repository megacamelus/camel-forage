package org.apache.camel.forage.vectordb.pgvector;

import dev.langchain4j.store.embedding.pgvector.DefaultMetadataStorageConfig;
import dev.langchain4j.store.embedding.pgvector.MetadataStorageConfig;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

public class PgVectorConfig implements Config {

    private static final ConfigModule HOST = ConfigModule.of(PgVectorConfig.class, "host");
    private static final ConfigModule PORT = ConfigModule.of(PgVectorConfig.class, "port");
    private static final ConfigModule USER = ConfigModule.of(PgVectorConfig.class, "user");
    private static final ConfigModule PASSWORD = ConfigModule.of(PgVectorConfig.class, "password");
    private static final ConfigModule DATABASE = ConfigModule.of(PgVectorConfig.class, "database");
    private static final ConfigModule TABLE = ConfigModule.of(PgVectorConfig.class, "table");
    private static final ConfigModule DIMENSION = ConfigModule.of(PgVectorConfig.class, "dimension");
    private static final ConfigModule USE_INDEX = ConfigModule.of(PgVectorConfig.class, "use-index");
    private static final ConfigModule INDEX_LIST_SIZE = ConfigModule.of(PgVectorConfig.class, "index-list-size");
    private static final ConfigModule CREATE_TABLE = ConfigModule.of(PgVectorConfig.class, "create-table");
    private static final ConfigModule DROP_TABLE_FIRST = ConfigModule.of(PgVectorConfig.class, "drop-table-first");
    private static final ConfigModule METADATA_STORAGE_CONFIG =
            ConfigModule.of(PgVectorConfig.class, "metadata-storage-config");

    public PgVectorConfig() {
        ConfigStore.getInstance().add(HOST, ConfigEntry.fromModule(HOST, "PGVECTOR_HOST"));
        ConfigStore.getInstance().add(PORT, ConfigEntry.fromModule(PORT, "PGVECTOR_PORT"));
        ConfigStore.getInstance().add(USER, ConfigEntry.fromModule(USER, "PGVECTOR_USER"));
        ConfigStore.getInstance().add(PASSWORD, ConfigEntry.fromModule(PASSWORD, "PGVECTOR_PASSWORD"));
        ConfigStore.getInstance().add(DATABASE, ConfigEntry.fromModule(DATABASE, "PGVECTOR_DATABASE"));
        ConfigStore.getInstance().add(TABLE, ConfigEntry.fromModule(TABLE, "PGVECTOR_TABLE"));
        ConfigStore.getInstance().add(DIMENSION, ConfigEntry.fromModule(DIMENSION, "PGVECTOR_DIMENSION"));
        ConfigStore.getInstance().add(USE_INDEX, ConfigEntry.fromModule(USE_INDEX, "PGVECTOR_USE_INDEX"));
        ConfigStore.getInstance()
                .add(INDEX_LIST_SIZE, ConfigEntry.fromModule(INDEX_LIST_SIZE, "PGVECTOR_INDEX_LIST_SIZE"));
        ConfigStore.getInstance().add(CREATE_TABLE, ConfigEntry.fromModule(CREATE_TABLE, "PGVECTOR_CREATE_TABLE"));
        ConfigStore.getInstance()
                .add(DROP_TABLE_FIRST, ConfigEntry.fromModule(DROP_TABLE_FIRST, "PGVECTOR_DROP_TABLE_FIRST"));
        ConfigStore.getInstance()
                .add(
                        METADATA_STORAGE_CONFIG,
                        ConfigEntry.fromModule(METADATA_STORAGE_CONFIG, "PGVECTOR_METADATA_STORAGE_CONFIG"));
        ConfigStore.getInstance().add(PgVectorConfig.class, this, this::register);
    }

    @Override
    public String name() {
        return "forage-vectordb-pgvector";
    }

    public String host() {
        return ConfigStore.getInstance()
                .get(HOST)
                .orElseThrow(() -> new MissingConfigException("Missing PGVector host"));
    }

    public Integer port() {
        return ConfigStore.getInstance()
                .get(PORT)
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing PGVector port"));
    }

    public String user() {
        return ConfigStore.getInstance()
                .get(USER)
                .orElseThrow(() -> new MissingConfigException("Missing PGVector user"));
    }

    public String password() {
        return ConfigStore.getInstance()
                .get(PASSWORD)
                .orElseThrow(() -> new MissingConfigException("Missing PGVector password"));
    }

    public String database() {
        return ConfigStore.getInstance()
                .get(DATABASE)
                .orElseThrow(() -> new MissingConfigException("Missing PGVector database"));
    }

    public String table() {
        return ConfigStore.getInstance()
                .get(TABLE)
                .orElseThrow(() -> new MissingConfigException("Missing PGVector table"));
    }

    public Integer dimension() {
        return ConfigStore.getInstance()
                .get(DIMENSION)
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing PGVector dimension"));
    }

    public Boolean useIndex() {
        return ConfigStore.getInstance()
                .get(USE_INDEX)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public Integer indexListSize() {
        return ConfigStore.getInstance()
                .get(INDEX_LIST_SIZE)
                .map(Integer::parseInt)
                .orElse(100);
    }

    public Boolean createTable() {
        return ConfigStore.getInstance()
                .get(CREATE_TABLE)
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    public Boolean dropTableFirst() {
        return ConfigStore.getInstance()
                .get(DROP_TABLE_FIRST)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public MetadataStorageConfig metadataStorageConfig() {
        return DefaultMetadataStorageConfig.defaultConfig();
    }

    private ConfigModule resolve(String name) {
        if (HOST.name().equals(name)) {
            return HOST;
        }
        if (PORT.name().equals(name)) {
            return PORT;
        }
        if (USER.name().equals(name)) {
            return USER;
        }
        if (PASSWORD.name().equals(name)) {
            return PASSWORD;
        }
        if (DATABASE.name().equals(name)) {
            return DATABASE;
        }
        if (TABLE.name().equals(name)) {
            return TABLE;
        }
        if (DIMENSION.name().equals(name)) {
            return DIMENSION;
        }
        if (USE_INDEX.name().equals(name)) {
            return USE_INDEX;
        }
        if (INDEX_LIST_SIZE.name().equals(name)) {
            return INDEX_LIST_SIZE;
        }
        if (CREATE_TABLE.name().equals(name)) {
            return CREATE_TABLE;
        }
        if (DROP_TABLE_FIRST.name().equals(name)) {
            return DROP_TABLE_FIRST;
        }
        if (METADATA_STORAGE_CONFIG.name().equals(name)) {
            return METADATA_STORAGE_CONFIG;
        }
        throw new IllegalArgumentException("Unknown config entry: " + name);
    }

    @Override
    public void register(String name, String value) {
        ConfigModule config = resolve(name);
        ConfigStore.getInstance().set(config, value);
    }
}
