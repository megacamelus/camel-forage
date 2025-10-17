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
    public static final ConfigModule CLUSTERED = ConfigModule.of(VertxConfig.class, "vertx.clustered");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(WORKER_POOL_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(EVENT_LOOP_POOL_SIZE, ConfigEntry.fromModule());
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
