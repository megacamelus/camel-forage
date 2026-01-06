package io.kaoto.forage.agent.factory;

import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration entries definition for multi-agent orchestration configurations.
 *
 * <p>This class defines all configuration modules and entries used by {@link MultiAgentConfig}
 * to manage multi-agent scenarios and agent ID extraction strategies. It provides the
 * configuration infrastructure for sophisticated multi-agent routing and coordination.
 *
 * <p>These configuration entries enable flexible agent routing strategies, supporting extraction
 * of agent identifiers from various exchange sources (route ID, headers, properties, variables).
 * Each configuration entry supports both default and named/prefixed configurations for
 * multi-instance scenarios.
 */
public final class MultiAgentConfigEntries extends ConfigEntries {
    public static final ConfigModule MULTI_AGENT_NAMES =
            ConfigModule.of(MultiAgentConfig.class, "forage.multi.agent.names");
    public static final ConfigModule MULTI_AGENT_ID_SOURCE =
            ConfigModule.of(MultiAgentConfig.class, "forage.multi.agent.id.source");
    public static final ConfigModule MULTI_AGENT_ID_SOURCE_HEADER =
            ConfigModule.of(MultiAgentConfig.class, "forage.multi.agent.id.source.header");
    public static final ConfigModule MULTI_AGENT_ID_SOURCE_PROPERTY =
            ConfigModule.of(MultiAgentConfig.class, "forage.multi.agent.id.source.property");
    public static final ConfigModule MULTI_AGENT_ID_SOURCE_VARIABLE =
            ConfigModule.of(MultiAgentConfig.class, "forage.multi.agent.id.source.variable");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(MULTI_AGENT_NAMES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MULTI_AGENT_ID_SOURCE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MULTI_AGENT_ID_SOURCE_HEADER, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MULTI_AGENT_ID_SOURCE_PROPERTY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MULTI_AGENT_ID_SOURCE_VARIABLE, ConfigEntry.fromModule());
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
