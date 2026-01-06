package io.kaoto.forage.vectordb.redis;

import static io.kaoto.forage.vectordb.redis.RedisConfigEntries.DIMENSION;
import static io.kaoto.forage.vectordb.redis.RedisConfigEntries.DISTANCE_METRIC;
import static io.kaoto.forage.vectordb.redis.RedisConfigEntries.HOST;
import static io.kaoto.forage.vectordb.redis.RedisConfigEntries.INDEX_NAME;
import static io.kaoto.forage.vectordb.redis.RedisConfigEntries.METADATA_FIELDS;
import static io.kaoto.forage.vectordb.redis.RedisConfigEntries.PASSWORD;
import static io.kaoto.forage.vectordb.redis.RedisConfigEntries.PORT;
import static io.kaoto.forage.vectordb.redis.RedisConfigEntries.PREFIX;
import static io.kaoto.forage.vectordb.redis.RedisConfigEntries.USER;

import java.util.Optional;
import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;

/**
 * Configuration class for Redis vector database integration in the Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use Redis
 * as a vector database. It handles server connection details, index configuration, and
 * vector search parameters through environment variables with appropriate fallback
 * mechanisms and default values.
 *
 * <p><strong>Configuration Parameters:</strong>
 * <ul>
 *   <li><strong>REDIS_HOST</strong> - The Redis server host (default: "localhost")</li>
 *   <li><strong>REDIS_PORT</strong> - The Redis server port (default: 6379)</li>
 *   <li><strong>REDIS_USER</strong> - The Redis username (optional)</li>
 *   <li><strong>REDIS_PASSWORD</strong> - The Redis password (optional)</li>
 *   <li><strong>REDIS_DIMENSION</strong> - The dimension of the vectors (required)</li>
 *   <li><strong>REDIS_PREFIX</strong> - The key prefix for vector storage (default: "vector:")</li>
 *   <li><strong>REDIS_INDEX_NAME</strong> - The name of the vector index (default: "vector_index")</li>
 *   <li><strong>REDIS_METADATA_FIELDS</strong> - Comma-separated list of metadata fields (optional)</li>
 *   <li><strong>REDIS_DISTANCE_METRIC</strong> - The distance metric for similarity search (default: "COSINE")</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (REDIS_HOST, REDIS_PORT, REDIS_USER, etc.)</li>
 *   <li>System properties (redis.host, redis.port, redis.user, etc.)</li>
 *   <li>forage-vectordb-redis.properties file in classpath</li>
 *   <li>Default values if none of the above are provided</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables (optional)
 * export REDIS_HOST="redis.example.com"
 * export REDIS_PORT="6379"
 * export REDIS_USER="redis_user"
 * export REDIS_PASSWORD="redis_password"
 * export REDIS_DIMENSION="384"
 * export REDIS_INDEX_NAME="my_vector_index"
 * export REDIS_DISTANCE_METRIC="L2"
 *
 * // Create and use configuration
 * RedisConfig config = new RedisConfig();
 * String host = config.host();              // Returns the configured host
 * int port = config.port();                 // Returns the configured port
 * int dimension = config.dimension();       // Returns the configured dimension
 * }</pre>
 *
 * <p><strong>Default Values:</strong>
 * <ul>
 *   <li>Host: "localhost"</li>
 *   <li>Port: 6379</li>
 *   <li>Prefix: "vector:"</li>
 *   <li>Index Name: "vector_index"</li>
 *   <li>Distance Metric: "COSINE"</li>
 *   <li>User, Password, Metadata Fields: No defaults (optional)</li>
 *   <li>Dimension: Required (no default)</li>
 * </ul>
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
public class RedisConfig implements Config {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 6379;
    private static final String DEFAULT_PREFIX = "embedding:";
    private static final String DEFAULT_INDEX_NAME = "embedding-index";
    private static final String DEFAULT_DISTANCE_METRIC = "COSINE";
    private final String prefix;

    public RedisConfig() {
        this(null);
    }

    public RedisConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        RedisConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(RedisConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        RedisConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = RedisConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    @Override
    public String name() {
        return "forage-vectordb-redis";
    }

    /**
     * Returns the Redis server host.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>REDIS_HOST environment variable</li>
     *   <li>redis.host system property</li>
     *   <li>host property in forage-vectordb-redis.properties</li>
     *   <li>Default value: "localhost"</li>
     * </ol>
     *
     * @return the Redis server host, never null
     */
    public String host() {
        return ConfigStore.getInstance().get(HOST.asNamed(prefix)).orElse(DEFAULT_HOST);
    }

    /**
     * Returns the Redis server port.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>REDIS_PORT environment variable</li>
     *   <li>redis.port system property</li>
     *   <li>port property in forage-vectordb-redis.properties</li>
     *   <li>Default value: 6379</li>
     * </ol>
     *
     * @return the Redis server port
     */
    public int port() {
        return ConfigStore.getInstance()
                .get(PORT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(DEFAULT_PORT);
    }

    /**
     * Returns the Redis username.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>REDIS_USER environment variable</li>
     *   <li>redis.user system property</li>
     *   <li>user property in forage-vectordb-redis.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * @return the Redis username, or null if not configured
     */
    public String user() {
        return ConfigStore.getInstance().get(USER.asNamed(prefix)).orElse(null);
    }

    /**
     * Returns the Redis password.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>REDIS_PASSWORD environment variable</li>
     *   <li>redis.password system property</li>
     *   <li>password property in forage-vectordb-redis.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * @return the Redis password, or null if not configured
     */
    public String password() {
        return ConfigStore.getInstance().get(PASSWORD.asNamed(prefix)).orElse(null);
    }

    /**
     * Returns the dimension of the vectors.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>REDIS_DIMENSION environment variable</li>
     *   <li>redis.dimension system property</li>
     *   <li>dimension property in forage-vectordb-redis.properties</li>
     *   <li>Required (throws exception if not configured)</li>
     * </ol>
     *
     * @return the vector dimension
     * @throws MissingConfigException if dimension is not configured
     */
    public int dimension() {
        return ConfigStore.getInstance()
                .get(DIMENSION.asNamed(prefix))
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Vector dimension is required but not configured"));
    }

    /**
     * Returns the key prefix for vector storage.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>REDIS_PREFIX environment variable</li>
     *   <li>redis.prefix system property</li>
     *   <li>prefix property in forage-vectordb-redis.properties</li>
     *   <li>Default value: "vector:"</li>
     * </ol>
     *
     * @return the key prefix for vector storage, never null
     */
    public String prefix() {
        return ConfigStore.getInstance().get(PREFIX.asNamed(prefix)).orElse(DEFAULT_PREFIX);
    }

    /**
     * Returns the name of the vector index.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>REDIS_INDEX_NAME environment variable</li>
     *   <li>redis.index.name system property</li>
     *   <li>index-name property in forage-vectordb-redis.properties</li>
     *   <li>Default value: "vector_index"</li>
     * </ol>
     *
     * @return the vector index name, never null
     */
    public String indexName() {
        return ConfigStore.getInstance().get(INDEX_NAME.asNamed(prefix)).orElse(DEFAULT_INDEX_NAME);
    }

    /**
     * Returns the comma-separated list of metadata fields.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>REDIS_METADATA_FIELDS environment variable</li>
     *   <li>redis.metadata.fields system property</li>
     *   <li>metadata-fields property in forage-vectordb-redis.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * @return the metadata fields as a comma-separated string, or null if not configured
     */
    public String metadataFields() {
        return ConfigStore.getInstance().get(METADATA_FIELDS.asNamed(prefix)).orElse(null);
    }

    /**
     * Returns the distance metric for similarity search.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>REDIS_DISTANCE_METRIC environment variable</li>
     *   <li>redis.distance.metric system property</li>
     *   <li>distance-metric property in forage-vectordb-redis.properties</li>
     *   <li>Default value: "COSINE"</li>
     * </ol>
     *
     * <p><strong>Valid Values:</strong> COSINE, L2, IP (Inner Product)
     *
     * @return the distance metric, never null
     */
    public String distanceMetric() {
        return ConfigStore.getInstance().get(DISTANCE_METRIC.asNamed(prefix)).orElse(DEFAULT_DISTANCE_METRIC);
    }
}
