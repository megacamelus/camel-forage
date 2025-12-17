package org.apache.camel.forage.models.chat.google;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigTag;

public final class GoogleConfigEntries extends ConfigEntries {
    public static final ConfigModule API_KEY = ConfigModule.of(
            GoogleConfig.class,
            "forage.google.api.key",
            "Google AI API key for authentication",
            "API Key",
            null,
            "password",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule MODEL_NAME = ConfigModule.of(
            GoogleConfig.class,
            "forage.google.model.name",
            "Google Gemini model name (e.g., gemini-pro, gemini-pro-vision, gemini-1.5-pro)",
            "Model Name",
            null,
            "string",
            true,
            ConfigTag.COMMON);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(API_KEY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MODEL_NAME, ConfigEntry.fromModule());
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
