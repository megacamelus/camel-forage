package org.apache.camel.forage.memory.chat.tck;

import org.apache.camel.forage.core.ai.ChatMemoryFactory;
import org.apache.camel.forage.memory.chat.infinispan.InfinispanMemoryFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Test for InfinispanMemoryFactory using the ChatMemoryFactoryTCK with testcontainers.
 *
 * <p>This test validates the InfinispanMemoryFactory implementation from the forage-memory-infinispan
 * module against the comprehensive test suite provided by the TCK. It uses testcontainers
 * to start a real Infinispan instance for testing.
 *
 * <p>The test ensures that the Infinispan-based chat memory implementation correctly handles:
 * <ul>
 *   <li>Message persistence across Infinispan operations</li>
 *   <li>Memory isolation between different conversation IDs</li>
 *   <li>Connection management and error handling</li>
 *   <li>All standard chat memory operations</li>
 *   <li>Distributed caching capabilities</li>
 * </ul>
 *
 * @since 1.0
 */
@Testcontainers(disabledWithoutDocker = true)
class InfinispanMemoryTCKTest extends ChatMemoryFactoryTCK {

    private static final int INFINISPAN_PORT = 11222;

    @Container
    static GenericContainer<?> infinispan = new GenericContainer<>(DockerImageName.parse("infinispan/server:15.1"))
            .withExposedPorts(INFINISPAN_PORT)
            .withEnv("USER", "admin")
            .withEnv("PASS", "password");

    @BeforeAll
    static void setUpInfinispan() {
        // Configure Infinispan connection for the test
        String infinispanHost = infinispan.getHost();
        Integer infinispanPort = infinispan.getMappedPort(INFINISPAN_PORT);

        // Set system properties for Infinispan configuration
        System.setProperty("infinispan.server-list", infinispanHost + ":" + infinispanPort);
        System.setProperty("infinispan.cache-name", "chat-memory");
        System.setProperty("infinispan.username", "admin");
        System.setProperty("infinispan.password", "password");
        System.setProperty("infinispan.realm", "default");
        System.setProperty("infinispan.sasl-mechanism", "DIGEST-MD5");
        System.setProperty("infinispan.connection-timeout", "60000");
        System.setProperty("infinispan.socket-timeout", "60000");
        System.setProperty("infinispan.max-retries", "3");
    }

    @AfterAll
    static void tearDownInfinispan() {
        // Clean up Infinispan configuration
        System.clearProperty("infinispan.server-list");
        System.clearProperty("infinispan.cache-name");
        System.clearProperty("infinispan.username");
        System.clearProperty("infinispan.password");
        System.clearProperty("infinispan.realm");
        System.clearProperty("infinispan.sasl-mechanism");
        System.clearProperty("infinispan.connection-timeout");
        System.clearProperty("infinispan.socket-timeout");
        System.clearProperty("infinispan.max-retries");

        // Close Infinispan cache manager
        InfinispanMemoryFactory.close();
    }

    @Override
    protected ChatMemoryFactory createChatMemoryFactory() {
        return new InfinispanMemoryFactory();
    }

    @Test
    void demonstratesInfinispanTCKUsage() {
        // This test exists to demonstrate that the TCK is working with Infinispan
        // All actual tests are inherited from ChatMemoryFactoryTCK
    }

    @Test
    void infinispanContainerIsRunning() {
        // Verify that the Infinispan container is properly started
        assert infinispan.isRunning();
        assert infinispan.getMappedPort(INFINISPAN_PORT) != null;
    }
}
