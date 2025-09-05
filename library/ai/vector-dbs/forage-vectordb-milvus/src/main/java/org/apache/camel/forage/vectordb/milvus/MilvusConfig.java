package org.apache.camel.forage.vectordb.milvus;

import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.AUTO_FLUSH_ON_INSERT;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.COLLECTION_NAME;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.CONSISTENCY_LEVEL;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.DATABASE_NAME;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.DIMENSION;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.HOST;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.ID_FIELD_NAME;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.INDEX_TYPE;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.METADATA_FIELD_NAME;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.METRIC_TYPE;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.PASSWORD;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.PORT;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.RETRIEVE_EMBEDDINGS_ON_SEARCH;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.TEXT_FIELD_NAME;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.TOKEN;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.URI;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.USERNAME;
import static org.apache.camel.forage.vectordb.milvus.MilvusConfigEntries.VECTOR_FIELD_NAME;

import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

/**
 * Configuration class for Milvus vector database connections within the Camel Forage framework.
 *
 * <p>This configuration class manages all settings required to connect to and operate with Milvus,
 * an open-source vector database built for scalable similarity search and AI applications.</p>
 *
 * <p>Configuration properties are loaded from multiple sources in order of precedence:</p>
 * <ol>
 *   <li>Environment variables (highest precedence)</li>
 *   <li>System properties</li>
 *   <li>Configuration files (forage-vectordb-milvus.properties)</li>
 *   <li>Default values (lowest precedence)</li>
 * </ol>
 *
 * <p>The configuration supports ID-scoped properties using the pattern {@code milvus.<id>.<property>}
 * which allows multiple Milvus configurations within the same application.</p>
 *
 * <p>Example configuration properties:</p>
 * <pre>
 * # Connection settings
 * milvus.host=localhost
 * milvus.port=19530
 * milvus.uri=http://localhost:19530
 *
 * # Authentication (choose one method)
 * milvus.token=your-api-token
 * # OR
 * milvus.username=user
 * milvus.password=pass
 *
 * # Collection settings
 * milvus.collection.name=my_vectors
 * milvus.dimension=1536
 * milvus.index.type=IVF_FLAT
 * milvus.metric.type=COSINE
 *
 * # Performance settings
 * milvus.consistency.level=EVENTUALLY
 * milvus.auto.flush.on.insert=false
 * milvus.retrieve.embeddings.on.search=false
 * </pre>
 *
 * @see MilvusProvider
 * @see MilvusConfigEntries
 *
 * @since 1.0
 */
public class MilvusConfig implements Config {

    private final String prefix;

    public MilvusConfig() {
        this(null);
    }

    public MilvusConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        MilvusConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(MilvusConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        MilvusConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-vectordb-milvus";
    }

    public String host() {
        return ConfigStore.getInstance()
                .get(HOST.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Milvus host"));
    }

    public Integer port() {
        return ConfigStore.getInstance()
                .get(PORT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Milvus port"));
    }

    public String collectionName() {
        return ConfigStore.getInstance().get(COLLECTION_NAME.asNamed(prefix)).orElse("default");
    }

    public Integer dimension() {
        return ConfigStore.getInstance()
                .get(DIMENSION.asNamed(prefix))
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Milvus dimension"));
    }

    public IndexType indexType() {
        String indexType =
                ConfigStore.getInstance().get(INDEX_TYPE.asNamed(prefix)).orElse(null);

        if (indexType != null) {
            return IndexType.valueOf(indexType);
        } else {
            return IndexType.FLAT;
        }
    }

    public MetricType metricType() {
        String metricType =
                ConfigStore.getInstance().get(METRIC_TYPE.asNamed(prefix)).orElse(null);
        if (metricType != null) {
            return MetricType.valueOf(metricType);
        } else {
            return MetricType.COSINE;
        }
    }

    public String uri() {
        return ConfigStore.getInstance()
                .get(URI.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Milvus URI"));
    }

    public String token() {
        return ConfigStore.getInstance()
                .get(TOKEN.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Milvus token"));
    }

    public String username() {
        return ConfigStore.getInstance()
                .get(USERNAME.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Google username"));
    }

    public String password() {
        return ConfigStore.getInstance()
                .get(PASSWORD.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Milvus password"));
    }

    public ConsistencyLevelEnum consistencyLevel() {
        String consistencyLevel =
                ConfigStore.getInstance().get(CONSISTENCY_LEVEL.asNamed(prefix)).orElse(null);

        if (consistencyLevel != null) {
            return ConsistencyLevelEnum.valueOf(consistencyLevel);
        } else {
            return ConsistencyLevelEnum.EVENTUALLY;
        }
    }

    public Boolean retrieveEmbeddingsOnSearch() {
        return ConfigStore.getInstance()
                .get(RETRIEVE_EMBEDDINGS_ON_SEARCH.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public String databaseName() {
        return ConfigStore.getInstance()
                .get(DATABASE_NAME.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Milvus database name"));
    }

    public String idFieldName() {
        return ConfigStore.getInstance().get(ID_FIELD_NAME.asNamed(prefix)).orElse("id");
    }

    public String textFieldName() {
        return ConfigStore.getInstance().get(TEXT_FIELD_NAME.asNamed(prefix)).orElse("text");
    }

    public String metadataFieldName() {
        return ConfigStore.getInstance()
                .get(METADATA_FIELD_NAME.asNamed(prefix))
                .orElse("metadata");
    }

    public String vectorFieldName() {
        return ConfigStore.getInstance().get(VECTOR_FIELD_NAME.asNamed(prefix)).orElse("vector");
    }

    public Boolean autoFlushOnInsert() {
        return ConfigStore.getInstance()
                .get(AUTO_FLUSH_ON_INSERT.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = MilvusConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }
}
