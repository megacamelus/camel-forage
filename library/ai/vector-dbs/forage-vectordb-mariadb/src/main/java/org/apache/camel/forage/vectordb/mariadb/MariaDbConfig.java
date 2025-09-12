package org.apache.camel.forage.vectordb.mariadb;

import static org.apache.camel.forage.vectordb.mariadb.MariaDbConfigEntries.CONTENT_FIELD_NAME;
import static org.apache.camel.forage.vectordb.mariadb.MariaDbConfigEntries.CREATE_TABLE;
import static org.apache.camel.forage.vectordb.mariadb.MariaDbConfigEntries.DIMENSION;
import static org.apache.camel.forage.vectordb.mariadb.MariaDbConfigEntries.DISTANCE_TYPE;
import static org.apache.camel.forage.vectordb.mariadb.MariaDbConfigEntries.DROP_TABLE_FIRST;
import static org.apache.camel.forage.vectordb.mariadb.MariaDbConfigEntries.EMBEDDING_FIELD_NAME;
import static org.apache.camel.forage.vectordb.mariadb.MariaDbConfigEntries.ID_FIELD_NAME;
import static org.apache.camel.forage.vectordb.mariadb.MariaDbConfigEntries.PASSWORD;
import static org.apache.camel.forage.vectordb.mariadb.MariaDbConfigEntries.TABLE;
import static org.apache.camel.forage.vectordb.mariadb.MariaDbConfigEntries.URL;
import static org.apache.camel.forage.vectordb.mariadb.MariaDbConfigEntries.USER;

import dev.langchain4j.store.embedding.mariadb.MariaDBDistanceType;
import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

/**
 * Configuration class for MariaDB vector database connections.
 *
 * <p>This class manages all configuration parameters required to connect to and configure
 * a MariaDB instance for vector storage and similarity search operations. It supports
 * multiple configuration sources and named/prefixed configurations for multi-instance setups.</p>
 *
 * <p>Configuration sources (in order of precedence):</p>
 * <ol>
 *   <li>Environment variables (highest precedence)</li>
 *   <li>System properties</li>
 *   <li>Configuration files (forage-vectordb-mariadb.properties)</li>
 *   <li>Default values (where applicable)</li>
 * </ol>
 *
 * <p>For named configurations, properties can be prefixed with the configuration name:
 * {@code <prefix>.mariadb.<property>}</p>
 *
 * @see MariaDbConfigEntries
 * @see MariaDbProvider
 */
public class MariaDbConfig implements Config {

    private static final int DEFAULT_DIMENSION = 384;

    private final String prefix;

    /**
     * Creates a new MariaDB configuration using default (non-prefixed) properties.
     */
    public MariaDbConfig() {
        this(null);
    }

    /**
     * Creates a new MariaDB configuration with an optional prefix for named configurations.
     *
     * <p>This constructor follows the standard three-phase initialization pattern:</p>
     * <ol>
     *   <li>Register prefixed configuration modules if prefix is provided</li>
     *   <li>Load configuration from properties files</li>
     *   <li>Load overrides from environment variables and system properties</li>
     * </ol>
     *
     * @param prefix optional prefix for scoped configuration properties
     */
    public MariaDbConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        MariaDbConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(MariaDbConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        MariaDbConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-vectordb-mariadb";
    }

    /**
     * Gets the MariaDB JDBC URL for database connection.
     *
     * @return the JDBC URL (e.g., "jdbc:mariadb://localhost:3306/vectordb")
     * @throws MissingConfigException if URL is not configured
     */
    public String url() {
        return ConfigStore.getInstance()
                .get(URL.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing MariaDB JDBC URL"));
    }

    /**
     * Gets the database username for authentication.
     *
     * @return the database username
     * @throws MissingConfigException if username is not configured
     */
    public String user() {
        return ConfigStore.getInstance()
                .get(USER.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing MariaDB user"));
    }

    /**
     * Gets the database password for authentication.
     *
     * @return the database password, or null if not configured
     */
    public String password() {
        return ConfigStore.getInstance().get(PASSWORD.asNamed(prefix)).orElse(null);
    }

    /**
     * Gets the table name where embeddings will be stored.
     *
     * @return the table name (default: "embeddings")
     */
    public String table() {
        return ConfigStore.getInstance().get(TABLE.asNamed(prefix)).orElse("embeddings");
    }

    /**
     * Gets the distance calculation method for vector similarity.
     *
     * @return the distance type (default: COSINE)
     * @throws IllegalArgumentException if an invalid distance type is configured
     */
    public MariaDBDistanceType distanceType() {
        return ConfigStore.getInstance()
                .get(DISTANCE_TYPE.asNamed(prefix))
                .map(value -> MariaDBDistanceType.valueOf(value.toUpperCase()))
                .orElse(MariaDBDistanceType.COSINE);
    }

    /**
     * Gets the name of the ID field in the embeddings table.
     *
     * @return the ID field name (default: "id")
     */
    public String idFieldName() {
        return ConfigStore.getInstance().get(ID_FIELD_NAME.asNamed(prefix)).orElse("id");
    }

    /**
     * Gets the name of the embedding vector field in the embeddings table.
     *
     * @return the embedding field name (default: "embedding")
     */
    public String embeddingFieldName() {
        return ConfigStore.getInstance()
                .get(EMBEDDING_FIELD_NAME.asNamed(prefix))
                .orElse("embedding");
    }

    /**
     * Gets the name of the content text field in the embeddings table.
     *
     * @return the content field name (default: "content")
     */
    public String contentFieldName() {
        return ConfigStore.getInstance().get(CONTENT_FIELD_NAME.asNamed(prefix)).orElse("content");
    }

    /**
     * Determines whether to automatically create the embeddings table if it doesn't exist.
     *
     * @return true to create table automatically (default: true)
     */
    public Boolean createTable() {
        return ConfigStore.getInstance()
                .get(CREATE_TABLE.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    /**
     * Determines whether to drop the table before creating it (useful for testing).
     *
     * @return true to drop table first (default: false)
     */
    public Boolean dropTableFirst() {
        return ConfigStore.getInstance()
                .get(DROP_TABLE_FIRST.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    /**
     * Gets the vector dimension size for the embeddings.
     *
     * @return the configured vector dimension, or the default of 384
     */
    public Integer dimension() {
        return ConfigStore.getInstance()
                .get(DIMENSION.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(DEFAULT_DIMENSION);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = MariaDbConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }
}
