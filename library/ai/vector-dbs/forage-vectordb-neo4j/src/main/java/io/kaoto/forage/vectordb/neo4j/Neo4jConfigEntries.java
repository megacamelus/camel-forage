package io.kaoto.forage.vectordb.neo4j;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigTag;

public final class Neo4jConfigEntries extends ConfigEntries {
    public static final ConfigModule INDEX_NAME = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.index.name",
            "Vector index name",
            "Index Name",
            "vector-index",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule METADATA_PREFIX = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.metadata.prefix",
            "Metadata prefix",
            "Metadata Prefix",
            "metadata_",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule EMBEDDING_PROPERTY = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.embedding.property",
            "Embedding property name",
            "Embedding Property",
            "embedding",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule ID_PROPERTY = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.id.property",
            "ID property name",
            "ID Property",
            "id",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule LABEL = ConfigModule.of(
            Neo4jConfig.class, "neo4j.label", "Node label", "Label", "Document", "string", false, ConfigTag.COMMON);
    public static final ConfigModule TEXT_PROPERTY = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.text.property",
            "Text property name",
            "Text Property",
            "text",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule DATABASE_NAME = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.database.name",
            "Database name",
            "Database Name",
            "neo4j",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule RETRIEVAL_QUERY = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.retrieval.query",
            "Custom retrieval query",
            "Retrieval Query",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule DIMENSION = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.dimension",
            "Vector dimension",
            "Dimension",
            null,
            "integer",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule AWAIT_INDEX_TIMEOUT = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.await.index.timeout",
            "Index creation timeout in seconds",
            "Await Index Timeout",
            "60",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule FULL_TEXT_INDEX_NAME = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.full.text.index.name",
            "Full text index name",
            "Full Text Index Name",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule FULL_TEXT_QUERY = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.full.text.query",
            "Full text query",
            "Full Text Query",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule FULL_TEXT_RETRIEVAL_QUERY = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.full.text.retrieval.query",
            "Full text retrieval query",
            "Full Text Retrieval Query",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule AUTO_CREATE_FULL_TEXT = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.auto.create.full.text",
            "Auto create full text index",
            "Auto Create Full Text",
            "false",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule ENTITY_CREATION_QUERY = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.entity.creation.query",
            "Custom entity creation query",
            "Entity Creation Query",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule URI = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.uri",
            "Neo4j server URI",
            "URI",
            "bolt://localhost:7687",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule USER = ConfigModule.of(
            Neo4jConfig.class, "forage.neo4j.user", "Username", "User", "neo4j", "string", false, ConfigTag.SECURITY);
    public static final ConfigModule PASSWORD = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.password",
            "Password",
            "Password",
            null,
            "password",
            true,
            ConfigTag.SECURITY);
    public static final ConfigModule WITH_ENCRYPTION = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.with.encryption",
            "Enable SSL encryption",
            "With Encryption",
            "false",
            "boolean",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule CONNECTION_TIMEOUT = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.connection.timeout",
            "Connection timeout in seconds",
            "Connection Timeout",
            "30",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule MAX_CONNECTION_LIFETIME = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.max.connection.lifetime",
            "Max connection lifetime in minutes",
            "Max Connection Lifetime",
            "60",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule MAX_CONNECTION_POOL_SIZE = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.max.connection.pool.size",
            "Max connection pool size",
            "Max Connection Pool Size",
            "100",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule CONNECTION_ACQUISITION_TIMEOUT = ConfigModule.of(
            Neo4jConfig.class,
            "forage.neo4j.connection.acquisition.timeout",
            "Connection acquisition timeout in seconds",
            "Connection Acquisition Timeout",
            "60",
            "integer",
            false,
            ConfigTag.ADVANCED);

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
