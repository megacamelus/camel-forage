package org.apache.camel.forage.models.chat.anthropic;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;

public final class AnthropicConfigEntries extends ConfigEntries {
    public static final ConfigModule API_KEY = ConfigModule.of(AnthropicConfig.class, "anthropic.api.key");
    public static final ConfigModule MODEL_NAME = ConfigModule.of(AnthropicConfig.class, "anthropic.model.name");
    public static final ConfigModule TEMPERATURE = ConfigModule.of(AnthropicConfig.class, "anthropic.temperature");
    public static final ConfigModule MAX_TOKENS = ConfigModule.of(AnthropicConfig.class, "anthropic.max.tokens");
    public static final ConfigModule TOP_P = ConfigModule.of(AnthropicConfig.class, "anthropic.top.p");
    public static final ConfigModule TOP_K = ConfigModule.of(AnthropicConfig.class, "anthropic.top.k");
    public static final ConfigModule STOP_SEQUENCES =
            ConfigModule.of(AnthropicConfig.class, "anthropic.stop.sequences");
    public static final ConfigModule TIMEOUT = ConfigModule.of(AnthropicConfig.class, "anthropic.timeout");
    public static final ConfigModule MAX_RETRIES = ConfigModule.of(AnthropicConfig.class, "anthropic.max.retries");
    public static final ConfigModule LOG_REQUESTS_AND_RESPONSES =
            ConfigModule.of(AnthropicConfig.class, "anthropic.log.requests.and.responses");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(API_KEY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MODEL_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TEMPERATURE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_TOKENS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TOP_P, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TOP_K, ConfigEntry.fromModule());
        CONFIG_MODULES.put(STOP_SEQUENCES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TIMEOUT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_RETRIES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(LOG_REQUESTS_AND_RESPONSES, ConfigEntry.fromModule());
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
