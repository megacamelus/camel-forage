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
 * Configuration entries for keyword filter guardrail.
 */
public final class KeywordFilterGuardrailConfigEntries extends ConfigEntries {

    public static final ConfigModule BLOCKED_WORDS = ConfigModule.of(
            KeywordFilterGuardrailConfig.class,
            "forage.guardrail.keyword.filter.blocked.words",
            "Comma-separated list of words to block",
            "Blocked Words",
            null,
            "string",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule CASE_SENSITIVE = ConfigModule.of(
            KeywordFilterGuardrailConfig.class,
            "forage.guardrail.keyword.filter.case.sensitive",
            "Whether matching should be case-sensitive",
            "Case Sensitive",
            "false",
            "boolean",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule WHOLE_WORD_MATCH = ConfigModule.of(
            KeywordFilterGuardrailConfig.class,
            "forage.guardrail.keyword.filter.whole.word.match",
            "Whether to match whole words only",
            "Whole Word Match",
            "true",
            "boolean",
            false,
            ConfigTag.COMMON);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(BLOCKED_WORDS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CASE_SENSITIVE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(WHOLE_WORD_MATCH, ConfigEntry.fromModule());
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
