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
 * Configuration entries for JSON format guardrail.
 */
public final class JsonFormatGuardrailConfigEntries extends ConfigEntries {

    public static final ConfigModule REQUIRED_FIELDS = ConfigModule.of(
            JsonFormatGuardrailConfig.class,
            "forage.guardrail.json.format.required.fields",
            "Comma-separated list of required field names in the JSON",
            "Required Fields",
            null,
            "string",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule EXTRACT_JSON = ConfigModule.of(
            JsonFormatGuardrailConfig.class,
            "forage.guardrail.json.format.extract.json",
            "Whether to extract JSON from surrounding text",
            "Extract JSON",
            "true",
            "boolean",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule ALLOW_ARRAY = ConfigModule.of(
            JsonFormatGuardrailConfig.class,
            "forage.guardrail.json.format.allow.array",
            "Whether to allow JSON arrays (not just objects)",
            "Allow Array",
            "true",
            "boolean",
            false,
            ConfigTag.COMMON);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(REQUIRED_FIELDS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(EXTRACT_JSON, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ALLOW_ARRAY, ConfigEntry.fromModule());
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
