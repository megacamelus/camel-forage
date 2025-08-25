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
        ConfigStore.getInstance().add(HOST, ConfigEntry.fromEnv("MILVUS_HOST"));
        ConfigStore.getInstance().add(PORT, ConfigEntry.fromEnv("MILVUS_PORT"));
        ConfigStore.getInstance().add(COLLECTION_NAME, ConfigEntry.fromEnv("MILVUS_COLLECTION_NAME"));
        ConfigStore.getInstance().add(DIMENSION, ConfigEntry.fromEnv("MILVUS_DIMENSION"));
        ConfigStore.getInstance().add(INDEX_TYPE, ConfigEntry.fromEnv("MILVUS_INDEX_TYPE"));
        ConfigStore.getInstance().add(METRIC_TYPE, ConfigEntry.fromEnv("MILVUS_METRIC_TYPE"));
        ConfigStore.getInstance().add(URI, ConfigEntry.fromEnv("MILVUS_URI"));
        ConfigStore.getInstance().add(TOKEN, ConfigEntry.fromEnv("MILVUS_TOKEN"));
        ConfigStore.getInstance().add(USERNAME, ConfigEntry.fromEnv("MILVUS_USERNAME"));
        ConfigStore.getInstance().add(PASSWORD, ConfigEntry.fromEnv("MILVUS_PASSWORD"));
        ConfigStore.getInstance().add(CONSISTENCY_LEVEL, ConfigEntry.fromEnv("MILVUS_CONSISTENCY_LEVEL"));
        ConfigStore.getInstance()
                .add(RETRIEVE_EMBEDDINGS_ON_SEARCH, ConfigEntry.fromEnv("MILVUS_RETRIEVE_EMBEDDINGS_ON_SEARCH"));
        ConfigStore.getInstance().add(AUTO_FLUSH_ON_INSERT, ConfigEntry.fromEnv("MILVUS_AUTO_FLUSH_ON_INSERT"));
        ConfigStore.getInstance().add(VECTOR_FIELD_NAME, ConfigEntry.fromEnv("MILVUS_ID_FIELD_NAME"));
        ConfigStore.getInstance().add(VECTOR_FIELD_NAME, ConfigEntry.fromEnv("MILVUS_TEXT_FIELD_NAME"));
        ConfigStore.getInstance().add(VECTOR_FIELD_NAME, ConfigEntry.fromEnv("MILVUS_METADATA_FIELD_NAME"));
        ConfigStore.getInstance().add(VECTOR_FIELD_NAME, ConfigEntry.fromEnv("MILVUS_VECTOR_FIELD_NAME"));
        ConfigStore.getInstance().add(MilvusConfig.class, this);
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
}
