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
 * Configuration entries for PII detector guardrail.
 */
public final class PiiDetectorGuardrailConfigEntries extends ConfigEntries {

    public static final ConfigModule DETECT_TYPES = ConfigModule.of(
            PiiDetectorGuardrailConfig.class,
            "forage.guardrail.pii.detect.types",
            "Comma-separated list of PII types to detect: EMAIL, PHONE, SSN, CREDIT_CARD, IP_ADDRESS",
            "Detect Types",
            "EMAIL,PHONE,SSN,CREDIT_CARD,IP_ADDRESS",
            "string",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule BLOCK_ON_DETECTION = ConfigModule.of(
            PiiDetectorGuardrailConfig.class,
            "forage.guardrail.pii.block.on.detection",
            "Whether to block the message when PII is detected (true) or just warn (false)",
            "Block on Detection",
            "true",
            "boolean",
            false,
            ConfigTag.COMMON);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(DETECT_TYPES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(BLOCK_ON_DETECTION, ConfigEntry.fromModule());
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
