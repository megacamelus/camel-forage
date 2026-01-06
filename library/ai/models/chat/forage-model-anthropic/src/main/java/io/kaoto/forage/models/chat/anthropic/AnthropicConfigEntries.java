package io.kaoto.forage.models.chat.anthropic;

import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigTag;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class AnthropicConfigEntries extends ConfigEntries {
    public static final ConfigModule API_KEY = ConfigModule.of(
            AnthropicConfig.class,
            "forage.anthropic.api.key",
            "Anthropic API key for authentication",
            "API Key",
            null,
            "password",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule MODEL_NAME = ConfigModule.of(
            AnthropicConfig.class,
            "forage.anthropic.model.name",
            "Claude model name (e.g., claude-3-haiku-20240307, claude-3-sonnet-20240229, claude-3-opus-20240229)",
            "Model Name",
            "claude-3-haiku-20240307",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule TEMPERATURE = ConfigModule.of(
            AnthropicConfig.class,
            "forage.anthropic.temperature",
            "Temperature for response generation (0.0-1.0): lower values are more deterministic, higher values are more creative",
            "Temperature",
            null,
            "double",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule MAX_TOKENS = ConfigModule.of(
            AnthropicConfig.class,
            "forage.anthropic.max.tokens",
            "Maximum number of tokens in the model's response",
            "Max Tokens",
            null,
            "integer",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule TOP_P = ConfigModule.of(
            AnthropicConfig.class,
            "forage.anthropic.top.p",
            "Top-p (nucleus sampling) probability threshold (0.0-1.0)",
            "Top P",
            null,
            "double",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule TOP_K = ConfigModule.of(
            AnthropicConfig.class,
            "forage.anthropic.top.k",
            "Top-k sampling parameter: limits the model to consider only the top-k most probable tokens",
            "Top K",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule STOP_SEQUENCES = ConfigModule.of(
            AnthropicConfig.class,
            "forage.anthropic.stop.sequences",
            "Comma-separated stop sequences that cause the model to stop generating further tokens",
            "Stop Sequences",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule TIMEOUT = ConfigModule.of(
            AnthropicConfig.class,
            "forage.anthropic.timeout",
            "Request timeout in seconds",
            "Timeout",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule MAX_RETRIES = ConfigModule.of(
            AnthropicConfig.class,
            "forage.anthropic.max.retries",
            "Maximum number of retry attempts for failed requests",
            "Max Retries",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule LOG_REQUESTS_AND_RESPONSES = ConfigModule.of(
            AnthropicConfig.class,
            "forage.anthropic.log.requests.and.responses",
            "Enable logging of requests and responses (warning: may log sensitive data)",
            "Log Requests",
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
