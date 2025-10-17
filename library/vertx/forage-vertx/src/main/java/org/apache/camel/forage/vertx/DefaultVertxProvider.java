package org.apache.camel.forage.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.core.vertx.VertxProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating Vert.x instances with configurable parameters.
 *
 * <p>This provider creates instances of {@link Vertx} using configuration
 * values managed by {@link VertxConfig}. The configuration supports environment
 * variables, system properties, and configuration files for flexible deployment.
 *
 * <p><strong>Configuration:</strong>
 * <ul>
 *   <li>Worker Pool Size: Configured via VERTX_WORKER_POOL_SIZE environment variable (no default)</li>
 *   <li>Event Loop Pool Size: Configured via VERTX_EVENT_LOOP_POOL_SIZE environment variable (no default)</li>
 *   <li>Clustered: Configured via VERTX_CLUSTERED environment variable (default: false)</li>
 * </ul>
 *
 * <p><strong>Usage:</strong>
 * <pre>{@code
 * // Configuration is automatic through environment variables or defaults
 * DefaultVertxProvider provider = new DefaultVertxProvider();
 * Vertx vertx = provider.create();
 * }</pre>
 *
 * @see VertxConfig
 * @see VertxProvider
 * @since 1.0
 */
@ForageBean(
        value = "default-vertx",
        components = {"camel-vertx"},
        description = "Default Vert.x instance provider")
public class DefaultVertxProvider implements VertxProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultVertxProvider.class);

    /**
     * Creates a new Vert.x instance with the configured parameters.
     *
     * <p>This method creates a {@link Vertx} instance using the configuration
     * settings. If clustered mode is enabled, it will attempt to create a clustered
     * Vert.x instance. Otherwise, it creates a standard non-clustered instance.
     *
     * @param id optional identifier for prefixed configuration
     * @return a new configured Vert.x instance
     */
    @Override
    public Vertx create(String id) {
        final VertxConfig config = new VertxConfig(id);

        Integer workerPoolSize = config.workerPoolSize();
        Integer eventLoopPoolSize = config.eventLoopPoolSize();
        Boolean clustered = config.clustered();

        LOG.trace(
                "Creating Vert.x instance with configuration: workerPoolSize={}, eventLoopPoolSize={}, clustered={}",
                workerPoolSize,
                eventLoopPoolSize,
                clustered);

        VertxOptions options = new VertxOptions();

        if (workerPoolSize != null) {
            options.setWorkerPoolSize(workerPoolSize);
        }

        if (eventLoopPoolSize != null) {
            options.setEventLoopPoolSize(eventLoopPoolSize);
        }

        if (clustered != null && clustered) {
            LOG.warn("Clustered mode is enabled but not yet implemented. Creating non-clustered instance.");
        }

        return Vertx.vertx(options);
    }
}
