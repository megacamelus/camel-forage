package org.apache.camel.forage.models.chat.huggingface;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigTag;

public final class HuggingFaceConfigEntries extends ConfigEntries {
    public static final ConfigModule API_KEY = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.api.key",
            "HuggingFace API key for authentication",
            "API Key",
            null,
            "password",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule MODEL_ID = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.model.id",
            "The HuggingFace model ID to use (e.g., microsoft/DialoGPT-medium)",
            "Model ID",
            null,
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule TEMPERATURE = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.temperature",
            "Temperature for response generation (0.0-2.0)",
            "Temperature",
            null,
            "double",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule MAX_NEW_TOKENS = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.max.new.tokens",
            "Maximum number of new tokens to generate",
            "Max New Tokens",
            null,
            "integer",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule TOP_K = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.top.k",
            "Top-k sampling parameter",
            "Top K",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule TOP_P = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.top.p",
            "Top-p (nucleus) sampling parameter",
            "Top P",
            null,
            "double",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule DO_SAMPLE = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.do.sample",
            "Whether to use sampling for text generation",
            "Do Sample",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule REPETITION_PENALTY = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.repetition.penalty",
            "Penalty for repeating tokens (1.0-2.0)",
            "Repetition Penalty",
            null,
            "double",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule RETURN_FULL_TEXT = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.return.full.text",
            "Whether to return full text including input",
            "Return Full Text",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule WAIT_FOR_MODEL = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.wait.for.model",
            "Whether to wait for the model to load if it's not ready",
            "Wait for Model",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule TIMEOUT = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.timeout",
            "Request timeout in seconds",
            "Timeout",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule MAX_RETRIES = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.max.retries",
            "Maximum number of retry attempts",
            "Max Retries",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule LOG_REQUESTS_AND_RESPONSES = ConfigModule.of(
            HuggingFaceConfig.class,
            "huggingface.log.requests.and.responses",
            "Enable request and response logging",
            "Log Requests/Responses",
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
        CONFIG_MODULES.put(MODEL_ID, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TEMPERATURE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_NEW_TOKENS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TOP_K, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TOP_P, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DO_SAMPLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(REPETITION_PENALTY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(RETURN_FULL_TEXT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(WAIT_FOR_MODEL, ConfigEntry.fromModule());
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
