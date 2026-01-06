package io.kaoto.forage.vectordb.infinispan;

import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.CACHE_CONFIG;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.CACHE_NAME;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.CREATE_CACHE;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.DIMENSION;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.DISTANCE;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.FILE_NAME;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.HOST;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.LANGCHAIN_ITEM_NAME;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.METADATA_ITEM_NAME;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.PACKAGE_NAME;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.PASSWORD;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.PORT;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.REGISTER_SCHEMA;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.SIMILARITY;
import static io.kaoto.forage.vectordb.infinispan.InfinispanConfigEntries.USERNAME;

import java.util.Optional;
import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;

/**
 * Configuration class for Infinispan vector database integration in the Camel Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use Infinispan
 * as a vector database. It handles cache configuration, vector similarity settings, and schema
 * management through environment variables with appropriate fallback mechanisms and default values.
 *
 * <p><strong>Configuration Parameters:</strong>
 * <ul>
 *   <li><strong>INFINISPAN_CACHE_NAME</strong> - The name of the cache to use (required)</li>
 *   <li><strong>INFINISPAN_DIMENSION</strong> - Vector dimension for embeddings (required)</li>
 *   <li><strong>INFINISPAN_DISTANCE</strong> - Distance metric for similarity (optional)</li>
 *   <li><strong>INFINISPAN_SIMILARITY</strong> - Similarity algorithm (optional)</li>
 *   <li><strong>INFINISPAN_CACHE_CONFIG</strong> - Cache configuration settings (optional)</li>
 *   <li><strong>INFINISPAN_PACKAGE_NAME</strong> - Package name for generated classes (optional)</li>
 *   <li><strong>INFINISPAN_FILE_NAME</strong> - Schema file name (optional)</li>
 *   <li><strong>INFINISPAN_LANGCHAIN_ITEM_NAME</strong> - LangChain item class name (optional)</li>
 *   <li><strong>INFINISPAN_METADATA_ITEM_NAME</strong> - Metadata item class name (optional)</li>
 *   <li><strong>INFINISPAN_REGISTER_SCHEMA</strong> - Whether to register schema, true/false (optional)</li>
 *   <li><strong>INFINISPAN_CREATE_CACHE</strong> - Whether to create cache if not exists, true/false (optional)</li>
 *   <li><strong>INFINISPAN_HOST</strong> - Host address of the Infinispan server (optional)</li>
 *   <li><strong>INFINISPAN_PORT</strong> - Port number of the Infinispan server (optional)</li>
 *   <li><strong>INFINISPAN_USERNAME</strong> - Username for authentication (optional)</li>
 *   <li><strong>INFINISPAN_PASSWORD</strong> - Password for authentication (optional)</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (INFINISPAN_CACHE_NAME, INFINISPAN_DIMENSION, etc.)</li>
 *   <li>System properties (infinispan.cache.name, infinispan.dimension, etc.)</li>
 *   <li>forage-vectordb-infinispan.properties file in classpath</li>
 *   <li>Default values where applicable</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables
 * export INFINISPAN_CACHE_NAME="vector-cache"
 * export INFINISPAN_DIMENSION="384"
 * export INFINISPAN_DISTANCE="COSINE"
 * export INFINISPAN_SIMILARITY="COSINE"
 * export INFINISPAN_REGISTER_SCHEMA="true"
 * export INFINISPAN_CREATE_CACHE="true"
 * export INFINISPAN_HOST="localhost"
 * export INFINISPAN_PORT="11222"
 * export INFINISPAN_USERNAME="admin"
 * export INFINISPAN_PASSWORD="admin"
 *
 * // Create and use configuration
 * InfinispanConfig config = new InfinispanConfig();
 * String cacheName = config.cacheName();        // Returns the configured cache name
 * Integer dimension = config.dimension();       // Returns the configured dimension
 * String distance = config.distance();          // Returns distance metric or default
 * }</pre>
 *
 * <p><strong>Default Values:</strong>
 * <ul>
 *   <li>Distance: 3 (cosine distance metric)</li>
 *   <li>Similarity: "COSINE" (cosine similarity algorithm)</li>
 *   <li>Package Name: "io.kaoto.forage.vectordb.infinispan.schema"</li>
 *   <li>File Name: "langchain-item.proto"</li>
 *   <li>LangChain Item Name: "LangChainItem"</li>
 *   <li>Metadata Item Name: "MetadataItem"</li>
 *   <li>Register Schema: true</li>
 *   <li>Create Cache: true</li>
 *   <li>Host: "localhost"</li>
 *   <li>Port: 11222</li>
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
public class InfinispanConfig implements Config {
    private static final int DEFAULT_DISTANCE = 3;
    private static final String DEFAULT_SIMILARITY = "COSINE";
    private static final String DEFAULT_PACKAGE_NAME = "io.kaoto.forage.vectordb.infinispan.schema";
    private static final String DEFAULT_FILE_NAME = "langchain-item.proto";
    private static final String DEFAULT_LANGCHAIN_ITEM_NAME = "LangChainItem";
    private static final String DEFAULT_METADATA_ITEM_NAME = "MetadataItem";
    private static final boolean DEFAULT_REGISTER_SCHEMA = true;
    private static final boolean DEFAULT_CREATE_CACHE = true;
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 11222;

    private final String prefix;

    /**
     * Constructs a new InfinispanConfig and registers configuration parameters with the ConfigStore.
     *
     * <p>During construction, this class:
     * <ul>
     *   <li>Registers the cache name configuration to be sourced from INFINISPAN_CACHE_NAME environment variable</li>
     *   <li>Registers the dimension configuration to be sourced from INFINISPAN_DIMENSION environment variable</li>
     *   <li>Registers optional parameters (distance, similarity, etc.) from their respective environment variables</li>
     *   <li>Attempts to load additional properties from forage-vectordb-infinispan.properties</li>
     * </ul>
     *
     * <p>Configuration values are resolved when this constructor is called, with default values
     * used when no configuration is provided through environment variables, system properties,
     * or configuration files.
     */
    public InfinispanConfig() {
        this(null);
    }

    public InfinispanConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        InfinispanConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(InfinispanConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        InfinispanConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-vectordb-infinispan";
    }

    /**
     * Returns the name of the Infinispan cache to use.
     *
     * <p>This method retrieves the cache name that specifies which Infinispan cache
     * should be used for vector operations. The cache must exist on the
     * configured Infinispan server or be created if createCache is enabled.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_CACHE_NAME environment variable</li>
     *   <li>infinispan.cache.name system property</li>
     *   <li>cache-name property in forage-vectordb-infinispan.properties</li>
     * </ol>
     *
     * @return the Infinispan cache name
     * @throws MissingConfigException if no cache name is configured
     */
    public String cacheName() {
        return ConfigStore.getInstance()
                .get(CACHE_NAME.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Infinispan cache name"));
    }

    /**
     * Returns the dimension for vector embeddings.
     *
     * <p>This method retrieves the dimension setting that specifies the size of the
     * vector embeddings that will be stored in the Infinispan cache. This must match
     * the dimension of the embeddings being stored.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_DIMENSION environment variable</li>
     *   <li>infinispan.dimension system property</li>
     *   <li>dimension property in forage-vectordb-infinispan.properties</li>
     * </ol>
     *
     * @return the vector dimension
     * @throws MissingConfigException if no dimension is configured
     */
    public Integer dimension() {
        return ConfigStore.getInstance()
                .get(DIMENSION.asNamed(prefix))
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Infinispan dimension"));
    }

    /**
     * Returns the distance metric for similarity calculations.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_DISTANCE environment variable</li>
     *   <li>infinispan.distance system property</li>
     *   <li>distance property in forage-vectordb-infinispan.properties</li>
     *   <li>Default value: "COSINE"</li>
     * </ol>
     *
     * @return the distance metric
     */
    public Integer distance() {
        return ConfigStore.getInstance()
                .get(DISTANCE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(DEFAULT_DISTANCE);
    }

    /**
     * Returns the similarity algorithm for vector comparisons.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_SIMILARITY environment variable</li>
     *   <li>infinispan.similarity system property</li>
     *   <li>similarity property in forage-vectordb-infinispan.properties</li>
     *   <li>Default value: "COSINE"</li>
     * </ol>
     *
     * @return the similarity algorithm
     */
    public String similarity() {
        return ConfigStore.getInstance().get(SIMILARITY.asNamed(prefix)).orElse(DEFAULT_SIMILARITY);
    }

    /**
     * Returns the cache configuration settings.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_CACHE_CONFIG environment variable</li>
     *   <li>infinispan.cache.config system property</li>
     *   <li>cache-config property in forage-vectordb-infinispan.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * @return the cache configuration, or null if not configured
     */
    public String cacheConfig() {
        return ConfigStore.getInstance().get(CACHE_CONFIG.asNamed(prefix)).orElse(null);
    }

    /**
     * Returns the package name for generated schema classes.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_PACKAGE_NAME environment variable</li>
     *   <li>infinispan.package.name system property</li>
     *   <li>package-name property in forage-vectordb-infinispan.properties</li>
     *   <li>Default value: "io.kaoto.forage.vectordb.infinispan.schema"</li>
     * </ol>
     *
     * @return the package name for generated classes
     */
    public String packageName() {
        return ConfigStore.getInstance().get(PACKAGE_NAME.asNamed(prefix)).orElse(DEFAULT_PACKAGE_NAME);
    }

    /**
     * Returns the schema file name.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_FILE_NAME environment variable</li>
     *   <li>infinispan.file.name system property</li>
     *   <li>file-name property in forage-vectordb-infinispan.properties</li>
     *   <li>Default value: "langchain-item.proto"</li>
     * </ol>
     *
     * @return the schema file name
     */
    public String fileName() {
        return ConfigStore.getInstance().get(FILE_NAME.asNamed(prefix)).orElse(DEFAULT_FILE_NAME);
    }

    /**
     * Returns the LangChain item class name.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_LANGCHAIN_ITEM_NAME environment variable</li>
     *   <li>infinispan.langchain.item.name system property</li>
     *   <li>langchain-item-name property in forage-vectordb-infinispan.properties</li>
     *   <li>Default value: "LangChainItem"</li>
     * </ol>
     *
     * @return the LangChain item class name
     */
    public String langchainItemName() {
        return ConfigStore.getInstance()
                .get(LANGCHAIN_ITEM_NAME.asNamed(prefix))
                .orElse(DEFAULT_LANGCHAIN_ITEM_NAME);
    }

    /**
     * Returns the metadata item class name.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_METADATA_ITEM_NAME environment variable</li>
     *   <li>infinispan.metadata.item.name system property</li>
     *   <li>metadata-item-name property in forage-vectordb-infinispan.properties</li>
     *   <li>Default value: "MetadataItem"</li>
     * </ol>
     *
     * @return the metadata item class name
     */
    public String metadataItemName() {
        return ConfigStore.getInstance().get(METADATA_ITEM_NAME.asNamed(prefix)).orElse(DEFAULT_METADATA_ITEM_NAME);
    }

    /**
     * Returns whether to register the schema automatically.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_REGISTER_SCHEMA environment variable</li>
     *   <li>infinispan.register.schema system property</li>
     *   <li>register-schema property in forage-vectordb-infinispan.properties</li>
     *   <li>Default value: true</li>
     * </ol>
     *
     * @return true if schema should be registered automatically
     */
    public Boolean registerSchema() {
        return ConfigStore.getInstance()
                .get(REGISTER_SCHEMA.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(DEFAULT_REGISTER_SCHEMA);
    }

    /**
     * Returns whether to create the cache if it doesn't exist.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_CREATE_CACHE environment variable</li>
     *   <li>infinispan.create.cache system property</li>
     *   <li>create-cache property in forage-vectordb-infinispan.properties</li>
     *   <li>Default value: true</li>
     * </ol>
     *
     * @return true if cache should be created if it doesn't exist
     */
    public Boolean createCache() {
        return ConfigStore.getInstance()
                .get(CREATE_CACHE.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(DEFAULT_CREATE_CACHE);
    }

    /**
     * Returns the host address of the Infinispan server.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_HOST environment variable</li>
     *   <li>infinispan.host system property</li>
     *   <li>host property in forage-vectordb-infinispan.properties</li>
     *   <li>Default value: "localhost"</li>
     * </ol>
     *
     * @return the Infinispan server host address
     */
    public String host() {
        return ConfigStore.getInstance().get(HOST.asNamed(prefix)).orElse(DEFAULT_HOST);
    }

    /**
     * Returns the port number of the Infinispan server.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_PORT environment variable</li>
     *   <li>infinispan.port system property</li>
     *   <li>port property in forage-vectordb-infinispan.properties</li>
     *   <li>Default value: 11222</li>
     * </ol>
     *
     * @return the Infinispan server port number
     */
    public Integer port() {
        return ConfigStore.getInstance()
                .get(PORT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(DEFAULT_PORT);
    }

    /**
     * Returns the username for authentication.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_USERNAME environment variable</li>
     *   <li>infinispan.username system property</li>
     *   <li>username property in forage-vectordb-infinispan.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * @return the username for authentication, or null if not configured
     */
    public String username() {
        return ConfigStore.getInstance().get(USERNAME.asNamed(prefix)).orElse(null);
    }

    /**
     * Returns the password for authentication.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>INFINISPAN_PASSWORD environment variable</li>
     *   <li>infinispan.password system property</li>
     *   <li>password property in forage-vectordb-infinispan.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * @return the password for authentication, or null if not configured
     */
    public String password() {
        return ConfigStore.getInstance().get(PASSWORD.asNamed(prefix)).orElse(null);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = InfinispanConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }
}
