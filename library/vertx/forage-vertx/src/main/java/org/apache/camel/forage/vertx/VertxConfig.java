package org.apache.camel.forage.vertx;

import static org.apache.camel.forage.vertx.VertxConfigEntries.CLUSTERED;
import static org.apache.camel.forage.vertx.VertxConfigEntries.EVENT_LOOP_POOL_SIZE;
import static org.apache.camel.forage.vertx.VertxConfigEntries.WORKER_POOL_SIZE;

import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;

/**
 * Configuration class for Vert.x integration in the Camel Forage framework.
 *
 * <p>This configuration class manages the settings required to create and configure
 * Vert.x instances. It handles Vert.x options such as worker pool size, event loop
 * pool size, and clustering configuration through environment variables with appropriate
 * fallback mechanisms and default values.
 *
 * <p><strong>Configuration Parameters:</strong>
 * <ul>
 *   <li><strong>VERTX_WORKER_POOL_SIZE</strong> - The size of the worker thread pool (no default)</li>
 *   <li><strong>VERTX_EVENT_LOOP_POOL_SIZE</strong> - The size of the event loop thread pool (no default)</li>
 *   <li><strong>VERTX_CLUSTERED</strong> - Whether to create a clustered Vert.x instance (default: false)</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (VERTX_WORKER_POOL_SIZE, VERTX_EVENT_LOOP_POOL_SIZE, VERTX_CLUSTERED)</li>
 *   <li>System properties (vertx.worker.pool.size, vertx.event.loop.pool.size, vertx.clustered)</li>
 *   <li>forage-vertx.properties file in classpath</li>
 *   <li>Default values if none of the above are provided</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables (optional)
 * export VERTX_WORKER_POOL_SIZE="20"
 * export VERTX_EVENT_LOOP_POOL_SIZE="8"
 * export VERTX_CLUSTERED="true"
 *
 * // Create and use configuration
 * VertxConfig config = new VertxConfig();
 * Integer workerPoolSize = config.workerPoolSize();
 * Integer eventLoopPoolSize = config.eventLoopPoolSize();
 * Boolean clustered = config.clustered();
 * }</pre>
 *
 * <p><strong>Default Values:</strong>
 * <ul>
 *   <li>Worker Pool Size: null (uses Vert.x default)</li>
 *   <li>Event Loop Pool Size: null (uses Vert.x default)</li>
 *   <li>Clustered: false</li>
 * </ul>
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
public class VertxConfig implements Config {

    private final String prefix;

    /**
     * Constructs a new VertxConfig and registers configuration parameters with the ConfigStore.
     *
     * <p>During construction, this class:
     * <ul>
     *   <li>Registers the worker pool size configuration</li>
     *   <li>Registers the event loop pool size configuration</li>
     *   <li>Registers the clustered mode configuration</li>
     *   <li>Attempts to load additional properties from forage-vertx.properties</li>
     * </ul>
     *
     * <p>Configuration values are resolved when this constructor is called, with default values
     * used when no configuration is provided through environment variables, system properties,
     * or configuration files.
     */
    public VertxConfig() {
        this(null);
    }

    public VertxConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        VertxConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(VertxConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        VertxConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = VertxConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    /**
     * Returns the unique identifier for this Vert.x configuration module.
     *
     * <p>This name corresponds to the module artifact and is used for:
     * <ul>
     *   <li>Loading configuration files (forage-vertx.properties)</li>
     *   <li>Identifying this module in logs and error messages</li>
     *   <li>Distinguishing this configuration from other module configurations</li>
     * </ul>
     *
     * @return the module name "forage-vertx"
     */
    @Override
    public String name() {
        return "forage-vertx";
    }

    /**
     * Returns the worker pool size configuration.
     *
     * <p>The worker pool size determines the number of threads in the worker pool used
     * for blocking operations. If not configured, Vert.x will use its default value.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>VERTX_WORKER_POOL_SIZE environment variable</li>
     *   <li>vertx.worker.pool.size system property</li>
     *   <li>worker-pool-size property in forage-vertx.properties</li>
     *   <li>null (uses Vert.x default)</li>
     * </ol>
     *
     * @return the worker pool size, or null if not configured
     */
    public Integer workerPoolSize() {
        return ConfigStore.getInstance()
                .get(WORKER_POOL_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(null);
    }

    /**
     * Returns the event loop pool size configuration.
     *
     * <p>The event loop pool size determines the number of event loop threads. If not
     * configured, Vert.x will use its default value (typically 2 * number of cores).
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>VERTX_EVENT_LOOP_POOL_SIZE environment variable</li>
     *   <li>vertx.event.loop.pool.size system property</li>
     *   <li>event-loop-pool-size property in forage-vertx.properties</li>
     *   <li>null (uses Vert.x default)</li>
     * </ol>
     *
     * @return the event loop pool size, or null if not configured
     */
    public Integer eventLoopPoolSize() {
        return ConfigStore.getInstance()
                .get(EVENT_LOOP_POOL_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(null);
    }

    /**
     * Returns whether to create a clustered Vert.x instance.
     *
     * <p>When true, creates a clustered Vert.x instance that can participate in a
     * cluster with other Vert.x instances. When false or not configured, creates
     * a non-clustered instance.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>VERTX_CLUSTERED environment variable</li>
     *   <li>vertx.clustered system property</li>
     *   <li>clustered property in forage-vertx.properties</li>
     *   <li>false (default)</li>
     * </ol>
     *
     * @return true if clustered mode is enabled, false otherwise
     */
    public Boolean clustered() {
        return ConfigStore.getInstance()
                .get(CLUSTERED.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }
}
