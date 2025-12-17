package org.apache.camel.forage.models.chat.bedrock;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigTag;

public final class BedrockConfigEntries extends ConfigEntries {
    public static final ConfigModule REGION = ConfigModule.of(
            BedrockConfig.class,
            "forage.bedrock.region",
            "AWS region where Bedrock is available",
            "Region",
            "us-east-1",
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule MODEL_ID = ConfigModule.of(
            BedrockConfig.class,
            "forage.bedrock.model.id",
            "Bedrock model identifier (e.g., anthropic.claude-3-5-sonnet-20240620-v1:0)",
            "Model ID",
            null,
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule ACCESS_KEY_ID = ConfigModule.of(
            BedrockConfig.class,
            "forage.bedrock.access.key.id",
            "AWS access key ID (optional, uses default credential chain if not provided)",
            "Access Key ID",
            null,
            "password",
            true,
            ConfigTag.SECURITY);
    public static final ConfigModule SECRET_ACCESS_KEY = ConfigModule.of(
            BedrockConfig.class,
            "forage.bedrock.secret.access.key",
            "AWS secret access key (optional, uses default credential chain if not provided)",
            "Secret Access Key",
            null,
            "password",
            true,
            ConfigTag.SECURITY);
    public static final ConfigModule TEMPERATURE = ConfigModule.of(
            BedrockConfig.class,
            "forage.bedrock.temperature",
            "Sampling temperature for response randomness (0.0-1.0)",
            "Temperature",
            null,
            "double",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule MAX_TOKENS = ConfigModule.of(
            BedrockConfig.class,
            "forage.bedrock.max.tokens",
            "Maximum number of tokens to generate",
            "Max Tokens",
            null,
            "integer",
            true,
            ConfigTag.ADVANCED);
    public static final ConfigModule TOP_P = ConfigModule.of(
            BedrockConfig.class,
            "forage.bedrock.top.p",
            "Top-P (nucleus) sampling parameter (0.0-1.0)",
            "Top P",
            null,
            "double",
            true,
            ConfigTag.ADVANCED);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(REGION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MODEL_ID, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ACCESS_KEY_ID, ConfigEntry.fromModule());
        CONFIG_MODULES.put(SECRET_ACCESS_KEY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TEMPERATURE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_TOKENS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TOP_P, ConfigEntry.fromModule());
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
     * @param prefix an optional prefix to use
     */
    public static void loadOverrides(String prefix) {
        load(CONFIG_MODULES, prefix);
    }
}
