package org.apache.camel.forage.models.chat.dashscope;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;

public final class DashscopeConfigEntries extends ConfigEntries {
    public static final ConfigModule API_KEY = ConfigModule.of(DashscopeConfig.class, "dashscope.api.key");
    public static final ConfigModule MODEL_NAME = ConfigModule.of(DashscopeConfig.class, "dashscope.model.name");
    public static final ConfigModule TEMPERATURE = ConfigModule.of(DashscopeConfig.class, "dashscope.temperature");
    public static final ConfigModule MAX_TOKENS = ConfigModule.of(DashscopeConfig.class, "dashscope.max.tokens");
    public static final ConfigModule TOP_P = ConfigModule.of(DashscopeConfig.class, "dashscope.top.p");
    public static final ConfigModule TOP_K = ConfigModule.of(DashscopeConfig.class, "dashscope.top.k");
    public static final ConfigModule REPETITION_PENALTY =
            ConfigModule.of(DashscopeConfig.class, "dashscope.repetition.penalty");
    public static final ConfigModule SEED = ConfigModule.of(DashscopeConfig.class, "dashscope.seed");
    public static final ConfigModule ENABLE_SEARCH = ConfigModule.of(DashscopeConfig.class, "dashscope.enable.search");
    public static final ConfigModule TIMEOUT = ConfigModule.of(DashscopeConfig.class, "dashscope.timeout");
    public static final ConfigModule MAX_RETRIES = ConfigModule.of(DashscopeConfig.class, "dashscope.max.retries");
    public static final ConfigModule LOG_REQUESTS_AND_RESPONSES =
            ConfigModule.of(DashscopeConfig.class, "dashscope.log.requests.and.responses");

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
        CONFIG_MODULES.put(REPETITION_PENALTY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(SEED, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ENABLE_SEARCH, ConfigEntry.fromModule());
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
