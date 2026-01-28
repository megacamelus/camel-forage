package io.kaoto.forage.guardrails.input;

import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigTag;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration entries for input length guardrail.
 */
public final class InputLengthGuardrailConfigEntries extends ConfigEntries {

    public static final ConfigModule MAX_CHARS = ConfigModule.of(
            InputLengthGuardrailConfig.class,
            "forage.guardrail.input.length.max.chars",
            "Maximum allowed character count for input messages",
            "Max Characters",
            "10000",
            "integer",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule MIN_CHARS = ConfigModule.of(
            InputLengthGuardrailConfig.class,
            "forage.guardrail.input.length.min.chars",
            "Minimum required character count for input messages",
            "Min Characters",
            "1",
            "integer",
            false,
            ConfigTag.COMMON);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(MAX_CHARS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MIN_CHARS, ConfigEntry.fromModule());
    }

    public static Map<ConfigModule, ConfigEntry> entries() {
        return Collections.unmodifiableMap(CONFIG_MODULES);
    }

    public static Optional<ConfigModule> find(String prefix, String name) {
        return find(CONFIG_MODULES, prefix, name);
    }

    public static void register(String prefix) {
        if (prefix != null) {
            for (Map.Entry<ConfigModule, ConfigEntry> entry : entries().entrySet()) {
                ConfigModule configModule = entry.getKey().asNamed(prefix);
                CONFIG_MODULES.put(configModule, ConfigEntry.fromModule());
            }
        }
    }

    public static void loadOverrides(String prefix) {
        load(CONFIG_MODULES, prefix);
    }
}
