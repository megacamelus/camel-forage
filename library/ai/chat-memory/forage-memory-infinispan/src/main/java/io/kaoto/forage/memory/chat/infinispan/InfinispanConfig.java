package io.kaoto.forage.memory.chat.infinispan;

import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.CACHE_NAME;
import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.CONNECTION_TIMEOUT;
import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.MAX_RETRIES;
import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.PASSWORD;
import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.POOL_MAX_ACTIVE;
import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.POOL_MAX_WAIT;
import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.POOL_MIN_IDLE;
import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.REALM;
import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.SASL_MECHANISM;
import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.SERVER_LIST;
import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.SOCKET_TIMEOUT;
import static io.kaoto.forage.memory.chat.infinispan.InfinispanConfigEntries.USERNAME;

import java.util.Optional;
import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class for Infinispan-based chat memory storage in the Forage framework.
 *
 * <p>This configuration manages connection parameters for Infinispan clusters used to store
 * persistent chat conversation history. It follows the standard Forage configuration
 * pattern with support for multiple configuration sources and environment-specific overrides.
 *
 * <p><strong>Configuration Properties:</strong>
 * <ul>
 *   <li><code>infinispan.server-list</code> - Comma-separated list of Infinispan servers (default: localhost:11222) - required</li>
 *   <li><code>infinispan.cache-name</code> - Name of the cache for storing chat messages (default: chat-memory) - required</li>
 *   <li><code>infinispan.username</code> - Username for authentication (optional)</li>
 *   <li><code>infinispan.password</code> - Password for authentication (optional)</li>
 *   <li><code>infinispan.realm</code> - Security realm (default: default)</li>
 *   <li><code>infinispan.sasl-mechanism</code> - SASL mechanism for authentication (default: DIGEST-MD5)</li>
 *   <li><code>infinispan.connection-timeout</code> - Connection timeout in milliseconds (default: 60000)</li>
 *   <li><code>infinispan.socket-timeout</code> - Socket timeout in milliseconds (default: 60000)</li>
 *   <li><code>infinispan.max-retries</code> - Maximum number of connection retries (default: 3)</li>
 *   <li><code>infinispan.pool.max-active</code> - Maximum active connections per server (default: 20)</li>
 *   <li><code>infinispan.pool.max-wait</code> - Maximum time to wait for connection in milliseconds (default: 3000)</li>
 * </ul>
 *
 * <p><strong>Configuration Sources (in order of precedence):</strong>
 * <ol>
 *   <li>Environment variables: {@code INFINISPAN_SERVER_LIST}, {@code INFINISPAN_CACHE_NAME}, {@code INFINISPAN_USERNAME}, {@code INFINISPAN_POOL_MAX_ACTIVE}, etc.</li>
 *   <li>System properties: {@code infinispan.server-list}, {@code infinispan.cache-name}, {@code infinispan.username}, etc.</li>
 *   <li>Configuration file: {@code forage-memory-infinispan.properties}</li>
 *   <li>Default values where applicable</li>
 * </ol>
 *
 * <p><strong>Example Environment Configuration:</strong>
 * <pre>{@code
 * export INFINISPAN_SERVER_LIST=infinispan1.example.com:11222,infinispan2.example.com:11222
 * export INFINISPAN_CACHE_NAME=chat-memory
 * export INFINISPAN_USERNAME=admin
 * export INFINISPAN_PASSWORD=secret123
 * export INFINISPAN_CONNECTION_TIMEOUT=30000
 * export INFINISPAN_POOL_MAX_ACTIVE=25
 * }</pre>
 *
 * <p><strong>Example System Properties:</strong>
 * <pre>{@code
 * -Dinfinispan.server-list=infinispan1.example.com:11222,infinispan2.example.com:11222
 * -Dinfinispan.cache-name=chat-memory
 * -Dinfinispan.username=admin
 * -Dinfinispan.password=secret123
 * -Dinfinispan.connection-timeout=30000
 * -Dinfinispan.pool.max-active=25
 * }</pre>
 *
 * <p><strong>Example Properties File (forage-memory-infinispan.properties):</strong>
 * <pre>
 * infinispan.server-list=infinispan1.example.com:11222,infinispan2.example.com:11222
 * infinispan.cache-name=chat-memory
 * infinispan.username=admin
 * infinispan.password=secret123
 * infinispan.realm=default
 * infinispan.sasl-mechanism=DIGEST-MD5
 * infinispan.connection-timeout=30000
 * infinispan.socket-timeout=30000
 * infinispan.max-retries=5
 * infinispan.pool.max-active=25
 * infinispan.pool.min-idle=2
 * infinispan.pool.max-wait=5000
 * </pre>
 *
 * @see Config
 * @see ConfigStore
 * @see PersistentInfinispanStore
 * @since 1.0
 */
public class InfinispanConfig implements Config {
    private static final Logger LOG = LoggerFactory.getLogger(InfinispanConfig.class);

    private final String prefix;

    /**
     * Creates a new Infinispan configuration instance and registers configuration entries
     * with the central {@link ConfigStore}.
     *
     * <p>This constructor automatically registers all Infinispan configuration parameters
     * and their corresponding environment variable mappings. It also sets up the
     * configuration loader to process property files if they exist.
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

    /**
     * Returns the comma-separated list of Infinispan server addresses.
     *
     * @return the server list in format "host1:port1,host2:port2", defaults to "localhost:11222" if not configured
     */
    public String serverList() {
        return ConfigStore.getInstance().get(SERVER_LIST.asNamed(prefix)).orElse(SERVER_LIST.defaultValue());
    }

    /**
     * Returns the name of the Infinispan cache to use for storing chat messages.
     *
     * @return the cache name, defaults to "chat-memory" if not configured
     */
    public String cacheName() {
        return ConfigStore.getInstance().get(CACHE_NAME.asNamed(prefix)).orElse(CACHE_NAME.defaultValue());
    }

    /**
     * Returns the username for Infinispan authentication.
     *
     * @return the username, or {@code null} if no authentication is required
     */
    public String username() {
        return ConfigStore.getInstance().get(USERNAME.asNamed(prefix)).orElse(USERNAME.defaultValue());
    }

    /**
     * Returns the password for Infinispan authentication.
     *
     * @return the password, or {@code null} if no authentication is required
     */
    public String password() {
        return ConfigStore.getInstance().get(PASSWORD.asNamed(prefix)).orElse(PASSWORD.defaultValue());
    }

    /**
     * Returns the security realm for Infinispan authentication.
     *
     * @return the realm, defaults to "default" if not configured
     */
    public String realm() {
        return ConfigStore.getInstance().get(REALM.asNamed(prefix)).orElse(REALM.defaultValue());
    }

    /**
     * Returns the SASL mechanism for Infinispan authentication.
     *
     * @return the SASL mechanism, defaults to "DIGEST-MD5" if not configured
     */
    public String saslMechanism() {
        return ConfigStore.getInstance().get(SASL_MECHANISM.asNamed(prefix)).orElse(SASL_MECHANISM.defaultValue());
    }

    /**
     * Returns the connection timeout in milliseconds.
     *
     * @return the connection timeout in milliseconds, defaults to 60000ms if not configured
     * @throws IllegalArgumentException if the configured timeout value is not a valid integer
     */
    public int connectionTimeout() {
        return ConfigStore.getInstance()
                .get(CONNECTION_TIMEOUT.asNamed(prefix))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan connection timeout value: " + value, e);
                    }
                })
                .orElse(Integer.parseInt(CONNECTION_TIMEOUT.defaultValue()));
    }

    /**
     * Returns the socket timeout in milliseconds.
     *
     * @return the socket timeout in milliseconds, defaults to 60000ms if not configured
     * @throws IllegalArgumentException if the configured timeout value is not a valid integer
     */
    public int socketTimeout() {
        return ConfigStore.getInstance()
                .get(SOCKET_TIMEOUT.asNamed(prefix))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan socket timeout value: " + value, e);
                    }
                })
                .orElse(Integer.parseInt(SOCKET_TIMEOUT.defaultValue()));
    }

    /**
     * Returns the maximum number of connection retries.
     *
     * @return the maximum retries, defaults to 3 if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int maxRetries() {
        return ConfigStore.getInstance()
                .get(MAX_RETRIES.asNamed(prefix))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan max retries value: " + value, e);
                    }
                })
                .orElse(Integer.parseInt(MAX_RETRIES.defaultValue()));
    }

    /**
     * Returns the maximum number of active connections per server.
     *
     * @return the maximum active connections per server, defaults to 20 if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int poolMaxActive() {
        return ConfigStore.getInstance()
                .get(POOL_MAX_ACTIVE.asNamed(prefix))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan pool max-active value: " + value, e);
                    }
                })
                .orElse(Integer.parseInt(POOL_MAX_ACTIVE.defaultValue()));
    }

    /**
     * Returns the minimum number of idle connections per server.
     *
     * @return the minimum idle connections per server, defaults to 1 if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int poolMinIdle() {
        return ConfigStore.getInstance()
                .get(POOL_MIN_IDLE.asNamed(prefix))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan pool min-idle value: " + value, e);
                    }
                })
                .orElse(Integer.parseInt(POOL_MIN_IDLE.defaultValue()));
    }

    /**
     * Returns the maximum time to wait for a connection in milliseconds.
     *
     * @return the maximum wait time in milliseconds, defaults to 3000ms if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int poolMaxWait() {
        return ConfigStore.getInstance()
                .get(POOL_MAX_WAIT.asNamed(prefix))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan pool max-wait value: " + value, e);
                    }
                })
                .orElse(Integer.parseInt(POOL_MAX_WAIT.defaultValue()));
    }

    /**
     * Returns the unique name identifier for this Infinispan memory configuration module.
     *
     * <p>This name is used to identify the module and corresponds to the expected
     * properties file name ({@code forage-memory-infinispan.properties}).
     *
     * @return the module name "forage-memory-infinispan"
     */
    @Override
    public String name() {
        return "forage-memory-infinispan";
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = InfinispanConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    public ConfigurationBuilder toConfigurationBuilder() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServers(serverList());
        builder.connectionTimeout(connectionTimeout());
        builder.socketTimeout(socketTimeout());
        builder.maxRetries(maxRetries());

        // Configure connection pool settings
        LOG.debug(
                "Configuring Infinispan connection pool: maxActive={}, maxIdle={}, maxTotal={}, minIdle={}, maxWait={}ms",
                poolMaxActive(),
                poolMinIdle(),
                poolMaxWait());
        builder.connectionPool()
                .maxActive(poolMaxActive())
                .minIdle(poolMinIdle())
                .maxWait(poolMaxWait());

        // Configure authentication if credentials are provided
        if (username() != null && password() != null) {
            LOG.debug("Configuring SASL authentication with mechanism: {}", saslMechanism());
            builder.security()
                    .authentication()
                    .enable()
                    .username(username())
                    .password(password())
                    .realm(realm())
                    .saslMechanism(saslMechanism());
        }
        return builder;
    }
}
