package org.apache.camel.forage.memory.chat.tck;

import org.apache.camel.forage.core.ai.ChatMemoryFactory;
import org.apache.camel.forage.memory.chat.redis.RedisMemoryFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Test for RedisMemoryFactory using the ChatMemoryFactoryTCK with testcontainers.
 *
 * <p>This test validates the RedisMemoryFactory implementation from the forage-memory-redis
 * module against the comprehensive test suite provided by the TCK. It uses testcontainers
 * to start a real Redis instance for testing.
 *
 * <p>The test ensures that the Redis-based chat memory implementation correctly handles:
 * <ul>
 *   <li>Message persistence across Redis operations</li>
 *   <li>Memory isolation between different conversation IDs</li>
 *   <li>Connection management and error handling</li>
 *   <li>All standard chat memory operations</li>
 * </ul>
 *
 * @since 1.0
 */
@Testcontainers(disabledWithoutDocker = true)
class RedisMemoryTCKTest extends ChatMemoryFactoryTCK {

    private static final int REDIS_PORT = 6379;

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(REDIS_PORT)
            .withCommand("redis-server", "--appendonly", "yes");

    @BeforeAll
    static void setUpRedis() {
        // Configure Redis connection for the test
        String redisHost = redis.getHost();
        Integer redisPort = redis.getMappedPort(REDIS_PORT);

        // Set system properties for Redis configuration
        System.setProperty("redis.host", redisHost);
        System.setProperty("redis.port", redisPort.toString());
        System.setProperty("redis.database", "0");
        System.setProperty("redis.timeout", "2000");
        System.setProperty("redis.pool.max.total", "8");
        System.setProperty("redis.pool.max.idle", "8");
        System.setProperty("redis.pool.min.idle", "0");
    }

    @AfterAll
    static void tearDownRedis() {
        // Clean up Redis configuration
        System.clearProperty("redis.host");
        System.clearProperty("redis.port");
        System.clearProperty("redis.database");
        System.clearProperty("redis.timeout");
        System.clearProperty("redis.pool.max.total");
        System.clearProperty("redis.pool.max.idle");
        System.clearProperty("redis.pool.min.idle");

        // Close Redis connection pool
        RedisMemoryFactory.close();
    }

    @Override
    protected ChatMemoryFactory createChatMemoryFactory() {
        return new RedisMemoryFactory();
    }

    @Test
    void demonstratesRedisTCKUsage() {
        // This test exists to demonstrate that the TCK is working with Redis
        // All actual tests are inherited from ChatMemoryFactoryTCK
    }

    @Test
    void redisContainerIsRunning() {
        // Verify that the Redis container is properly started
        assert redis.isRunning();
        assert redis.getMappedPort(REDIS_PORT) != null;
    }
}
