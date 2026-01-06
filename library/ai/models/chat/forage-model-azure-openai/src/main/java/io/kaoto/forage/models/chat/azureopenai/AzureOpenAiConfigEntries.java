package io.kaoto.forage.models.chat.azureopenai;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigTag;

public final class AzureOpenAiConfigEntries extends ConfigEntries {
    public static final ConfigModule API_KEY = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.api.key",
            "Azure OpenAI API key for authentication",
            "API Key",
            null,
            "password",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule ENDPOINT = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.endpoint",
            "Azure OpenAI resource endpoint URL (e.g., https://your-resource.openai.azure.com/)",
            "Endpoint",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule DEPLOYMENT_NAME = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.deployment.name",
            "Azure OpenAI deployment name (e.g., gpt-35-turbo, gpt-4)",
            "Deployment Name",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule SERVICE_VERSION = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.service.version",
            "Azure OpenAI API service version (e.g., 2024-02-01)",
            "Service Version",
            null,
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule TEMPERATURE = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.temperature",
            "Temperature for response generation (0.0-2.0): lower values are more deterministic, higher values are more creative",
            "Temperature",
            null,
            "double",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule MAX_TOKENS = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.max.tokens",
            "Maximum number of tokens in the model's response",
            "Max Tokens",
            null,
            "integer",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule TOP_P = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.top.p",
            "Top-p (nucleus sampling) probability threshold (0.0-1.0)",
            "Top P",
            null,
            "double",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule PRESENCE_PENALTY = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.presence.penalty",
            "Presence penalty for discouraging new topic introduction (-2.0 to 2.0)",
            "Presence Penalty",
            null,
            "double",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule FREQUENCY_PENALTY = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.frequency.penalty",
            "Frequency penalty for discouraging token repetition (-2.0 to 2.0)",
            "Frequency Penalty",
            null,
            "double",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule SEED = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.seed",
            "Seed for deterministic response generation (same seed + same input = similar output)",
            "Seed",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule USER = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.user",
            "User identifier for tracking and monitoring API usage",
            "User ID",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule TIMEOUT = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.timeout",
            "Request timeout in seconds",
            "Timeout",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule MAX_RETRIES = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.max.retries",
            "Maximum number of retry attempts for failed requests",
            "Max Retries",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule LOG_REQUESTS_AND_RESPONSES = ConfigModule.of(
            AzureOpenAiConfig.class,
            "forage.azure.openai.log.requests.and.responses",
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
        CONFIG_MODULES.put(ENDPOINT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DEPLOYMENT_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(SERVICE_VERSION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TEMPERATURE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_TOKENS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TOP_P, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PRESENCE_PENALTY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(FREQUENCY_PENALTY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(SEED, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USER, ConfigEntry.fromModule());
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
