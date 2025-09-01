package org.apache.camel.forage.memory.chat.infinispan;

import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;

/**
 * Configuration class for Infinispan-based chat memory storage in the Camel Forage framework.
 *
 * <p>This configuration manages connection parameters for Infinispan clusters used to store
 * persistent chat conversation history. It follows the standard Camel Forage configuration
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

    private static final ConfigModule SERVER_LIST = ConfigModule.of(InfinispanConfig.class, "infinispan.server-list");
    private static final ConfigModule CACHE_NAME = ConfigModule.of(InfinispanConfig.class, "infinispan.cache-name");
    private static final ConfigModule USERNAME = ConfigModule.of(InfinispanConfig.class, "infinispan.username");
    private static final ConfigModule PASSWORD = ConfigModule.of(InfinispanConfig.class, "infinispan.password");
    private static final ConfigModule REALM = ConfigModule.of(InfinispanConfig.class, "infinispan.realm");
    private static final ConfigModule SASL_MECHANISM =
            ConfigModule.of(InfinispanConfig.class, "infinispan.sasl-mechanism");
    private static final ConfigModule CONNECTION_TIMEOUT =
            ConfigModule.of(InfinispanConfig.class, "infinispan.connection-timeout");
    private static final ConfigModule SOCKET_TIMEOUT =
            ConfigModule.of(InfinispanConfig.class, "infinispan.socket-timeout");
    private static final ConfigModule MAX_RETRIES = ConfigModule.of(InfinispanConfig.class, "infinispan.max-retries");

    // Pool configuration
    private static final ConfigModule POOL_MAX_ACTIVE =
            ConfigModule.of(InfinispanConfig.class, "infinispan.pool.max-active");
    private static final ConfigModule POOL_MIN_IDLE =
            ConfigModule.of(InfinispanConfig.class, "infinispan.pool.min-idle");
    private static final ConfigModule POOL_MAX_WAIT =
            ConfigModule.of(InfinispanConfig.class, "infinispan.pool.max-wait");

    /**
     * Creates a new Infinispan configuration instance and registers configuration entries
     * with the central {@link ConfigStore}.
     *
     * <p>This constructor automatically registers all Infinispan configuration parameters
     * and their corresponding environment variable mappings. It also sets up the
     * configuration loader to process property files if they exist.
     */
    public InfinispanConfig() {
        ConfigStore.getInstance().add(SERVER_LIST, ConfigEntry.fromModule(SERVER_LIST, "INFINISPAN_SERVER_LIST"));
        ConfigStore.getInstance().add(CACHE_NAME, ConfigEntry.fromModule(CACHE_NAME, "INFINISPAN_CACHE_NAME"));
        ConfigStore.getInstance().add(USERNAME, ConfigEntry.fromModule(USERNAME, "INFINISPAN_USERNAME"));
        ConfigStore.getInstance().add(PASSWORD, ConfigEntry.fromModule(PASSWORD, "INFINISPAN_PASSWORD"));
        ConfigStore.getInstance().add(REALM, ConfigEntry.fromModule(REALM, "INFINISPAN_REALM"));
        ConfigStore.getInstance()
                .add(SASL_MECHANISM, ConfigEntry.fromModule(SASL_MECHANISM, "INFINISPAN_SASL_MECHANISM"));
        ConfigStore.getInstance()
                .add(CONNECTION_TIMEOUT, ConfigEntry.fromModule(CONNECTION_TIMEOUT, "INFINISPAN_CONNECTION_TIMEOUT"));
        ConfigStore.getInstance()
                .add(SOCKET_TIMEOUT, ConfigEntry.fromModule(SOCKET_TIMEOUT, "INFINISPAN_SOCKET_TIMEOUT"));
        ConfigStore.getInstance().add(MAX_RETRIES, ConfigEntry.fromModule(MAX_RETRIES, "INFINISPAN_MAX_RETRIES"));

        // Pool configuration
        ConfigStore.getInstance()
                .add(POOL_MAX_ACTIVE, ConfigEntry.fromModule(POOL_MAX_ACTIVE, "INFINISPAN_POOL_MAX_ACTIVE"));
        ConfigStore.getInstance().add(POOL_MIN_IDLE, ConfigEntry.fromModule(POOL_MIN_IDLE, "INFINISPAN_POOL_MIN_IDLE"));
        ConfigStore.getInstance().add(POOL_MAX_WAIT, ConfigEntry.fromModule(POOL_MAX_WAIT, "INFINISPAN_POOL_MAX_WAIT"));

        ConfigStore.getInstance().add(InfinispanConfig.class, this, this::register);
    }

    /**
     * Returns the comma-separated list of Infinispan server addresses.
     *
     * @return the server list in format "host1:port1,host2:port2", defaults to "localhost:11222" if not configured
     */
    public String serverList() {
        return ConfigStore.getInstance().get(SERVER_LIST).orElse("localhost:11222");
    }

    /**
     * Returns the name of the Infinispan cache to use for storing chat messages.
     *
     * @return the cache name, defaults to "chat-memory" if not configured
     */
    public String cacheName() {
        return ConfigStore.getInstance().get(CACHE_NAME).orElse("chat-memory");
    }

    /**
     * Returns the username for Infinispan authentication.
     *
     * @return the username, or {@code null} if no authentication is required
     */
    public String username() {
        return ConfigStore.getInstance().get(USERNAME).orElse(null);
    }

    /**
     * Returns the password for Infinispan authentication.
     *
     * @return the password, or {@code null} if no authentication is required
     */
    public String password() {
        return ConfigStore.getInstance().get(PASSWORD).orElse(null);
    }

    /**
     * Returns the security realm for Infinispan authentication.
     *
     * @return the realm, defaults to "default" if not configured
     */
    public String realm() {
        return ConfigStore.getInstance().get(REALM).orElse("default");
    }

    /**
     * Returns the SASL mechanism for Infinispan authentication.
     *
     * @return the SASL mechanism, defaults to "DIGEST-MD5" if not configured
     */
    public String saslMechanism() {
        return ConfigStore.getInstance().get(SASL_MECHANISM).orElse("DIGEST-MD5");
    }

    /**
     * Returns the connection timeout in milliseconds.
     *
     * @return the connection timeout in milliseconds, defaults to 60000ms if not configured
     * @throws IllegalArgumentException if the configured timeout value is not a valid integer
     */
    public int connectionTimeout() {
        return ConfigStore.getInstance()
                .get(CONNECTION_TIMEOUT)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan connection timeout value: " + value, e);
                    }
                })
                .orElse(60000);
    }

    /**
     * Returns the socket timeout in milliseconds.
     *
     * @return the socket timeout in milliseconds, defaults to 60000ms if not configured
     * @throws IllegalArgumentException if the configured timeout value is not a valid integer
     */
    public int socketTimeout() {
        return ConfigStore.getInstance()
                .get(SOCKET_TIMEOUT)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan socket timeout value: " + value, e);
                    }
                })
                .orElse(60000);
    }

    /**
     * Returns the maximum number of connection retries.
     *
     * @return the maximum retries, defaults to 3 if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int maxRetries() {
        return ConfigStore.getInstance()
                .get(MAX_RETRIES)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan max retries value: " + value, e);
                    }
                })
                .orElse(3);
    }

    /**
     * Returns the maximum number of active connections per server.
     *
     * @return the maximum active connections per server, defaults to 20 if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int poolMaxActive() {
        return ConfigStore.getInstance()
                .get(POOL_MAX_ACTIVE)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan pool max-active value: " + value, e);
                    }
                })
                .orElse(20);
    }

    /**
     * Returns the minimum number of idle connections per server.
     *
     * @return the minimum idle connections per server, defaults to 1 if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int poolMinIdle() {
        return ConfigStore.getInstance()
                .get(POOL_MIN_IDLE)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan pool min-idle value: " + value, e);
                    }
                })
                .orElse(1);
    }

    /**
     * Returns the maximum time to wait for a connection in milliseconds.
     *
     * @return the maximum wait time in milliseconds, defaults to 3000ms if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int poolMaxWait() {
        return ConfigStore.getInstance()
                .get(POOL_MAX_WAIT)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Infinispan pool max-wait value: " + value, e);
                    }
                })
                .orElse(3000);
    }

    /**
     * Resolves a configuration property name to its corresponding {@link ConfigModule}.
     *
     * <p>This method is used during configuration loading to map property names from
     * configuration files to their respective configuration modules.
     *
     * @param name the configuration property name to resolve
     * @return the corresponding ConfigModule
     * @throws IllegalArgumentException if the property name is not recognized
     */
    private ConfigModule resolve(String name) {
        if (SERVER_LIST.name().equals(name)) {
            return SERVER_LIST;
        }
        if (CACHE_NAME.name().equals(name)) {
            return CACHE_NAME;
        }
        if (USERNAME.name().equals(name)) {
            return USERNAME;
        }
        if (PASSWORD.name().equals(name)) {
            return PASSWORD;
        }
        if (REALM.name().equals(name)) {
            return REALM;
        }
        if (SASL_MECHANISM.name().equals(name)) {
            return SASL_MECHANISM;
        }
        if (CONNECTION_TIMEOUT.name().equals(name)) {
            return CONNECTION_TIMEOUT;
        }
        if (SOCKET_TIMEOUT.name().equals(name)) {
            return SOCKET_TIMEOUT;
        }
        if (MAX_RETRIES.name().equals(name)) {
            return MAX_RETRIES;
        }
        if (POOL_MAX_ACTIVE.name().equals(name)) {
            return POOL_MAX_ACTIVE;
        }
        if (POOL_MIN_IDLE.name().equals(name)) {
            return POOL_MIN_IDLE;
        }
        if (POOL_MAX_WAIT.name().equals(name)) {
            return POOL_MAX_WAIT;
        }

        throw new IllegalArgumentException("Unknown Infinispan configuration property: " + name
                + ". Supported properties: infinispan.server-list, infinispan.cache-name, infinispan.username, "
                + "infinispan.password, infinispan.realm, infinispan.sasl-mechanism, infinispan.connection-timeout, "
                + "infinispan.socket-timeout, infinispan.max-retries, infinispan.pool.max-active, "
                + "infinispan.pool.min-idle, infinispan.pool.max-wait");
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

    /**
     * Registers a configuration property value that was loaded from a configuration file.
     *
     * <p>This method is called by the configuration loading system to dynamically
     * register properties that were found in the module's properties file or other
     * external configuration sources.
     *
     * @param name the configuration property name (e.g., "infinispan.server-list", "infinispan.cache-name")
     * @param value the configuration property value
     * @throws IllegalArgumentException if the property name is not recognized by this module
     */
    @Override
    public void register(String name, String value) {
        ConfigModule config = resolve(name);
        ConfigStore.getInstance().set(config, value);
    }
}
