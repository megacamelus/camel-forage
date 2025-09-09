package org.apache.camel.forage.vectordb.neo4j;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;

public final class Neo4jConfigEntries extends ConfigEntries {
    public static final ConfigModule INDEX_NAME = ConfigModule.of(Neo4jConfig.class, "neo4j.index.name");
    public static final ConfigModule METADATA_PREFIX = ConfigModule.of(Neo4jConfig.class, "neo4j.metadata.prefix");
    public static final ConfigModule EMBEDDING_PROPERTY =
            ConfigModule.of(Neo4jConfig.class, "neo4j.embedding.property");
    public static final ConfigModule ID_PROPERTY = ConfigModule.of(Neo4jConfig.class, "neo4j.id.property");
    public static final ConfigModule LABEL = ConfigModule.of(Neo4jConfig.class, "neo4j.label");
    public static final ConfigModule TEXT_PROPERTY = ConfigModule.of(Neo4jConfig.class, "neo4j.text.property");
    public static final ConfigModule DATABASE_NAME = ConfigModule.of(Neo4jConfig.class, "neo4j.database.name");
    public static final ConfigModule RETRIEVAL_QUERY = ConfigModule.of(Neo4jConfig.class, "neo4j.retrieval.query");
    public static final ConfigModule DIMENSION = ConfigModule.of(Neo4jConfig.class, "neo4j.dimension");
    public static final ConfigModule AWAIT_INDEX_TIMEOUT =
            ConfigModule.of(Neo4jConfig.class, "neo4j.await.index.timeout");
    public static final ConfigModule FULL_TEXT_INDEX_NAME =
            ConfigModule.of(Neo4jConfig.class, "neo4j.full.text.index.name");
    public static final ConfigModule FULL_TEXT_QUERY = ConfigModule.of(Neo4jConfig.class, "neo4j.full.text.query");
    public static final ConfigModule FULL_TEXT_RETRIEVAL_QUERY =
            ConfigModule.of(Neo4jConfig.class, "neo4j.full.text.retrieval.query");
    public static final ConfigModule AUTO_CREATE_FULL_TEXT =
            ConfigModule.of(Neo4jConfig.class, "neo4j.auto.create.full.text");
    public static final ConfigModule ENTITY_CREATION_QUERY =
            ConfigModule.of(Neo4jConfig.class, "neo4j.entity.creation.query");
    public static final ConfigModule URI = ConfigModule.of(Neo4jConfig.class, "neo4j.uri");
    public static final ConfigModule USER = ConfigModule.of(Neo4jConfig.class, "neo4j.user");
    public static final ConfigModule PASSWORD = ConfigModule.of(Neo4jConfig.class, "neo4j.password");
    public static final ConfigModule WITH_ENCRYPTION = ConfigModule.of(Neo4jConfig.class, "neo4j.with.encryption");
    public static final ConfigModule CONNECTION_TIMEOUT =
            ConfigModule.of(Neo4jConfig.class, "neo4j.connection.timeout");
    public static final ConfigModule MAX_CONNECTION_LIFETIME =
            ConfigModule.of(Neo4jConfig.class, "neo4j.max.connection.lifetime");
    public static final ConfigModule MAX_CONNECTION_POOL_SIZE =
            ConfigModule.of(Neo4jConfig.class, "neo4j.max.connection.pool.size");
    public static final ConfigModule CONNECTION_ACQUISITION_TIMEOUT =
            ConfigModule.of(Neo4jConfig.class, "neo4j.connection.acquisition.timeout");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(INDEX_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(METADATA_PREFIX, ConfigEntry.fromModule());
        CONFIG_MODULES.put(EMBEDDING_PROPERTY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ID_PROPERTY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(LABEL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TEXT_PROPERTY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DATABASE_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(RETRIEVAL_QUERY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DIMENSION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AWAIT_INDEX_TIMEOUT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(FULL_TEXT_INDEX_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(FULL_TEXT_QUERY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(FULL_TEXT_RETRIEVAL_QUERY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AUTO_CREATE_FULL_TEXT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ENTITY_CREATION_QUERY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(URI, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USER, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PASSWORD, ConfigEntry.fromModule());
        CONFIG_MODULES.put(WITH_ENCRYPTION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CONNECTION_TIMEOUT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_CONNECTION_LIFETIME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_CONNECTION_POOL_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CONNECTION_ACQUISITION_TIMEOUT, ConfigEntry.fromModule());
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
