package io.kaoto.forage.vertx;

import static io.kaoto.forage.vertx.VertxConfigEntries.BLOCKED_THREAD_CHECK_INTERVAL;
import static io.kaoto.forage.vertx.VertxConfigEntries.BLOCKED_THREAD_CHECK_INTERVAL_UNIT;
import static io.kaoto.forage.vertx.VertxConfigEntries.CLUSTERED;
import static io.kaoto.forage.vertx.VertxConfigEntries.DISABLE_TCCL;
import static io.kaoto.forage.vertx.VertxConfigEntries.EVENT_LOOP_POOL_SIZE;
import static io.kaoto.forage.vertx.VertxConfigEntries.HA_ENABLED;
import static io.kaoto.forage.vertx.VertxConfigEntries.HA_GROUP;
import static io.kaoto.forage.vertx.VertxConfigEntries.INTERNAL_BLOCKING_POOL_SIZE;
import static io.kaoto.forage.vertx.VertxConfigEntries.MAX_EVENT_LOOP_EXECUTE_TIME;
import static io.kaoto.forage.vertx.VertxConfigEntries.MAX_EVENT_LOOP_EXECUTE_TIME_UNIT;
import static io.kaoto.forage.vertx.VertxConfigEntries.MAX_WORKER_EXECUTE_TIME;
import static io.kaoto.forage.vertx.VertxConfigEntries.MAX_WORKER_EXECUTE_TIME_UNIT;
import static io.kaoto.forage.vertx.VertxConfigEntries.PREFER_NATIVE_TRANSPORT;
import static io.kaoto.forage.vertx.VertxConfigEntries.QUORUM_SIZE;
import static io.kaoto.forage.vertx.VertxConfigEntries.USE_DAEMON_THREAD;
import static io.kaoto.forage.vertx.VertxConfigEntries.WARNING_EXCEPTION_TIME;
import static io.kaoto.forage.vertx.VertxConfigEntries.WARNING_EXCEPTION_TIME_UNIT;
import static io.kaoto.forage.vertx.VertxConfigEntries.WORKER_POOL_SIZE;

import io.vertx.core.VertxOptions;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;

/**
 * Configuration class for Vert.x integration in the Forage framework.
 *
 * <p>This configuration class manages the settings required to create and configure
 * Vert.x instances. It handles Vert.x options such as worker pool size, event loop
 * pool size, clustering, high availability, threading, and performance tuning configuration
 * through environment variables with appropriate fallback mechanisms and default values.
 *
 * <p><strong>Configuration Parameters:</strong>
 * <ul>
 *   <li><strong>VERTX_WORKER_POOL_SIZE</strong> - The size of the worker thread pool (no default)</li>
 *   <li><strong>VERTX_EVENT_LOOP_POOL_SIZE</strong> - The size of the event loop thread pool (no default)</li>
 *   <li><strong>VERTX_INTERNAL_BLOCKING_POOL_SIZE</strong> - The size of the internal blocking pool (no default)</li>
 *   <li><strong>VERTX_BLOCKED_THREAD_CHECK_INTERVAL</strong> - Interval for checking blocked threads in time units (no default)</li>
 *   <li><strong>VERTX_MAX_EVENT_LOOP_EXECUTE_TIME</strong> - Maximum event loop execution time in time units (no default)</li>
 *   <li><strong>VERTX_MAX_WORKER_EXECUTE_TIME</strong> - Maximum worker execution time in time units (no default)</li>
 *   <li><strong>VERTX_HA_ENABLED</strong> - Whether to enable high availability (default: false)</li>
 *   <li><strong>VERTX_QUORUM_SIZE</strong> - The quorum size for HA (no default)</li>
 *   <li><strong>VERTX_HA_GROUP</strong> - The HA group name (no default)</li>
 *   <li><strong>VERTX_WARNING_EXCEPTION_TIME</strong> - Time threshold for warning exceptions in time units (no default)</li>
 *   <li><strong>VERTX_PREFER_NATIVE_TRANSPORT</strong> - Whether to prefer native transport (default: false)</li>
 *   <li><strong>VERTX_MAX_EVENT_LOOP_EXECUTE_TIME_UNIT</strong> - Time unit for max event loop execute time (no default)</li>
 *   <li><strong>VERTX_MAX_WORKER_EXECUTE_TIME_UNIT</strong> - Time unit for max worker execute time (no default)</li>
 *   <li><strong>VERTX_BLOCKED_THREAD_CHECK_INTERVAL_UNIT</strong> - Time unit for blocked thread check interval (no default)</li>
 *   <li><strong>VERTX_DISABLE_TCCL</strong> - Whether to disable thread context class loader (default: false)</li>
 *   <li><strong>VERTX_USE_DAEMON_THREAD</strong> - Whether to use daemon threads (default: false)</li>
 *   <li><strong>VERTX_CLUSTERED</strong> - Whether to create a clustered Vert.x instance (default: false)</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (e.g., VERTX_WORKER_POOL_SIZE, VERTX_HA_ENABLED)</li>
 *   <li>System properties (e.g., vertx.worker.pool.size, vertx.ha.enabled)</li>
 *   <li>forage-vertx.properties file in classpath</li>
 *   <li>Default values if none of the above are provided</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables (optional)
 * export VERTX_WORKER_POOL_SIZE="20"
 * export VERTX_EVENT_LOOP_POOL_SIZE="8"
 * export VERTX_HA_ENABLED="true"
 * export VERTX_QUORUM_SIZE="3"
 * export VERTX_PREFER_NATIVE_TRANSPORT="true"
 *
 * // Create and use configuration
 * VertxConfig config = new VertxConfig();
 * Integer workerPoolSize = config.workerPoolSize();
 * Integer eventLoopPoolSize = config.eventLoopPoolSize();
 * Boolean haEnabled = config.haEnabled();
 * Integer quorumSize = config.quorumSize();
 * Boolean preferNativeTransport = config.preferNativeTransport();
 * }</pre>
 *
 * <p><strong>Default Values:</strong>
 * <ul>
 *   <li>Worker Pool Size: null (uses Vert.x default)</li>
 *   <li>Event Loop Pool Size: null (uses Vert.x default)</li>
 *   <li>Internal Blocking Pool Size: null (uses Vert.x default)</li>
 *   <li>Blocked Thread Check Interval: null (uses Vert.x default)</li>
 *   <li>Max Event Loop Execute Time: null (uses Vert.x default)</li>
 *   <li>Max Worker Execute Time: null (uses Vert.x default)</li>
 *   <li>HA Enabled: false</li>
 *   <li>Quorum Size: null</li>
 *   <li>HA Group: null</li>
 *   <li>Warning Exception Time: null (uses Vert.x default)</li>
 *   <li>Prefer Native Transport: false</li>
 *   <li>Max Event Loop Execute Time Unit: null (uses Vert.x default)</li>
 *   <li>Max Worker Execute Time Unit: null (uses Vert.x default)</li>
 *   <li>Blocked Thread Check Interval Unit: null (uses Vert.x default)</li>
 *   <li>Disable TCCL: false</li>
 *   <li>Use Daemon Thread: false</li>
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
     *   <li>Registers all Vert.x configuration parameters (pool sizes, HA settings, threading options, etc.)</li>
     *   <li>Attempts to load additional properties from forage-vertx.properties</li>
     *   <li>Loads overrides from environment variables and system properties</li>
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
                .orElse(VertxOptions.DEFAULT_WORKER_POOL_SIZE);
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
                .orElse(VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE);
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

    public Integer internalBlockingPoolSize() {
        return ConfigStore.getInstance()
                .get(INTERNAL_BLOCKING_POOL_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(VertxOptions.DEFAULT_INTERNAL_BLOCKING_POOL_SIZE);
    }

    public Long blockedThreadCheckInterval() {
        return ConfigStore.getInstance()
                .get(BLOCKED_THREAD_CHECK_INTERVAL.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(VertxOptions.DEFAULT_BLOCKED_THREAD_CHECK_INTERVAL);
    }

    public Long maxEventLoopExecuteTime() {
        return ConfigStore.getInstance()
                .get(MAX_EVENT_LOOP_EXECUTE_TIME.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME);
    }

    public Long maxWorkerExecuteTime() {
        return ConfigStore.getInstance()
                .get(MAX_WORKER_EXECUTE_TIME.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME);
    }

    public Boolean haEnabled() {
        return ConfigStore.getInstance()
                .get(HA_ENABLED.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(VertxOptions.DEFAULT_HA_ENABLED);
    }

    public Integer quorumSize() {
        return ConfigStore.getInstance()
                .get(QUORUM_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(VertxOptions.DEFAULT_QUORUM_SIZE);
    }

    public String haGroup() {
        return ConfigStore.getInstance().get(HA_GROUP.asNamed(prefix)).orElse(VertxOptions.DEFAULT_HA_GROUP);
    }

    public Long warningExceptionTime() {
        return ConfigStore.getInstance()
                .get(WARNING_EXCEPTION_TIME.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(TimeUnit.SECONDS.toNanos(5));
    }

    public TimeUnit warningExceptionTimeUnit() {
        return ConfigStore.getInstance()
                .get(WARNING_EXCEPTION_TIME_UNIT.asNamed(prefix))
                .map(TimeUnit::valueOf)
                .orElse(VertxOptions.DEFAULT_WARNING_EXCEPTION_TIME_UNIT);
    }

    public Boolean preferNativeTransport() {
        return ConfigStore.getInstance()
                .get(PREFER_NATIVE_TRANSPORT.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(VertxOptions.DEFAULT_PREFER_NATIVE_TRANSPORT);
    }

    public TimeUnit maxEventLoopExecuteTimeUnit() {
        return ConfigStore.getInstance()
                .get(MAX_EVENT_LOOP_EXECUTE_TIME_UNIT.asNamed(prefix))
                .map(TimeUnit::valueOf)
                .orElse(VertxOptions.DEFAULT_MAX_EVENT_LOOP_EXECUTE_TIME_UNIT);
    }

    public TimeUnit maxWorkerExecuteTimeUnit() {
        return ConfigStore.getInstance()
                .get(MAX_WORKER_EXECUTE_TIME_UNIT.asNamed(prefix))
                .map(TimeUnit::valueOf)
                .orElse(VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME_UNIT);
    }

    public TimeUnit blockedThreadCheckIntervalUnit() {
        return ConfigStore.getInstance()
                .get(BLOCKED_THREAD_CHECK_INTERVAL_UNIT.asNamed(prefix))
                .map(TimeUnit::valueOf)
                .orElse(VertxOptions.DEFAULT_BLOCKED_THREAD_CHECK_INTERVAL_UNIT);
    }

    public Boolean disableTccl() {
        return ConfigStore.getInstance()
                .get(DISABLE_TCCL.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(VertxOptions.DEFAULT_DISABLE_TCCL);
    }

    public Boolean useDaemonThread() {
        return ConfigStore.getInstance()
                .get(USE_DAEMON_THREAD.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(VertxOptions.DEFAULT_USE_DAEMON_THREAD);
    }
}
