package org.apache.camel.forage.memory.chat.redis;

import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;

/**
 * Configuration class for Redis-based chat memory storage in the Camel Forage framework.
 *
 * <p>This configuration manages connection parameters for Redis instances used to store
 * persistent chat conversation history. It follows the standard Camel Forage configuration
 * pattern with support for multiple configuration sources and environment-specific overrides.
 *
 * <p><strong>Configuration Properties:</strong>
 * <ul>
 *   <li><code>redis.host</code> - Redis server hostname (default: localhost) - required</li>
 *   <li><code>redis.port</code> - Redis server port (default: 6379) - required</li>
 *   <li><code>redis.password</code> - Redis authentication password (optional)</li>
 *   <li><code>redis.database</code> - Redis database number (default: 0)</li>
 *   <li><code>redis.timeout</code> - Connection timeout in milliseconds (default: 2000)</li>
 *   <li><code>redis.pool.max-total</code> - Maximum number of connections in the pool (default: 10)</li>
 *   <li><code>redis.pool.max-idle</code> - Maximum number of idle connections (default: 5)</li>
 *   <li><code>redis.pool.min-idle</code> - Minimum number of idle connections (default: 1)</li>
 *   <li><code>redis.pool.test-on-borrow</code> - Test connections when borrowing from pool (default: true)</li>
 *   <li><code>redis.pool.test-on-return</code> - Test connections when returning to pool (default: true)</li>
 *   <li><code>redis.pool.test-while-idle</code> - Test idle connections periodically (default: true)</li>
 *   <li><code>redis.pool.max-wait-millis</code> - Maximum time to wait for a connection (default: 2000)</li>
 * </ul>
 *
 * <p><strong>Configuration Sources (in order of precedence):</strong>
 * <ol>
 *   <li>Environment variables: {@code REDIS_HOST}, {@code REDIS_PORT}, {@code REDIS_PASSWORD}, {@code REDIS_POOL_MAX_TOTAL}, etc.</li>
 *   <li>System properties: {@code redis.host}, {@code redis.port}, {@code redis.password}, {@code redis.pool.max-total}, etc.</li>
 *   <li>Configuration file: {@code forage-memory-redis.properties}</li>
 *   <li>Default values where applicable</li>
 * </ol>
 *
 * <p><strong>Example Environment Configuration:</strong>
 * <pre>{@code
 * export REDIS_HOST=redis.example.com
 * export REDIS_PORT=6379
 * export REDIS_PASSWORD=secret123
 * export REDIS_DATABASE=1
 * export REDIS_POOL_MAX_TOTAL=20
 * export REDIS_POOL_MAX_IDLE=10
 * }</pre>
 *
 * <p><strong>Example System Properties:</strong>
 * <pre>{@code
 * -Dredis.host=redis.example.com
 * -Dredis.port=6379
 * -Dredis.password=secret123
 * -Dredis.database=1
 * -Dredis.pool.max-total=20
 * -Dredis.pool.max-idle=10
 * }</pre>
 *
 * <p><strong>Example Properties File (forage-memory-redis.properties):</strong>
 * <pre>
 * redis.host=redis.example.com
 * redis.port=6379
 * redis.password=secret123
 * redis.database=1
 * redis.timeout=5000
 * redis.pool.max-total=20
 * redis.pool.max-idle=10
 * redis.pool.min-idle=2
 * redis.pool.test-on-borrow=true
 * redis.pool.max-wait-millis=3000
 * </pre>
 *
 * @see Config
 * @see ConfigStore
 * @see PersistentRedisStore
 * @since 1.0
 */
public class RedisConfig implements Config {

    private static final ConfigModule HOST = ConfigModule.of(RedisConfig.class, "redis.host");
    private static final ConfigModule PORT = ConfigModule.of(RedisConfig.class, "redis.port");
    private static final ConfigModule PASSWORD = ConfigModule.of(RedisConfig.class, "redis.password");
    private static final ConfigModule DATABASE = ConfigModule.of(RedisConfig.class, "redis.database");
    private static final ConfigModule TIMEOUT = ConfigModule.of(RedisConfig.class, "redis.timeout");

    // Pool configuration
    private static final ConfigModule POOL_MAX_TOTAL = ConfigModule.of(RedisConfig.class, "redis.pool.max-total");
    private static final ConfigModule POOL_MAX_IDLE = ConfigModule.of(RedisConfig.class, "redis.pool.max-idle");
    private static final ConfigModule POOL_MIN_IDLE = ConfigModule.of(RedisConfig.class, "redis.pool.min-idle");
    private static final ConfigModule POOL_TEST_ON_BORROW =
            ConfigModule.of(RedisConfig.class, "redis.pool.test-on-borrow");
    private static final ConfigModule POOL_TEST_ON_RETURN =
            ConfigModule.of(RedisConfig.class, "redis.pool.test-on-return");
    private static final ConfigModule POOL_TEST_WHILE_IDLE =
            ConfigModule.of(RedisConfig.class, "redis.pool.test-while-idle");
    private static final ConfigModule POOL_MAX_WAIT_MILLIS =
            ConfigModule.of(RedisConfig.class, "redis.pool.max-wait-millis");

    /**
     * Creates a new Redis configuration instance and registers configuration entries
     * with the central {@link ConfigStore}.
     *
     * <p>This constructor automatically registers all Redis configuration parameters
     * and their corresponding environment variable mappings. It also sets up the
     * configuration loader to process property files if they exist.
     */
    public RedisConfig() {
        ConfigStore.getInstance().add(HOST, ConfigEntry.fromModule(HOST, "REDIS_HOST"));
        ConfigStore.getInstance().add(PORT, ConfigEntry.fromModule(PORT, "REDIS_PORT"));
        ConfigStore.getInstance().add(PASSWORD, ConfigEntry.fromModule(PASSWORD, "REDIS_PASSWORD"));
        ConfigStore.getInstance().add(DATABASE, ConfigEntry.fromModule(DATABASE, "REDIS_DATABASE"));
        ConfigStore.getInstance().add(TIMEOUT, ConfigEntry.fromModule(TIMEOUT, "REDIS_TIMEOUT"));

        // Pool configuration
        ConfigStore.getInstance().add(POOL_MAX_TOTAL, ConfigEntry.fromModule(POOL_MAX_TOTAL, "REDIS_POOL_MAX_TOTAL"));
        ConfigStore.getInstance().add(POOL_MAX_IDLE, ConfigEntry.fromModule(POOL_MAX_IDLE, "REDIS_POOL_MAX_IDLE"));
        ConfigStore.getInstance().add(POOL_MIN_IDLE, ConfigEntry.fromModule(POOL_MIN_IDLE, "REDIS_POOL_MIN_IDLE"));
        ConfigStore.getInstance()
                .add(POOL_TEST_ON_BORROW, ConfigEntry.fromModule(POOL_TEST_ON_BORROW, "REDIS_POOL_TEST_ON_BORROW"));
        ConfigStore.getInstance()
                .add(POOL_TEST_ON_RETURN, ConfigEntry.fromModule(POOL_TEST_ON_RETURN, "REDIS_POOL_TEST_ON_RETURN"));
        ConfigStore.getInstance()
                .add(POOL_TEST_WHILE_IDLE, ConfigEntry.fromModule(POOL_TEST_WHILE_IDLE, "REDIS_POOL_TEST_WHILE_IDLE"));
        ConfigStore.getInstance()
                .add(POOL_MAX_WAIT_MILLIS, ConfigEntry.fromModule(POOL_MAX_WAIT_MILLIS, "REDIS_POOL_MAX_WAIT_MILLIS"));

        ConfigStore.getInstance().add(RedisConfig.class, this, this::register);
    }

    /**
     * Returns the Redis server hostname.
     *
     * @return the Redis server hostname, defaults to "localhost" if not configured
     */
    public String host() {
        return ConfigStore.getInstance().get(HOST).orElse("localhost");
    }

    /**
     * Returns the Redis server port number.
     *
     * @return the Redis server port, defaults to 6379 (standard Redis port) if not configured
     * @throws NumberFormatException if the configured port value is not a valid integer
     */
    public int port() {
        return ConfigStore.getInstance()
                .get(PORT)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Redis port number: " + value, e);
                    }
                })
                .orElse(6379);
    }

    /**
     * Returns the Redis authentication password.
     *
     * @return the Redis password, or {@code null} if no authentication is required
     */
    public String password() {
        return ConfigStore.getInstance().get(PASSWORD).orElse(null);
    }

    /**
     * Returns the Redis database number to connect to.
     *
     * @return the Redis database number, defaults to 0 if not configured
     * @throws NumberFormatException if the configured database value is not a valid integer
     */
    public int database() {
        return ConfigStore.getInstance()
                .get(DATABASE)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Redis database number: " + value, e);
                    }
                })
                .orElse(0);
    }

    /**
     * Returns the Redis connection timeout in milliseconds.
     *
     * @return the connection timeout in milliseconds, defaults to 2000ms if not configured
     * @throws NumberFormatException if the configured timeout value is not a valid integer
     */
    public int timeout() {
        return ConfigStore.getInstance()
                .get(TIMEOUT)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Redis timeout value: " + value, e);
                    }
                })
                .orElse(2000);
    }

    /**
     * Returns the maximum number of connections in the Redis connection pool.
     *
     * @return the maximum total connections, defaults to 10 if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int poolMaxTotal() {
        return ConfigStore.getInstance()
                .get(POOL_MAX_TOTAL)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Redis pool max-total value: " + value, e);
                    }
                })
                .orElse(10);
    }

    /**
     * Returns the maximum number of idle connections in the Redis connection pool.
     *
     * @return the maximum idle connections, defaults to 5 if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int poolMaxIdle() {
        return ConfigStore.getInstance()
                .get(POOL_MAX_IDLE)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Redis pool max-idle value: " + value, e);
                    }
                })
                .orElse(5);
    }

    /**
     * Returns the minimum number of idle connections in the Redis connection pool.
     *
     * @return the minimum idle connections, defaults to 1 if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int poolMinIdle() {
        return ConfigStore.getInstance()
                .get(POOL_MIN_IDLE)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Redis pool min-idle value: " + value, e);
                    }
                })
                .orElse(1);
    }

    /**
     * Returns whether to test connections when borrowing from the pool.
     *
     * @return true if connections should be tested on borrow, defaults to true if not configured
     * @throws IllegalArgumentException if the configured value is not a valid boolean
     */
    public boolean poolTestOnBorrow() {
        return ConfigStore.getInstance()
                .get(POOL_TEST_ON_BORROW)
                .map(value -> {
                    if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                        return Boolean.parseBoolean(value);
                    }
                    throw new IllegalArgumentException(
                            "Invalid Redis pool test-on-borrow value: " + value + " (must be true or false)");
                })
                .orElse(true);
    }

    /**
     * Returns whether to test connections when returning to the pool.
     *
     * @return true if connections should be tested on return, defaults to true if not configured
     * @throws IllegalArgumentException if the configured value is not a valid boolean
     */
    public boolean poolTestOnReturn() {
        return ConfigStore.getInstance()
                .get(POOL_TEST_ON_RETURN)
                .map(value -> {
                    if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                        return Boolean.parseBoolean(value);
                    }
                    throw new IllegalArgumentException(
                            "Invalid Redis pool test-on-return value: " + value + " (must be true or false)");
                })
                .orElse(true);
    }

    /**
     * Returns whether to test idle connections periodically.
     *
     * @return true if idle connections should be tested, defaults to true if not configured
     * @throws IllegalArgumentException if the configured value is not a valid boolean
     */
    public boolean poolTestWhileIdle() {
        return ConfigStore.getInstance()
                .get(POOL_TEST_WHILE_IDLE)
                .map(value -> {
                    if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                        return Boolean.parseBoolean(value);
                    }
                    throw new IllegalArgumentException(
                            "Invalid Redis pool test-while-idle value: " + value + " (must be true or false)");
                })
                .orElse(true);
    }

    /**
     * Returns the maximum time to wait for a connection from the pool in milliseconds.
     *
     * @return the maximum wait time in milliseconds, defaults to 2000ms if not configured
     * @throws IllegalArgumentException if the configured value is not a valid integer
     */
    public int poolMaxWaitMillis() {
        return ConfigStore.getInstance()
                .get(POOL_MAX_WAIT_MILLIS)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid Redis pool max-wait-millis value: " + value, e);
                    }
                })
                .orElse(2000);
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
        if (HOST.name().equals(name)) {
            return HOST;
        }
        if (PORT.name().equals(name)) {
            return PORT;
        }
        if (PASSWORD.name().equals(name)) {
            return PASSWORD;
        }
        if (DATABASE.name().equals(name)) {
            return DATABASE;
        }
        if (TIMEOUT.name().equals(name)) {
            return TIMEOUT;
        }
        if (POOL_MAX_TOTAL.name().equals(name)) {
            return POOL_MAX_TOTAL;
        }
        if (POOL_MAX_IDLE.name().equals(name)) {
            return POOL_MAX_IDLE;
        }
        if (POOL_MIN_IDLE.name().equals(name)) {
            return POOL_MIN_IDLE;
        }
        if (POOL_TEST_ON_BORROW.name().equals(name)) {
            return POOL_TEST_ON_BORROW;
        }
        if (POOL_TEST_ON_RETURN.name().equals(name)) {
            return POOL_TEST_ON_RETURN;
        }
        if (POOL_TEST_WHILE_IDLE.name().equals(name)) {
            return POOL_TEST_WHILE_IDLE;
        }
        if (POOL_MAX_WAIT_MILLIS.name().equals(name)) {
            return POOL_MAX_WAIT_MILLIS;
        }

        throw new IllegalArgumentException("Unknown Redis configuration property: " + name
                + ". Supported properties: redis.host, redis.port, redis.password, redis.database, redis.timeout, "
                + "redis.pool.max-total, redis.pool.max-idle, redis.pool.min-idle, redis.pool.test-on-borrow, "
                + "redis.pool.test-on-return, redis.pool.test-while-idle, redis.pool.max-wait-millis");
    }

    /**
     * Returns the unique name identifier for this Redis memory configuration module.
     *
     * <p>This name is used to identify the module and corresponds to the expected
     * properties file name ({@code forage-memory-redis.properties}).
     *
     * @return the module name "forage-memory-redis"
     */
    @Override
    public String name() {
        return "forage-memory-redis";
    }

    /**
     * Registers a configuration property value that was loaded from a configuration file.
     *
     * <p>This method is called by the configuration loading system to dynamically
     * register properties that were found in the module's properties file or other
     * external configuration sources.
     *
     * @param name the configuration property name (e.g., "redis.host", "redis.port")
     * @param value the configuration property value
     * @throws IllegalArgumentException if the property name is not recognized by this module
     */
    @Override
    public void register(String name, String value) {
        ConfigModule config = resolve(name);
        ConfigStore.getInstance().set(config, value);
    }
}
