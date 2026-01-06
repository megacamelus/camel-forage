package io.kaoto.forage.agent.factory;

import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration entries definition for individual agent factory configurations.
 *
 * <p>This class defines all configuration modules and entries used by {@link AgentFactoryConfig}
 * to manage individual agent settings. It provides the configuration infrastructure for
 * single-agent scenarios and individual agent instances within multi-agent setups.
 *
 * <p>Supports both default configurations and named/prefixed configurations for multi-instance
 * scenarios through the standard Camel Forage configuration pattern.
 */
public final class AgentFactoryConfigEntries extends ConfigEntries {
    public static final String FEATURE_MEMORY = "memory";
    public static final ConfigModule PROVIDER_MODEL_FACTORY_CLASS =
            ConfigModule.of(AgentFactoryConfig.class, "forage.provider.model.factory.class");
    public static final ConfigModule PROVIDER_FEATURES =
            ConfigModule.of(AgentFactoryConfig.class, "forage.provider.features");
    public static final ConfigModule PROVIDER_FEATURES_MEMORY_FACTORY_CLASS =
            ConfigModule.of(AgentFactoryConfig.class, "forage.provider.features.memory.factory.class");
    public static final ConfigModule PROVIDER_AGENT_CLASS =
            ConfigModule.of(AgentFactoryConfig.class, "forage.provider.agent.class");
    public static final ConfigModule GUARDRAILS_INPUT_CLASSES =
            ConfigModule.of(AgentFactoryConfig.class, "forage.guardrails.input.classes");
    public static final ConfigModule GUARDRAILS_OUTPUT_CLASSES =
            ConfigModule.of(AgentFactoryConfig.class, "forage.guardrails.output.classes");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(PROVIDER_MODEL_FACTORY_CLASS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PROVIDER_FEATURES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PROVIDER_FEATURES_MEMORY_FACTORY_CLASS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PROVIDER_AGENT_CLASS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(GUARDRAILS_INPUT_CLASSES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(GUARDRAILS_OUTPUT_CLASSES, ConfigEntry.fromModule());
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
