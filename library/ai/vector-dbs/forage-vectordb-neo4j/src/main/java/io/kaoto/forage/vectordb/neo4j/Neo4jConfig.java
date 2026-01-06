package io.kaoto.forage.vectordb.neo4j;

import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.AUTO_CREATE_FULL_TEXT;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.AWAIT_INDEX_TIMEOUT;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.CONNECTION_ACQUISITION_TIMEOUT;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.CONNECTION_TIMEOUT;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.DATABASE_NAME;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.DIMENSION;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.EMBEDDING_PROPERTY;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.ENTITY_CREATION_QUERY;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.FULL_TEXT_INDEX_NAME;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.FULL_TEXT_QUERY;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.FULL_TEXT_RETRIEVAL_QUERY;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.ID_PROPERTY;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.INDEX_NAME;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.LABEL;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.MAX_CONNECTION_LIFETIME;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.MAX_CONNECTION_POOL_SIZE;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.METADATA_PREFIX;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.PASSWORD;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.RETRIEVAL_QUERY;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.TEXT_PROPERTY;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.URI;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.USER;
import static io.kaoto.forage.vectordb.neo4j.Neo4jConfigEntries.WITH_ENCRYPTION;

import java.time.Duration;
import java.util.Optional;
import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;

/**
 * Configuration class for Neo4j vector database integration in the Camel Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use Neo4j
 * as a vector database. It handles server connection details, index configuration,
 * graph properties, and connection pool parameters through environment variables with
 * appropriate fallback mechanisms and default values.
 *
 * <p><strong>Configuration Parameters:</strong>
 * <ul>
 *   <li><strong>NEO4J_URI</strong> - The Neo4j server URI (default: "bolt://localhost:7687")</li>
 *   <li><strong>NEO4J_USER</strong> - The Neo4j username (default: "neo4j")</li>
 *   <li><strong>NEO4J_PASSWORD</strong> - The Neo4j password (required)</li>
 *   <li><strong>NEO4J_DATABASE_NAME</strong> - The database name (default: "neo4j")</li>
 *   <li><strong>NEO4J_INDEX_NAME</strong> - The vector index name (default: "vector-index")</li>
 *   <li><strong>NEO4J_LABEL</strong> - The node label (default: "Document")</li>
 *   <li><strong>NEO4J_EMBEDDING_PROPERTY</strong> - The embedding property name (default: "embedding")</li>
 *   <li><strong>NEO4J_TEXT_PROPERTY</strong> - The text property name (default: "text")</li>
 *   <li><strong>NEO4J_ID_PROPERTY</strong> - The ID property name (default: "id")</li>
 *   <li><strong>NEO4J_METADATA_PREFIX</strong> - The metadata prefix (default: "metadata_")</li>
 *   <li><strong>NEO4J_DIMENSION</strong> - The dimension of the vectors (required)</li>
 *   <li><strong>NEO4J_WITH_ENCRYPTION</strong> - Enable SSL encryption (default: false)</li>
 *   <li><strong>NEO4J_CONNECTION_TIMEOUT</strong> - Connection timeout in seconds (default: 30)</li>
 *   <li><strong>NEO4J_MAX_CONNECTION_LIFETIME</strong> - Max connection lifetime in minutes (default: 60)</li>
 *   <li><strong>NEO4J_MAX_CONNECTION_POOL_SIZE</strong> - Max connection pool size (default: 100)</li>
 *   <li><strong>NEO4J_CONNECTION_ACQUISITION_TIMEOUT</strong> - Connection acquisition timeout in seconds (default: 60)</li>
 *   <li><strong>NEO4J_AWAIT_INDEX_TIMEOUT</strong> - Index creation timeout in seconds (default: 60)</li>
 *   <li><strong>NEO4J_RETRIEVAL_QUERY</strong> - Custom retrieval query (optional)</li>
 *   <li><strong>NEO4J_ENTITY_CREATION_QUERY</strong> - Custom entity creation query (optional)</li>
 *   <li><strong>NEO4J_FULL_TEXT_INDEX_NAME</strong> - Full text index name (optional)</li>
 *   <li><strong>NEO4J_FULL_TEXT_QUERY</strong> - Full text query (optional)</li>
 *   <li><strong>NEO4J_FULL_TEXT_RETRIEVAL_QUERY</strong> - Full text retrieval query (optional)</li>
 *   <li><strong>NEO4J_AUTO_CREATE_FULL_TEXT</strong> - Auto create full text index (default: false)</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (NEO4J_URI, NEO4J_USER, NEO4J_PASSWORD, etc.)</li>
 *   <li>System properties (neo4j.uri, neo4j.user, neo4j.password, etc.)</li>
 *   <li>forage-vectordb-neo4j.properties file in classpath</li>
 *   <li>Default values if none of the above are provided</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables (optional)
 * export NEO4J_URI="bolt://neo4j.example.com:7687"
 * export NEO4J_USER="neo4j"
 * export NEO4J_PASSWORD="password"
 * export NEO4J_DIMENSION="384"
 * export NEO4J_INDEX_NAME="my_vector_index"
 * export NEO4J_LABEL="Document"
 *
 * // Create and use configuration
 * Neo4jConfig config = new Neo4jConfig();
 * String uri = config.uri();                  // Returns the configured URI
 * String user = config.user();                // Returns the configured user
 * int dimension = config.dimension();         // Returns the configured dimension
 * }</pre>
 *
 * <p>This class automatically registers itself and its configuration parameters with the
 * {@link ConfigStore} during construction, making the configuration values available
 * to other components in the framework.
 *
 * @see Config
 * @see ConfigStore
 * @see ConfigModule
 * @since 1.0
 */
public class Neo4jConfig implements Config {

    private static final String DEFAULT_URI = "bolt://localhost:7687";
    private static final String DEFAULT_USER = "neo4j";
    private static final String DEFAULT_DATABASE_NAME = "neo4j";
    private static final String DEFAULT_INDEX_NAME = "vector-index";
    private static final String DEFAULT_LABEL = "Document";
    private static final String DEFAULT_EMBEDDING_PROPERTY = "embedding";
    private static final String DEFAULT_TEXT_PROPERTY = "text";
    private static final String DEFAULT_ID_PROPERTY = "id";
    private static final String DEFAULT_METADATA_PREFIX = "metadata_";
    private static final boolean DEFAULT_WITH_ENCRYPTION = false;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 30;
    private static final int DEFAULT_MAX_CONNECTION_LIFETIME = 60;
    private static final int DEFAULT_MAX_CONNECTION_POOL_SIZE = 100;
    private static final int DEFAULT_CONNECTION_ACQUISITION_TIMEOUT = 60;
    private static final int DEFAULT_AWAIT_INDEX_TIMEOUT = 60;
    private static final boolean DEFAULT_AUTO_CREATE_FULL_TEXT = false;
    private final String prefix;

    public Neo4jConfig() {
        this(null);
    }

    public Neo4jConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        Neo4jConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(Neo4jConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        Neo4jConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = Neo4jConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    @Override
    public String name() {
        return "forage-vectordb-neo4j";
    }

    /**
     * Returns the Neo4j server URI.
     */
    public String uri() {
        return ConfigStore.getInstance().get(URI.asNamed(prefix)).orElse(DEFAULT_URI);
    }

    /**
     * Returns the Neo4j username.
     */
    public String user() {
        return ConfigStore.getInstance().get(USER.asNamed(prefix)).orElse(DEFAULT_USER);
    }

    /**
     * Returns the Neo4j password.
     */
    public String password() {
        return ConfigStore.getInstance()
                .get(PASSWORD.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Neo4j password is required but not configured"));
    }

    /**
     * Returns the database name.
     */
    public String databaseName() {
        return ConfigStore.getInstance().get(DATABASE_NAME.asNamed(prefix)).orElse(DEFAULT_DATABASE_NAME);
    }

    /**
     * Returns the vector index name.
     */
    public String indexName() {
        return ConfigStore.getInstance().get(INDEX_NAME.asNamed(prefix)).orElse(DEFAULT_INDEX_NAME);
    }

    /**
     * Returns the node label.
     */
    public String label() {
        return ConfigStore.getInstance().get(LABEL.asNamed(prefix)).orElse(DEFAULT_LABEL);
    }

    /**
     * Returns the embedding property name.
     */
    public String embeddingProperty() {
        return ConfigStore.getInstance().get(EMBEDDING_PROPERTY.asNamed(prefix)).orElse(DEFAULT_EMBEDDING_PROPERTY);
    }

    /**
     * Returns the text property name.
     */
    public String textProperty() {
        return ConfigStore.getInstance().get(TEXT_PROPERTY.asNamed(prefix)).orElse(DEFAULT_TEXT_PROPERTY);
    }

    /**
     * Returns the ID property name.
     */
    public String idProperty() {
        return ConfigStore.getInstance().get(ID_PROPERTY.asNamed(prefix)).orElse(DEFAULT_ID_PROPERTY);
    }

    /**
     * Returns the metadata prefix.
     */
    public String metadataPrefix() {
        return ConfigStore.getInstance().get(METADATA_PREFIX.asNamed(prefix)).orElse(DEFAULT_METADATA_PREFIX);
    }

    /**
     * Returns the dimension of the vectors.
     */
    public int dimension() {
        return ConfigStore.getInstance()
                .get(DIMENSION.asNamed(prefix))
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Vector dimension is required but not configured"));
    }

    /**
     * Returns whether SSL encryption is enabled.
     */
    public boolean withEncryption() {
        return ConfigStore.getInstance()
                .get(WITH_ENCRYPTION.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(DEFAULT_WITH_ENCRYPTION);
    }

    /**
     * Returns the connection timeout duration.
     */
    public Duration connectionTimeout() {
        return Duration.ofSeconds(ConfigStore.getInstance()
                .get(CONNECTION_TIMEOUT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(DEFAULT_CONNECTION_TIMEOUT));
    }

    /**
     * Returns the maximum connection lifetime duration.
     */
    public Duration maxConnectionLifetime() {
        return Duration.ofMinutes(ConfigStore.getInstance()
                .get(MAX_CONNECTION_LIFETIME.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(DEFAULT_MAX_CONNECTION_LIFETIME));
    }

    /**
     * Returns the maximum connection pool size.
     */
    public int maxConnectionPoolSize() {
        return ConfigStore.getInstance()
                .get(MAX_CONNECTION_POOL_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(DEFAULT_MAX_CONNECTION_POOL_SIZE);
    }

    /**
     * Returns the connection acquisition timeout duration.
     */
    public Duration connectionAcquisitionTimeout() {
        return Duration.ofSeconds(ConfigStore.getInstance()
                .get(CONNECTION_ACQUISITION_TIMEOUT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(DEFAULT_CONNECTION_ACQUISITION_TIMEOUT));
    }

    /**
     * Returns the index creation timeout duration.
     */
    public Duration awaitIndexTimeout() {
        return Duration.ofSeconds(ConfigStore.getInstance()
                .get(AWAIT_INDEX_TIMEOUT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(DEFAULT_AWAIT_INDEX_TIMEOUT));
    }

    /**
     * Returns the custom retrieval query.
     */
    public String retrievalQuery() {
        return ConfigStore.getInstance().get(RETRIEVAL_QUERY.asNamed(prefix)).orElse(null);
    }

    /**
     * Returns the custom entity creation query.
     */
    public String entityCreationQuery() {
        return ConfigStore.getInstance()
                .get(ENTITY_CREATION_QUERY.asNamed(prefix))
                .orElse(null);
    }

    /**
     * Returns the full text index name.
     */
    public String fullTextIndexName() {
        return ConfigStore.getInstance()
                .get(FULL_TEXT_INDEX_NAME.asNamed(prefix))
                .orElse(null);
    }

    /**
     * Returns the full text query.
     */
    public String fullTextQuery() {
        return ConfigStore.getInstance().get(FULL_TEXT_QUERY.asNamed(prefix)).orElse(null);
    }

    /**
     * Returns the full text retrieval query.
     */
    public String fullTextRetrievalQuery() {
        return ConfigStore.getInstance()
                .get(FULL_TEXT_RETRIEVAL_QUERY.asNamed(prefix))
                .orElse(null);
    }

    /**
     * Returns whether to auto create full text index.
     */
    public boolean autoCreateFullText() {
        return ConfigStore.getInstance()
                .get(AUTO_CREATE_FULL_TEXT.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(DEFAULT_AUTO_CREATE_FULL_TEXT);
    }
}
