package org.apache.camel.forage.models.chat.openai;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigTag;

public final class OpenAIConfigEntries extends ConfigEntries {
    public static final ConfigModule API_KEY = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.api.key",
            "OpenAI API key for authentication",
            "API Key",
            null,
            "password",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule MODEL_NAME = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.model.name",
            "The specific OpenAI model to use",
            "Model Name",
            "gpt-3.5-turbo",
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule BASE_URL = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.base.url",
            "Custom base URL for OpenAI API",
            "Base URL",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule TEMPERATURE = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.temperature",
            "Temperature for response randomness (0.0-2.0)",
            "Temperature",
            null,
            "double",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule MAX_TOKENS = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.max.tokens",
            "Maximum number of tokens to generate",
            "Max Tokens",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule TOP_P = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.top.p",
            "Top-P (nucleus) sampling parameter (0.0-1.0)",
            "Top P",
            null,
            "double",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule FREQUENCY_PENALTY = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.frequency.penalty",
            "Frequency penalty (-2.0 to 2.0)",
            "Frequency Penalty",
            null,
            "double",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule PRESENCE_PENALTY = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.presence.penalty",
            "Presence penalty (-2.0 to 2.0)",
            "Presence Penalty",
            null,
            "double",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule LOG_REQUESTS = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.log.requests",
            "Enable request logging",
            "Log Requests",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule LOG_RESPONSES = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.log.responses",
            "Enable response logging",
            "Log Responses",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule TIMEOUT = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.timeout",
            "Request timeout duration",
            "Timeout",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule HTTP1_1 = ConfigModule.of(
            OpenAIConfig.class,
            "forage.openai.http1",
            "Use HTTP/1.1 instead of HTTP/2",
            "Use HTTP/1.1",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);

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
        CONFIG_MODULES.put(TIMEOUT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(HTTP1_1, ConfigEntry.fromModule());
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
