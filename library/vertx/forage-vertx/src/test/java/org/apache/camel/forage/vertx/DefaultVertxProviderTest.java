package org.apache.camel.forage.vertx;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Vertx;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for DefaultVertxProvider using system properties.
 *
 * <p>This test demonstrates how the Camel Forage framework can create Vert.x instances
 * with configuration from system properties. The test verifies basic Vert.x instance
 * creation and configuration.
 */
public class DefaultVertxProviderTest {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultVertxProviderTest.class);

    @BeforeAll
    public static void setupVertxConfiguration() {
        LOG.info("Setting up Vert.x configuration with system properties");

        System.setProperty("forage.vertx.worker.pool.size", "20");
        System.setProperty("forage.vertx.event.loop.pool.size", "8");
        System.setProperty("forage.vertx.clustered", "false");

        LOG.info("Vert.x configuration setup complete");
    }

    @AfterAll
    public static void cleanupVertxConfiguration() {
        LOG.info("Cleaning up Vert.x system properties");

        System.clearProperty("forage.vertx.worker.pool.size");
        System.clearProperty("forage.vertx.event.loop.pool.size");
        System.clearProperty("forage.vertx.clustered");

        LOG.info("Vert.x system properties cleared");
    }

    @Test
    public void shouldCreateVertxInstance() {
        LOG.info("Testing Vert.x instance creation");

        DefaultVertxProvider provider = new DefaultVertxProvider();
        Vertx vertx = provider.create();

        assertThat(vertx).isNotNull();
        LOG.info("Successfully created Vert.x instance");

        vertx.close();
    }

    @Test
    public void shouldCreateMultipleVertxInstances() {
        LOG.info("Testing multiple Vert.x instance creation");

        DefaultVertxProvider provider = new DefaultVertxProvider();

        Vertx vertx1 = provider.create();
        Vertx vertx2 = provider.create();

        assertThat(vertx1).isNotNull();
        assertThat(vertx2).isNotNull();
        assertThat(vertx1).isNotSameAs(vertx2);

        LOG.info("Successfully created multiple Vert.x instances");

        vertx1.close();
        vertx2.close();
    }

    @Test
    public void shouldCreateVertxWithNamedConfiguration() {
        LOG.info("Testing Vert.x instance creation with named configuration");

        System.setProperty("forage.custom.vertx.worker.pool.size", "30");
        System.setProperty("forage.custom.vertx.event.loop.pool.size", "16");

        try {
            DefaultVertxProvider provider = new DefaultVertxProvider();
            Vertx vertx = provider.create("custom");

            assertThat(vertx).isNotNull();
            LOG.info("Successfully created Vert.x instance with named configuration");

            vertx.close();
        } finally {
            System.clearProperty("forage.custom.vertx.worker.pool.size");
            System.clearProperty("forage.custom.vertx.event.loop.pool.size");
        }
    }

    @Test
    public void shouldCreateVertxWithDefaultConfiguration() {
        LOG.info("Testing Vert.x instance creation with default configuration");

        System.clearProperty("forage.vertx.worker.pool.size");
        System.clearProperty("forage.vertx.event.loop.pool.size");
        System.clearProperty("forage.vertx.clustered");

        try {
            DefaultVertxProvider provider = new DefaultVertxProvider();
            Vertx vertx = provider.create();

            assertThat(vertx).isNotNull();
            LOG.info("Successfully created Vert.x instance with default configuration");

            vertx.close();
        } finally {
            System.setProperty("forage.vertx.worker.pool.size", "20");
            System.setProperty("forage.vertx.event.loop.pool.size", "8");
            System.setProperty("forage.vertx.clustered", "false");
        }
    }
}
