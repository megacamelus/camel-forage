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
 * Configuration entries for sensitive data output guardrail.
 */
public final class SensitiveDataGuardrailConfigEntries extends ConfigEntries {

    public static final ConfigModule DETECT_TYPES = ConfigModule.of(
            SensitiveDataGuardrailConfig.class,
            "forage.guardrail.sensitive.data.detect.types",
            "Comma-separated list of sensitive data types to detect: API_KEY, AWS_KEY, SECRET, PRIVATE_KEY, CREDIT_CARD, SSN, JWT, CONNECTION_STRING, GITHUB_TOKEN",
            "Detect Types",
            "API_KEY,AWS_KEY,SECRET,PRIVATE_KEY,CREDIT_CARD,SSN,JWT,CONNECTION_STRING,GITHUB_TOKEN",
            "string",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule ACTION = ConfigModule.of(
            SensitiveDataGuardrailConfig.class,
            "forage.guardrail.sensitive.data.action",
            "Action to take when sensitive data is detected: BLOCK, REDACT, WARN",
            "Action",
            "BLOCK",
            "string",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule REDACTION_TEXT = ConfigModule.of(
            SensitiveDataGuardrailConfig.class,
            "forage.guardrail.sensitive.data.redaction.text",
            "Text to use for redaction when action is REDACT",
            "Redaction Text",
            "[REDACTED]",
            "string",
            false,
            ConfigTag.COMMON);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(DETECT_TYPES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ACTION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(REDACTION_TEXT, ConfigEntry.fromModule());
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
