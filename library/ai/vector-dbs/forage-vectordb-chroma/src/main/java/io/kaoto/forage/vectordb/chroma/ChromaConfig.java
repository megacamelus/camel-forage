package io.kaoto.forage.vectordb.chroma;

import static io.kaoto.forage.vectordb.chroma.ChromaConfigEntries.COLLECTION_NAME;
import static io.kaoto.forage.vectordb.chroma.ChromaConfigEntries.LOG_REQUESTS;
import static io.kaoto.forage.vectordb.chroma.ChromaConfigEntries.LOG_RESPONSES;
import static io.kaoto.forage.vectordb.chroma.ChromaConfigEntries.TIMEOUT;
import static io.kaoto.forage.vectordb.chroma.ChromaConfigEntries.URL;

import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;
import java.time.Duration;
import java.util.Optional;

/**
 * Configuration class for Chroma vector database integration in the Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use Chroma
 * vector database. It handles server connection details and database configuration through
 * environment variables with appropriate fallback mechanisms and default values.
 *
 * <p><strong>Configuration Parameters:</strong>
 * <ul>
 *   <li><strong>CHROMA_URL</strong> - The URL of the Chroma server (required)</li>
 *   <li><strong>CHROMA_COLLECTION_NAME</strong> - The name of the collection to use (required)</li>
 *   <li><strong>CHROMA_TIMEOUT</strong> - Request timeout in seconds (optional)</li>
 *   <li><strong>CHROMA_LOG_REQUESTS</strong> - Enable request logging, true/false (optional)</li>
 *   <li><strong>CHROMA_LOG_RESPONSES</strong> - Enable response logging, true/false (optional)</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (CHROMA_URL, CHROMA_COLLECTION_NAME, etc.)</li>
 *   <li>System properties (chroma.url, chroma.collection.name, etc.)</li>
 *   <li>forage-vectordb-chroma.properties file in classpath</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables
 * export CHROMA_URL="http://localhost:8000"
 * export CHROMA_COLLECTION_NAME="my-collection"
 * export CHROMA_TIMEOUT="30"
 * export CHROMA_LOG_REQUESTS="false"
 * export CHROMA_LOG_RESPONSES="false"
 *
 * // Create and use configuration
 * ChromaConfig config = new ChromaConfig();
 * String url = config.url();                    // Returns the configured URL
 * String collection = config.collectionName();  // Returns the configured collection name
 * Duration timeout = config.timeout();          // Returns timeout or null if not set
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
public class ChromaConfig implements Config {

    private final String prefix;
    public static final int DEFAULT_TIMEOUT = 5;

    /**
     * Constructs a new ChromaConfig and registers configuration parameters with the ConfigStore.
     *
     * <p>During construction, this class:
     * <ul>
     *   <li>Registers the URL configuration to be sourced from CHROMA_URL environment variable</li>
     *   <li>Registers the collection name configuration to be sourced from CHROMA_COLLECTION_NAME environment variable</li>
     *   <li>Registers optional parameters (timeout, logRequests, logResponses) from their respective environment variables</li>
     *   <li>Attempts to load additional properties from forage-vectordb-chroma.properties</li>
     * </ul>
     *
     * <p>Configuration values are resolved when this constructor is called.
     */
    public ChromaConfig() {
        this(null);
    }

    public ChromaConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        ChromaConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(ChromaConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        ChromaConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-vectordb-chroma";
    }

    /**
     * Returns the URL of the Chroma server.
     *
     * <p>This method retrieves the URL that was configured through environment variables,
     * system properties, or configuration files. The URL specifies where the Chroma
     * server is running and should include the protocol and port.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>CHROMA_URL environment variable</li>
     *   <li>chroma.url system property</li>
     *   <li>url property in forage-vectordb-chroma.properties</li>
     * </ol>
     *
     * <p><strong>Example Values:</strong>
     * <ul>
     *   <li>"http://localhost:8000" - Local Chroma installation</li>
     *   <li>"http://chroma-server:8000" - Remote server</li>
     *   <li>"https://my-chroma.example.com" - HTTPS endpoint</li>
     * </ul>
     *
     * @return the Chroma server URL
     * @throws MissingConfigException if no URL is configured
     */
    public String url() {
        return ConfigStore.getInstance()
                .get(URL.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Chroma URL"));
    }

    /**
     * Returns the name of the Chroma collection to use.
     *
     * <p>This method retrieves the collection name that specifies which Chroma collection
     * should be used for vector operations. The collection must exist on the
     * configured Chroma server or be created before use.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>CHROMA_COLLECTION_NAME environment variable</li>
     *   <li>chroma.collection.name system property</li>
     *   <li>collection-name property in forage-vectordb-chroma.properties</li>
     * </ol>
     *
     * @return the Chroma collection name
     * @throws MissingConfigException if no collection name is configured
     */
    public String collectionName() {
        return ConfigStore.getInstance()
                .get(COLLECTION_NAME.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Chroma collection name"));
    }

    /**
     * Returns the timeout duration for requests to the Chroma server.
     *
     * <p>This method retrieves the timeout setting that controls how long to wait
     * for responses from the Chroma server before timing out.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>CHROMA_TIMEOUT environment variable</li>
     *   <li>chroma.timeout system property</li>
     *   <li>timeout property in forage-vectordb-chroma.properties</li>
     *   <li>5 second default value</li>
     * </ol>
     *
     * <p><strong>Valid Values:</strong> Positive integers representing seconds
     *
     * @return the timeout duration, or null if not configured
     */
    public Duration timeout() {
        return ConfigStore.getInstance()
                .get(TIMEOUT.asNamed(prefix))
                .map(s -> Duration.ofSeconds(Long.parseLong(s)))
                .orElse(Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    /**
     * Returns whether request logging is enabled.
     *
     * <p>When enabled, the Chroma client will log all requests sent to the server.
     * This is useful for debugging and monitoring but should be disabled in production
     * to avoid logging sensitive information.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>CHROMA_LOG_REQUESTS environment variable</li>
     *   <li>chroma.log.requests system property</li>
     *   <li>log-requests property in forage-vectordb-chroma.properties</li>
     *   <li>default value is true</li>
     * </ol>
     *
     * <p><strong>Valid Values:</strong> "true" or "false" (case-insensitive)
     *
     * @return true if request logging is enabled, false if disabled, null if not configured
     */
    public Boolean logRequests() {
        return ConfigStore.getInstance()
                .get(LOG_REQUESTS.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    /**
     * Returns whether response logging is enabled.
     *
     * <p>When enabled, the Chroma client will log all responses received from the server.
     * This is useful for debugging and monitoring but should be disabled in production
     * to avoid logging sensitive information.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>CHROMA_LOG_RESPONSES environment variable</li>
     *   <li>chroma.log.responses system property</li>
     *   <li>log-responses property in forage-vectordb-chroma.properties</li>
     *   <li>default value is true</li>
     * </ol>
     *
     * <p><strong>Valid Values:</strong> "true" or "false" (case-insensitive)
     *
     * @return true if response logging is enabled, false if disabled, null if not configured
     */
    public Boolean logResponses() {
        return ConfigStore.getInstance()
                .get(LOG_RESPONSES.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = ChromaConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }
}
