package org.apache.camel.forage.memory.chat.redis;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.apache.camel.forage.core.ai.ChatMemoryBeanProvider;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Redis-based implementation of {@link ChatMemoryBeanProvider} that creates chat memory providers
 * with persistent storage using Redis as the backing store.
 *
 * <p>This factory creates {@link ChatMemoryProvider} instances that use Redis for storing
 * conversation history, enabling chat memory to persist across application restarts and
 * be shared across multiple application instances.
 *
 * <p><strong>Key Features:</strong>
 * <ul>
 *   <li>Persistent chat memory storage using Redis</li>
 *   <li>Configurable message window size for memory management</li>
 *   <li>Connection pooling for optimal Redis performance</li>
 *   <li>Automatic discovery via ServiceLoader mechanism</li>
 *   <li>Thread-safe memory provider creation</li>
 * </ul>
 *
 * <p><strong>Configuration:</strong>
 * The factory uses {@link RedisConfig} to obtain Redis connection parameters.
 * Configuration can be provided through environment variables, system properties,
 * or configuration files. See {@link RedisConfig} for detailed configuration options.
 *
 * <p><strong>Thread Safety:</strong>
 * This factory is thread-safe and can be safely used in concurrent environments.
 * Each call to {@link #create()} returns a provider that can handle multiple
 * concurrent memory operations.
 *
 * @see ChatMemoryBeanProvider
 * @see RedisConfig
 * @see PersistentRedisStore
 * @since 1.0
 */
@ForageBean(
        value = "redis",
        components = {"camel-langchain4j-agent"},
        description = "Redis-based chat memory factory")
public class RedisMemoryBeanProvider implements ChatMemoryBeanProvider {
    private static final Logger LOG = LoggerFactory.getLogger(RedisMemoryBeanProvider.class);
    private static final int DEFAULT_MAX_MESSAGES = 100;

    private static final RedisConfig CONFIG = new RedisConfig();
    private static final JedisPool JEDIS_POOL;
    private static final PersistentRedisStore REDIS_STORE;

    static {
        LOG.info(
                "Initializing Redis chat memory provider with host: {}, port: {}, database: {}",
                CONFIG.host(),
                CONFIG.port(),
                CONFIG.database());

        try {
            // Initialize Redis connection pool with configuration from RedisConfig
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(CONFIG.poolMaxTotal());
            poolConfig.setMaxIdle(CONFIG.poolMaxIdle());
            poolConfig.setMinIdle(CONFIG.poolMinIdle());
            poolConfig.setTestOnBorrow(CONFIG.poolTestOnBorrow());
            poolConfig.setTestOnReturn(CONFIG.poolTestOnReturn());
            poolConfig.setTestWhileIdle(CONFIG.poolTestWhileIdle());
            poolConfig.setMaxWaitMillis(CONFIG.poolMaxWaitMillis());

            LOG.debug(
                    "Redis pool configuration: maxTotal={}, maxIdle={}, minIdle={}, testOnBorrow={}, testOnReturn={}, testWhileIdle={}, maxWaitMillis={}",
                    poolConfig.getMaxTotal(),
                    poolConfig.getMaxIdle(),
                    poolConfig.getMinIdle(),
                    poolConfig.getTestOnBorrow(),
                    poolConfig.getTestOnReturn(),
                    poolConfig.getTestWhileIdle(),
                    poolConfig.getMaxWaitMillis());

            JEDIS_POOL = new JedisPool(
                    poolConfig, CONFIG.host(), CONFIG.port(), CONFIG.timeout(), CONFIG.password(), CONFIG.database());

            // Test the connection
            try (var jedis = JEDIS_POOL.getResource()) {
                jedis.ping();
                LOG.info(
                        "Successfully connected to Redis at {}:{}/{} with pool configuration",
                        CONFIG.host(),
                        CONFIG.port(),
                        CONFIG.database());
            }

            REDIS_STORE = new PersistentRedisStore(JEDIS_POOL);

        } catch (JedisException e) {
            LOG.error("Failed to initialize Redis connection pool for chat memory", e);
            throw new RuntimeException("Failed to connect to Redis for chat memory storage", e);
        }
    }

    /**
     * Creates a new Redis memory factory.
     *
     * <p>The factory uses the statically initialized Redis connection pool that was
     * configured during class loading using the {@link RedisConfig} settings.
     */
    public RedisMemoryBeanProvider() {
        // Redis pool and store are initialized statically
    }

    /**
     * Creates a new chat memory provider that uses Redis for persistent storage.
     *
     * <p>This method returns a {@link ChatMemoryProvider} that creates message window-based
     * chat memory instances backed by Redis storage. Each memory instance can store up to
     * the configured maximum number of messages per conversation.
     *
     * <p>The returned provider is thread-safe and can be used to create multiple chat
     * memory instances for different conversations. All instances will share the same
     * Redis connection pool for optimal performance.
     *
     * <p><strong>Memory Lifecycle:</strong>
     * The chat memories created by the returned provider will automatically persist
     * messages to Redis and retrieve them on subsequent access. The message window
     * will automatically manage the conversation size by removing oldest messages
     * when the maximum is exceeded.
     *
     * @return a new chat memory provider backed by Redis storage, never {@code null}
     * @throws RuntimeException if Redis connection cannot be established or configured
     */
    @Override
    public ChatMemoryProvider create() {
        return memoryId -> {
            LOG.debug("Creating message window chat memory for ID: {}", memoryId);
            return MessageWindowChatMemory.builder()
                    .id(memoryId)
                    .maxMessages(DEFAULT_MAX_MESSAGES)
                    .chatMemoryStore(REDIS_STORE)
                    .build();
        };
    }

    @Override
    public ChatMemoryProvider create(String id) {
        throw new UnsupportedOperationException("Named chat memory stores are not yet supported for Redis");
    }

    /**
     * Closes the Redis connection pool and releases all associated resources.
     *
     * <p>This method should be called during application shutdown to ensure proper
     * cleanup of Redis connections. After calling this method, the factory should
     * not be used to create new memory providers.
     *
     * <p><strong>Note:</strong> This method is not automatically called and must be
     * explicitly invoked by the application or container during shutdown. Since the
     * Redis pool is static, this affects all instances of this factory class.
     */
    public static void close() {
        if (JEDIS_POOL != null && !JEDIS_POOL.isClosed()) {
            LOG.info("Closing Redis connection pool for chat memory");
            JEDIS_POOL.close();
        }
    }
}
