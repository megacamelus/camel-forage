package org.apache.camel.forage.vectordb.pinecone;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigTag;

public final class PineconeConfigEntries extends ConfigEntries {
    public static final ConfigModule API_KEY = ConfigModule.of(
            PineconeConfig.class,
            "forage.pinecone.api.key",
            "Pinecone API key for authentication",
            "API Key",
            null,
            "password",
            true,
            ConfigTag.SECURITY);
    public static final ConfigModule INDEX = ConfigModule.of(
            PineconeConfig.class,
            "forage.pinecone.index",
            "Name of the Pinecone index",
            "Index",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule NAME_SPACE = ConfigModule.of(
            PineconeConfig.class,
            "forage.pinecone.name.space",
            "Namespace within the index",
            "Namespace",
            "default",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule METADATA_TEXT_KEY = ConfigModule.of(
            PineconeConfig.class,
            "forage.pinecone.metadata.text.key",
            "Metadata key for storing text content",
            "Metadata Text Key",
            "text_segment",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule CREATE_INDEX = ConfigModule.of(
            PineconeConfig.class,
            "forage.pinecone.create.index",
            "Create index if it does not exist",
            "Create Index",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule ENVIRONMENT = ConfigModule.of(
            PineconeConfig.class,
            "forage.pinecone.environment",
            "Pinecone environment (deprecated)",
            "Environment",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule PROJECT_ID = ConfigModule.of(
            PineconeConfig.class,
            "forage.pinecone.project.id",
            "Pinecone project ID (deprecated)",
            "Project ID",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule DIMENSION = ConfigModule.of(
            PineconeConfig.class,
            "forage.pinecone.dimension",
            "Vector dimension size",
            "Dimension",
            null,
            "integer",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule CLOUD = ConfigModule.of(
            PineconeConfig.class,
            "forage.pinecone.cloud",
            "Cloud provider (e.g., aws, gcp, azure)",
            "Cloud",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule REGION = ConfigModule.of(
            PineconeConfig.class,
            "forage.pinecone.region",
            "Cloud region for the index",
            "Region",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule DELETION_PROTECTION = ConfigModule.of(
            PineconeConfig.class,
            "forage.pinecone.deletion.protection",
            "Enable deletion protection (ENABLED or DISABLED)",
            "Deletion Protection",
            "ENABLED",
            "string",
            false,
            ConfigTag.ADVANCED);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(API_KEY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(INDEX, ConfigEntry.fromModule());
        CONFIG_MODULES.put(NAME_SPACE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(METADATA_TEXT_KEY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CREATE_INDEX, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ENVIRONMENT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PROJECT_ID, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DIMENSION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CLOUD, ConfigEntry.fromModule());
        CONFIG_MODULES.put(REGION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DELETION_PROTECTION, ConfigEntry.fromModule());
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
