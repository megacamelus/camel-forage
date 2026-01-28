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
 * Configuration entries for code injection guardrail.
 */
public final class CodeInjectionGuardrailConfigEntries extends ConfigEntries {

    public static final ConfigModule STRICT = ConfigModule.of(
            CodeInjectionGuardrailConfig.class,
            "forage.guardrail.code.injection.strict",
            "Enable strict mode (fail on any single pattern match)",
            "Strict Mode",
            "false",
            "boolean",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule DETECT_TYPES = ConfigModule.of(
            CodeInjectionGuardrailConfig.class,
            "forage.guardrail.code.injection.detect.types",
            "Comma-separated list of injection types to detect: SHELL_COMMAND, SQL_INJECTION, JAVASCRIPT, HTML_XSS, PATH_TRAVERSAL, COMMAND_CHAINING, TEMPLATE_INJECTION",
            "Detect Types",
            "SHELL_COMMAND,SQL_INJECTION,JAVASCRIPT,HTML_XSS,PATH_TRAVERSAL,COMMAND_CHAINING,TEMPLATE_INJECTION",
            "string",
            false,
            ConfigTag.COMMON);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(STRICT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DETECT_TYPES, ConfigEntry.fromModule());
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
