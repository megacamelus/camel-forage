package org.apache.camel.forage.models.chat.openai;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;

public final class OpenAIConfigEntries extends ConfigEntries {
    public static final ConfigModule API_KEY = ConfigModule.of(OpenAIConfig.class, "openai.api.key");
    public static final ConfigModule MODEL_NAME = ConfigModule.of(OpenAIConfig.class, "openai.model.name");
    public static final ConfigModule BASE_URL = ConfigModule.of(OpenAIConfig.class, "openai.base.url");
    public static final ConfigModule TEMPERATURE = ConfigModule.of(OpenAIConfig.class, "openai.temperature");
    public static final ConfigModule MAX_TOKENS = ConfigModule.of(OpenAIConfig.class, "openai.max.tokens");
    public static final ConfigModule TOP_P = ConfigModule.of(OpenAIConfig.class, "openai.top.p");
    public static final ConfigModule FREQUENCY_PENALTY =
            ConfigModule.of(OpenAIConfig.class, "openai.frequency.penalty");
    public static final ConfigModule PRESENCE_PENALTY = ConfigModule.of(OpenAIConfig.class, "openai.presence.penalty");
    public static final ConfigModule LOG_REQUESTS = ConfigModule.of(OpenAIConfig.class, "openai.log.requests");
    public static final ConfigModule LOG_RESPONSES = ConfigModule.of(OpenAIConfig.class, "openai.log.responses");

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(API_KEY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MODEL_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(BASE_URL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TEMPERATURE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_TOKENS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TOP_P, ConfigEntry.fromModule());
        CONFIG_MODULES.put(FREQUENCY_PENALTY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PRESENCE_PENALTY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(LOG_REQUESTS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(LOG_RESPONSES, ConfigEntry.fromModule());
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
