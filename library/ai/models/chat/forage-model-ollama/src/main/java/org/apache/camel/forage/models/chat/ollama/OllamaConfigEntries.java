package org.apache.camel.forage.models.chat.ollama;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigTag;

public final class OllamaConfigEntries extends ConfigEntries {
    public static final ConfigModule BASE_URL = ConfigModule.of(
            OllamaConfig.class,
            "ollama.base.url",
            "The base URL of the Ollama server",
            "Base URL",
            "http://localhost:11434",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule MODEL_NAME = ConfigModule.of(
            OllamaConfig.class,
            "ollama.model.name",
            "The Ollama model to use",
            "Model Name",
            "llama3",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule TEMPERATURE = ConfigModule.of(
            OllamaConfig.class,
            "ollama.temperature",
            "Temperature for response randomness (0.0-2.0)",
            "Temperature",
            null,
            "double",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule TOP_K = ConfigModule.of(
            OllamaConfig.class,
            "ollama.top.k",
            "Top-K sampling parameter",
            "Top K",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule TOP_P = ConfigModule.of(
            OllamaConfig.class,
            "ollama.top.p",
            "Top-P (nucleus) sampling parameter (0.0-1.0)",
            "Top P",
            null,
            "double",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule MIN_P = ConfigModule.of(
            OllamaConfig.class,
            "ollama.min.p",
            "Minimum probability threshold (0.0-1.0)",
            "Min P",
            null,
            "double",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule NUM_CTX = ConfigModule.of(
            OllamaConfig.class,
            "ollama.num.ctx",
            "Context window size",
            "Context Size",
            null,
            "integer",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule LOG_REQUESTS = ConfigModule.of(
            OllamaConfig.class,
            "ollama.log.requests",
            "Enable request logging",
            "Log Requests",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule LOG_RESPONSES = ConfigModule.of(
            OllamaConfig.class,
            "ollama.log.responses",
            "Enable response logging",
            "Log Responses",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(BASE_URL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MODEL_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TEMPERATURE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TOP_K, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TOP_P, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MIN_P, ConfigEntry.fromModule());
        CONFIG_MODULES.put(NUM_CTX, ConfigEntry.fromModule());
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
