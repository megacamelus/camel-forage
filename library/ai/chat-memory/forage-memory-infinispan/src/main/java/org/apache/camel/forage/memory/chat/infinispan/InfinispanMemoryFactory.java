package org.apache.camel.forage.memory.chat.infinispan;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.apache.camel.forage.core.ai.ChatMemoryFactory;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Infinispan-based implementation of {@link ChatMemoryFactory} that creates chat memory providers
 * with persistent storage using Infinispan as the backing store.
 *
 * <p>This factory creates {@link ChatMemoryProvider} instances that use Infinispan for storing
 * conversation history, enabling chat memory to persist across application restarts and
 * be shared across multiple application instances in a distributed environment.
 *
 * <p><strong>Key Features:</strong>
 * <ul>
 *   <li>Persistent chat memory storage using Infinispan</li>
 *   <li>Configurable message window size for memory management</li>
 *   <li>Distributed caching for scalability and high availability</li>
 *   <li>Automatic discovery via ServiceLoader mechanism</li>
 *   <li>Thread-safe memory provider creation</li>
 * </ul>
 *
 * <p><strong>Configuration:</strong>
 * The factory uses {@link InfinispanConfig} to obtain Infinispan connection parameters.
 * Configuration can be provided through environment variables, system properties,
 * or configuration files. See {@link InfinispanConfig} for detailed configuration options.
 *
 * <p><strong>Thread Safety:</strong>
 * This factory is thread-safe and can be safely used in concurrent environments.
 * Each call to {@link #newChatMemory()} returns a provider that can handle multiple
 * concurrent memory operations.
 *
 * @see ChatMemoryFactory
 * @see InfinispanConfig
 * @see PersistentInfinispanStore
 * @since 1.0
 */
public class InfinispanMemoryFactory implements ChatMemoryFactory {
    private static final Logger LOG = LoggerFactory.getLogger(InfinispanMemoryFactory.class);
    private static final int DEFAULT_MAX_MESSAGES = 10;

    private static final InfinispanConfig CONFIG = new InfinispanConfig();
    private static final RemoteCacheManager CACHE_MANAGER;
    private static RemoteCache<String, String> CACHE;
    private static final PersistentInfinispanStore INFINISPAN_STORE;

    static {
        LOG.info(
                "Initializing Infinispan chat memory provider with servers: {}, cache: {}",
                CONFIG.serverList(),
                CONFIG.cacheName());

        try {
            // Initialize Infinispan cache manager with configuration from InfinispanConfig
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.addServers(CONFIG.serverList());
            builder.connectionTimeout(CONFIG.connectionTimeout());
            builder.socketTimeout(CONFIG.socketTimeout());
            builder.maxRetries(CONFIG.maxRetries());

            // Configure connection pool settings
            LOG.debug(
                    "Configuring Infinispan connection pool: maxActive={}, maxIdle={}, maxTotal={}, minIdle={}, maxWait={}ms",
                    CONFIG.poolMaxActive(),
                    CONFIG.poolMinIdle(),
                    CONFIG.poolMaxWait());
            builder.connectionPool()
                    .maxActive(CONFIG.poolMaxActive())
                    .minIdle(CONFIG.poolMinIdle())
                    .maxWait(CONFIG.poolMaxWait());

            // Configure authentication if credentials are provided
            if (CONFIG.username() != null && CONFIG.password() != null) {
                LOG.debug("Configuring SASL authentication with mechanism: {}", CONFIG.saslMechanism());
                builder.security()
                        .authentication()
                        .enable()
                        .username(CONFIG.username())
                        .password(CONFIG.password())
                        .realm(CONFIG.realm())
                        .saslMechanism(CONFIG.saslMechanism());
            }

            CACHE_MANAGER = new RemoteCacheManager(builder.build());

            // Start the cache manager
            CACHE_MANAGER.start();

            // Get or create the cache for chat messages
            String cacheName = CONFIG.cacheName();
            try {
                // Try to get the named cache first
                CACHE = CACHE_MANAGER.getCache(cacheName);
                if (CACHE == null) {
                    LOG.info("Cache '{}' not found, creating it with default template", cacheName);
                    // Create cache using the default template
                    CACHE_MANAGER.administration().createCache(cacheName, (String) null);
                    CACHE = CACHE_MANAGER.getCache(cacheName);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        String.format("Failed to get or create named cache %s", cacheName), e);
            }

            // Test the connection by performing a simple operation
            CACHE.size(); // This will throw an exception if connection fails
            LOG.info(
                    "Successfully connected to Infinispan cluster at {} with cache '{}'",
                    CONFIG.serverList(),
                    CONFIG.cacheName());

            INFINISPAN_STORE = new PersistentInfinispanStore(CACHE);

        } catch (Exception e) {
            LOG.error("Failed to initialize Infinispan connection for chat memory", e);
            throw new RuntimeException("Failed to connect to Infinispan for chat memory storage", e);
        }
    }

    /**
     * Creates a new Infinispan memory factory.
     *
     * <p>The factory uses the statically initialized Infinispan cache manager that was
     * configured during class loading using the {@link InfinispanConfig} settings.
     */
    public InfinispanMemoryFactory() {
        // Cache manager and store are initialized statically
    }

    /**
     * Creates a new chat memory provider that uses Infinispan for persistent storage.
     *
     * <p>This method returns a {@link ChatMemoryProvider} that creates message window-based
     * chat memory instances backed by Infinispan storage. Each memory instance can store up to
     * the configured maximum number of messages per conversation.
     *
     * <p>The returned provider is thread-safe and can be used to create multiple chat
     * memory instances for different conversations. All instances will share the same
     * Infinispan cache for optimal performance and data consistency.
     *
     * <p><strong>Memory Lifecycle:</strong>
     * The chat memories created by the returned provider will automatically persist
     * messages to Infinispan and retrieve them on subsequent access. The message window
     * will automatically manage the conversation size by removing oldest messages
     * when the maximum is exceeded.
     *
     * @return a new chat memory provider backed by Infinispan storage, never {@code null}
     * @throws RuntimeException if Infinispan connection cannot be established or configured
     */
    @Override
    public ChatMemoryProvider newChatMemory() {
        return memoryId -> {
            LOG.debug("Creating message window chat memory for ID: {}", memoryId);
            return MessageWindowChatMemory.builder()
                    .id(memoryId)
                    .maxMessages(DEFAULT_MAX_MESSAGES)
                    .chatMemoryStore(INFINISPAN_STORE)
                    .build();
        };
    }

    /**
     * Closes the Infinispan cache manager and releases all associated resources.
     *
     * <p>This method should be called during application shutdown to ensure proper
     * cleanup of Infinispan connections. After calling this method, the factory should
     * not be used to create new memory providers.
     *
     * <p><strong>Note:</strong> This method is not automatically called and must be
     * explicitly invoked by the application or container during shutdown. Since the
     * cache manager is static, this affects all instances of this factory class.
     */
    public static void close() {
        if (CACHE_MANAGER != null) {
            LOG.info("Closing Infinispan cache manager for chat memory");
            CACHE_MANAGER.close();
        }
    }
}
