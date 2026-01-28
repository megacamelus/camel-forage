package io.kaoto.forage.guardrails.output;

import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigTag;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration entries for output length guardrail.
 */
public final class OutputLengthGuardrailConfigEntries extends ConfigEntries {

    public static final ConfigModule MAX_CHARS = ConfigModule.of(
            OutputLengthGuardrailConfig.class,
            "forage.guardrail.output.length.max.chars",
            "Maximum allowed character count for output messages",
            "Max Characters",
            "50000",
            "integer",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule MIN_CHARS = ConfigModule.of(
            OutputLengthGuardrailConfig.class,
            "forage.guardrail.output.length.min.chars",
            "Minimum required character count for output messages",
            "Min Characters",
            "1",
            "integer",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule TRUNCATE_ON_OVERFLOW = ConfigModule.of(
            OutputLengthGuardrailConfig.class,
            "forage.guardrail.output.length.truncate.on.overflow",
            "Whether to truncate instead of failing on overflow",
            "Truncate on Overflow",
            "false",
            "boolean",
            false,
            ConfigTag.COMMON);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(MAX_CHARS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MIN_CHARS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRUNCATE_ON_OVERFLOW, ConfigEntry.fromModule());
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
