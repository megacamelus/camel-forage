package org.apache.camel.forage.vertx;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;

public final class VertxConfigEntries extends ConfigEntries {
    public static final ConfigModule WORKER_POOL_SIZE = ConfigModule.of(VertxConfig.class, "vertx.worker.pool.size");
    public static final ConfigModule EVENT_LOOP_POOL_SIZE =
            ConfigModule.of(VertxConfig.class, "vertx.event.loop.pool.size");
    public static final ConfigModule INTERNAL_BLOCKING_POOL_SIZE =
            ConfigModule.of(VertxConfig.class, "vertx.internal.blocking.pool.size");
    public static final ConfigModule BLOCKED_THREAD_CHECK_INTERVAL =
            ConfigModule.of(VertxConfig.class, "vertx.blocked.thread.check.interval");
    public static final ConfigModule MAX_EVENT_LOOP_EXECUTE_TIME =
            ConfigModule.of(VertxConfig.class, "vertx.max.event.loop.execute.time");
    public static final ConfigModule MAX_WORKER_EXECUTE_TIME =
            ConfigModule.of(VertxConfig.class, "vertx.max.worker.execute.time");
    public static final ConfigModule HA_ENABLED = ConfigModule.of(VertxConfig.class, "vertx.ha.enabled");
    public static final ConfigModule QUORUM_SIZE = ConfigModule.of(VertxConfig.class, "vertx.quorum.size");
    public static final ConfigModule HA_GROUP = ConfigModule.of(VertxConfig.class, "vertx.ha.group");
    public static final ConfigModule WARNING_EXCEPTION_TIME =
            ConfigModule.of(VertxConfig.class, "vertx.warning.exception.time");
    public static final ConfigModule WARNING_EXCEPTION_TIME_UNIT =
            ConfigModule.of(VertxConfig.class, "vertx.warning.exception.time.unit");
    public static final ConfigModule PREFER_NATIVE_TRANSPORT =
            ConfigModule.of(VertxConfig.class, "vertx.prefer.native.transport");
    public static final ConfigModule MAX_EVENT_LOOP_EXECUTE_TIME_UNIT =
            ConfigModule.of(VertxConfig.class, "vertx.max.event.loop.execute.time.unit");
    public static final ConfigModule MAX_WORKER_EXECUTE_TIME_UNIT =
            ConfigModule.of(VertxConfig.class, "vertx.max.worker.execute.time.unit");
    public static final ConfigModule BLOCKED_THREAD_CHECK_INTERVAL_UNIT =
            ConfigModule.of(VertxConfig.class, "vertx.blocked.thread.check.interval.unit");
    public static final ConfigModule DISABLE_TCCL = ConfigModule.of(VertxConfig.class, "vertx.disable.tccl");
    public static final ConfigModule USE_DAEMON_THREAD = ConfigModule.of(VertxConfig.class, "vertx.use.daemon.thread");
    public static final ConfigModule CLUSTERED = ConfigModule.of(VertxConfig.class, "vertx.clustered");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(WORKER_POOL_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(EVENT_LOOP_POOL_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(INTERNAL_BLOCKING_POOL_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(BLOCKED_THREAD_CHECK_INTERVAL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_EVENT_LOOP_EXECUTE_TIME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_WORKER_EXECUTE_TIME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(HA_ENABLED, ConfigEntry.fromModule());
        CONFIG_MODULES.put(QUORUM_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(HA_GROUP, ConfigEntry.fromModule());
        CONFIG_MODULES.put(WARNING_EXCEPTION_TIME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(WARNING_EXCEPTION_TIME_UNIT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PREFER_NATIVE_TRANSPORT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_EVENT_LOOP_EXECUTE_TIME_UNIT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_WORKER_EXECUTE_TIME_UNIT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(BLOCKED_THREAD_CHECK_INTERVAL_UNIT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DISABLE_TCCL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USE_DAEMON_THREAD, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CLUSTERED, ConfigEntry.fromModule());
    }

    public static Map<ConfigModule, ConfigEntry> entries() {
        return Collections.unmodifiableMap(CONFIG_MODULES);
    }

    public static Optional<ConfigModule> find(String prefix, String name) {
        return find(CONFIG_MODULES, prefix, name);
    }

    /**
     * Registers new known configuration if a prefix is provided (otherwise is ignored)
     * @param prefix the prefix to register
     */
    public static void register(String prefix) {
        if (prefix != null) {
            for (Map.Entry<ConfigModule, ConfigEntry> entry : entries().entrySet()) {
                ConfigModule configModule = entry.getKey().asNamed(prefix);
                CONFIG_MODULES.put(configModule, ConfigEntry.fromModule());
            }
        }
    }

    /**
     * Load override configurations (which are defined via environment variables and/or system properties)
     * @param prefix and optional prefix to use
     */
    public static void loadOverrides(String prefix) {
        load(CONFIG_MODULES, prefix);
    }
}
