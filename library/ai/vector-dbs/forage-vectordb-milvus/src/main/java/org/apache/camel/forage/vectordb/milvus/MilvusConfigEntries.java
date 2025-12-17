package org.apache.camel.forage.vectordb.milvus;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigTag;

public final class MilvusConfigEntries extends ConfigEntries {
    public static final ConfigModule HOST = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.host",
            "Milvus server host address",
            "Host",
            null,
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule PORT = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.port",
            "Milvus server port number",
            "Port",
            null,
            "integer",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule COLLECTION_NAME = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.collection.name",
            "Name of the Milvus collection",
            "Collection Name",
            "default",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule DIMENSION = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.dimension",
            "Vector dimension size",
            "Dimension",
            null,
            "integer",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule INDEX_TYPE = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.index.type",
            "Index type for vector search (e.g., IVF_FLAT, HNSW)",
            "Index Type",
            "FLAT",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule METRIC_TYPE = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.metric.type",
            "Distance metric type (e.g., COSINE, L2, IP)",
            "Metric Type",
            "COSINE",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule URI = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.uri",
            "Milvus server URI (alternative to host/port)",
            "URI",
            null,
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule TOKEN = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.token",
            "Authentication token",
            "Token",
            null,
            "password",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule USERNAME = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.username",
            "Username for authentication",
            "Username",
            "",
            "string",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule PASSWORD = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.password",
            "Password for authentication",
            "Password",
            "",
            "password",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule CONSISTENCY_LEVEL = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.consistency.level",
            "Consistency level for queries (e.g., STRONG, EVENTUALLY)",
            "Consistency Level",
            "EVENTUALLY",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule RETRIEVE_EMBEDDINGS_ON_SEARCH = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.retrieve.embeddings.on.search",
            "Whether to retrieve embeddings in search results",
            "Retrieve Embeddings",
            "false",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule AUTO_FLUSH_ON_INSERT = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.auto.flush.on.insert",
            "Automatically flush data after insert operations",
            "Auto Flush",
            "false",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule DATABASE_NAME = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.database.name",
            "Name of the Milvus database",
            "Database Name",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule ID_FIELD_NAME = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.id.field.name",
            "Name of the ID field",
            "ID Field Name",
            "id",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule TEXT_FIELD_NAME = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.text.field.name",
            "Name of the text field",
            "Text Field Name",
            "text",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule METADATA_FIELD_NAME = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.metadata.field.name",
            "Name of the metadata field",
            "Metadata Field Name",
            "metadata",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule VECTOR_FIELD_NAME = ConfigModule.of(
            MilvusConfig.class,
            "forage.milvus.vector.field.name",
            "Name of the vector field",
            "Vector Field Name",
            "vector",
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
        CONFIG_MODULES.put(COLLECTION_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DIMENSION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(INDEX_TYPE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(METRIC_TYPE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(URI, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TOKEN, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USERNAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PASSWORD, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CONSISTENCY_LEVEL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(RETRIEVE_EMBEDDINGS_ON_SEARCH, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AUTO_FLUSH_ON_INSERT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DATABASE_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ID_FIELD_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TEXT_FIELD_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(METADATA_FIELD_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(VECTOR_FIELD_NAME, ConfigEntry.fromModule());
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
