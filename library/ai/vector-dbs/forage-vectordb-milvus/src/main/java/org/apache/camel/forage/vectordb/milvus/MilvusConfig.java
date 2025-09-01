package org.apache.camel.forage.vectordb.milvus;

import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

public class MilvusConfig implements Config {

    private static final ConfigModule HOST = ConfigModule.of(MilvusConfig.class, "host");
    private static final ConfigModule PORT = ConfigModule.of(MilvusConfig.class, "port");
    private static final ConfigModule COLLECTION_NAME = ConfigModule.of(MilvusConfig.class, "collection-name");
    private static final ConfigModule DIMENSION = ConfigModule.of(MilvusConfig.class, "dimension");
    private static final ConfigModule INDEX_TYPE = ConfigModule.of(MilvusConfig.class, "index-type");
    private static final ConfigModule METRIC_TYPE = ConfigModule.of(MilvusConfig.class, "metric-type");
    private static final ConfigModule URI = ConfigModule.of(MilvusConfig.class, "uri");
    private static final ConfigModule TOKEN = ConfigModule.of(MilvusConfig.class, "token");
    private static final ConfigModule USERNAME = ConfigModule.of(MilvusConfig.class, "username");
    private static final ConfigModule PASSWORD = ConfigModule.of(MilvusConfig.class, "password");
    private static final ConfigModule CONSISTENCY_LEVEL = ConfigModule.of(MilvusConfig.class, "consistency-level");
    private static final ConfigModule RETRIEVE_EMBEDDINGS_ON_SEARCH =
            ConfigModule.of(MilvusConfig.class, "retrieve-embeddings-on-search");
    private static final ConfigModule AUTO_FLUSH_ON_INSERT =
            ConfigModule.of(MilvusConfig.class, "auto-flush-on-insert");

    private static final ConfigModule DATABASE_NAME = ConfigModule.of(MilvusConfig.class, "database-name");
    private static final ConfigModule ID_FIELD_NAME = ConfigModule.of(MilvusConfig.class, "id-field-name");
    private static final ConfigModule TEXT_FIELD_NAME = ConfigModule.of(MilvusConfig.class, "text-field-name");
    private static final ConfigModule METADATA_FIELD_NAME = ConfigModule.of(MilvusConfig.class, "metadata-field-name");
    private static final ConfigModule VECTOR_FIELD_NAME = ConfigModule.of(MilvusConfig.class, "vector-field-name");

    public MilvusConfig() {
        ConfigStore.getInstance().add(HOST, ConfigEntry.fromModule(HOST, "MILVUS_HOST"));
        ConfigStore.getInstance().add(PORT, ConfigEntry.fromModule(PORT, "MILVUS_PORT"));
        ConfigStore.getInstance()
                .add(COLLECTION_NAME, ConfigEntry.fromModule(COLLECTION_NAME, "MILVUS_COLLECTION_NAME"));
        ConfigStore.getInstance().add(DIMENSION, ConfigEntry.fromModule(DIMENSION, "MILVUS_DIMENSION"));
        ConfigStore.getInstance().add(INDEX_TYPE, ConfigEntry.fromModule(INDEX_TYPE, "MILVUS_INDEX_TYPE"));
        ConfigStore.getInstance().add(METRIC_TYPE, ConfigEntry.fromModule(METRIC_TYPE, "MILVUS_METRIC_TYPE"));
        ConfigStore.getInstance().add(URI, ConfigEntry.fromModule(URI, "MILVUS_URI"));
        ConfigStore.getInstance().add(TOKEN, ConfigEntry.fromModule(TOKEN, "MILVUS_TOKEN"));
        ConfigStore.getInstance().add(USERNAME, ConfigEntry.fromModule(USERNAME, "MILVUS_USERNAME"));
        ConfigStore.getInstance().add(PASSWORD, ConfigEntry.fromModule(PASSWORD, "MILVUS_PASSWORD"));
        ConfigStore.getInstance()
                .add(CONSISTENCY_LEVEL, ConfigEntry.fromModule(CONSISTENCY_LEVEL, "MILVUS_CONSISTENCY_LEVEL"));
        ConfigStore.getInstance()
                .add(
                        RETRIEVE_EMBEDDINGS_ON_SEARCH,
                        ConfigEntry.fromModule(RETRIEVE_EMBEDDINGS_ON_SEARCH, "MILVUS_RETRIEVE_EMBEDDINGS_ON_SEARCH"));
        ConfigStore.getInstance()
                .add(AUTO_FLUSH_ON_INSERT, ConfigEntry.fromModule(AUTO_FLUSH_ON_INSERT, "MILVUS_AUTO_FLUSH_ON_INSERT"));
        ConfigStore.getInstance().add(DATABASE_NAME, ConfigEntry.fromModule(DATABASE_NAME, "MILVUS_DATABASE_NAME"));
        ConfigStore.getInstance().add(ID_FIELD_NAME, ConfigEntry.fromModule(ID_FIELD_NAME, "MILVUS_ID_FIELD_NAME"));
        ConfigStore.getInstance()
                .add(TEXT_FIELD_NAME, ConfigEntry.fromModule(TEXT_FIELD_NAME, "MILVUS_TEXT_FIELD_NAME"));
        ConfigStore.getInstance()
                .add(METADATA_FIELD_NAME, ConfigEntry.fromModule(METADATA_FIELD_NAME, "MILVUS_METADATA_FIELD_NAME"));
        ConfigStore.getInstance()
                .add(VECTOR_FIELD_NAME, ConfigEntry.fromModule(VECTOR_FIELD_NAME, "MILVUS_VECTOR_FIELD_NAME"));
        ConfigStore.getInstance().add(MilvusConfig.class, this, this::register);
    }

    @Override
    public String name() {
        return "forage-vectordb-milvus";
    }

    public String host() {
        return ConfigStore.getInstance().get(HOST).orElseThrow(() -> new MissingConfigException("Missing Milvus host"));
    }

    public Integer port() {
        return ConfigStore.getInstance()
                .get(PORT)
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Milvus port"));
    }

    public String collectionName() {
        return ConfigStore.getInstance().get(COLLECTION_NAME).orElse("default");
    }

    public Integer dimension() {
        return ConfigStore.getInstance()
                .get(DIMENSION)
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Milvus dimension"));
    }

    public IndexType indexType() {
        String indexType = ConfigStore.getInstance().get(INDEX_TYPE).orElse(null);

        if (indexType != null) {
            return IndexType.valueOf(indexType);
        } else {
            return IndexType.FLAT;
        }
    }

    public MetricType metricType() {
        String metricType = ConfigStore.getInstance().get(METRIC_TYPE).orElse(null);
        if (metricType != null) {
            return MetricType.valueOf(metricType);
        } else {
            return MetricType.COSINE;
        }
    }

    public String uri() {
        return ConfigStore.getInstance().get(URI).orElseThrow(() -> new MissingConfigException("Missing Milvus URI"));
    }

    public String token() {
        return ConfigStore.getInstance()
                .get(TOKEN)
                .orElseThrow(() -> new MissingConfigException("Missing Milvus token"));
    }

    public String username() {
        return ConfigStore.getInstance()
                .get(USERNAME)
                .orElseThrow(() -> new MissingConfigException("Missing Google username"));
    }

    public String password() {
        return ConfigStore.getInstance()
                .get(PASSWORD)
                .orElseThrow(() -> new MissingConfigException("Missing Milvus password"));
    }

    public ConsistencyLevelEnum consistencyLevel() {
        String consistencyLevel =
                ConfigStore.getInstance().get(CONSISTENCY_LEVEL).orElse(null);

        if (consistencyLevel != null) {
            return ConsistencyLevelEnum.valueOf(consistencyLevel);
        } else {
            return ConsistencyLevelEnum.EVENTUALLY;
        }
    }

    public Boolean retrieveEmbeddingsOnSearch() {
        return ConfigStore.getInstance()
                .get(RETRIEVE_EMBEDDINGS_ON_SEARCH)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public String databaseName() {
        return ConfigStore.getInstance()
                .get(DATABASE_NAME)
                .orElseThrow(() -> new MissingConfigException("Missing Milvus database name"));
    }

    public String idFieldName() {
        return ConfigStore.getInstance().get(ID_FIELD_NAME).orElse("id");
    }

    public String textFieldName() {
        return ConfigStore.getInstance().get(TEXT_FIELD_NAME).orElse("text");
    }

    public String metadataFieldName() {
        return ConfigStore.getInstance().get(METADATA_FIELD_NAME).orElse("metadata");
    }

    public String vectorFieldName() {
        return ConfigStore.getInstance().get(VECTOR_FIELD_NAME).orElse("vector");
    }

    public Boolean autoFlushOnInsert() {
        return ConfigStore.getInstance()
                .get(AUTO_FLUSH_ON_INSERT)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    private ConfigModule resolve(String name) {
        if (HOST.name().equals(name)) {
            return HOST;
        }
        if (PORT.name().equals(name)) {
            return PORT;
        }
        if (COLLECTION_NAME.name().equals(name)) {
            return COLLECTION_NAME;
        }
        if (DIMENSION.name().equals(name)) {
            return DIMENSION;
        }
        if (INDEX_TYPE.name().equals(name)) {
            return INDEX_TYPE;
        }
        if (METRIC_TYPE.name().equals(name)) {
            return METRIC_TYPE;
        }
        if (URI.name().equals(name)) {
            return URI;
        }
        if (TOKEN.name().equals(name)) {
            return TOKEN;
        }
        if (USERNAME.name().equals(name)) {
            return USERNAME;
        }
        if (PASSWORD.name().equals(name)) {
            return PASSWORD;
        }
        if (CONSISTENCY_LEVEL.name().equals(name)) {
            return CONSISTENCY_LEVEL;
        }
        if (RETRIEVE_EMBEDDINGS_ON_SEARCH.name().equals(name)) {
            return RETRIEVE_EMBEDDINGS_ON_SEARCH;
        }
        if (AUTO_FLUSH_ON_INSERT.name().equals(name)) {
            return AUTO_FLUSH_ON_INSERT;
        }
        if (DATABASE_NAME.name().equals(name)) {
            return DATABASE_NAME;
        }
        if (ID_FIELD_NAME.name().equals(name)) {
            return ID_FIELD_NAME;
        }
        if (TEXT_FIELD_NAME.name().equals(name)) {
            return TEXT_FIELD_NAME;
        }
        if (METADATA_FIELD_NAME.name().equals(name)) {
            return METADATA_FIELD_NAME;
        }
        if (VECTOR_FIELD_NAME.name().equals(name)) {
            return VECTOR_FIELD_NAME;
        }
        throw new IllegalArgumentException("Unknown config entry: " + name);
    }

    @Override
    public void register(String name, String value) {
        ConfigModule config = resolve(name);
        ConfigStore.getInstance().set(config, value);
    }
}
