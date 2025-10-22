package org.apache.camel.forage.vectordb.weaviate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.camel.forage.core.util.config.ConfigEntries;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigTag;

public final class WeaviateConfigEntries extends ConfigEntries {
    public static final ConfigModule API_KEY = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.api.key",
            "API key for authentication",
            "API Key",
            null,
            "password",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule SCHEME = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.scheme",
            "Connection scheme (http or https)",
            "Scheme",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule HOST = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.host",
            "Weaviate server host address",
            "Host",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule PORT = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.port",
            "Weaviate server port number",
            "Port",
            null,
            "integer",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule USE_GRPC_FOR_INSERTS = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.use.grpc.for.inserts",
            "Use gRPC protocol for insert operations",
            "Use gRPC for Inserts",
            null,
            "boolean",
            true,
            ConfigTag.ADVANCED);
    public static final ConfigModule SECURED_GRPC = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.secured.grpc",
            "Enable secured gRPC connections",
            "Secured gRPC",
            null,
            "boolean",
            true,
            ConfigTag.SECURITY);
    public static final ConfigModule GRPC_PORT = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.grpc.port",
            "gRPC server port number",
            "gRPC Port",
            null,
            "integer",
            true,
            ConfigTag.ADVANCED);
    public static final ConfigModule OBJECT_CLASS = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.object.class",
            "Weaviate object class name",
            "Object Class",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule AVOID_DUPS = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.avoid.dups",
            "Avoid duplicate entries",
            "Avoid Duplicates",
            "true",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule CONSISTENCY_LEVEL = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.consistency.level",
            "Consistency level for operations",
            "Consistency Level",
            "ALL",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule METADATA_KEYS = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.metadata.keys",
            "Comma-separated list of metadata keys",
            "Metadata Keys",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule TEXT_FIELD_NAME = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.text.field.name",
            "Name of the text field",
            "Text Field Name",
            "text",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule METADATA_FIELD_NAME = ConfigModule.of(
            WeaviateConfig.class,
            "weaviate.metadata.field.name",
            "Name of the metadata field",
            "Metadata Field Name",
            "_metadata",
            "string",
            false,
            ConfigTag.ADVANCED);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(API_KEY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(SCHEME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(HOST, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PORT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USE_GRPC_FOR_INSERTS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(SECURED_GRPC, ConfigEntry.fromModule());
        CONFIG_MODULES.put(GRPC_PORT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(OBJECT_CLASS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AVOID_DUPS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CONSISTENCY_LEVEL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(METADATA_KEYS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TEXT_FIELD_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(METADATA_FIELD_NAME, ConfigEntry.fromModule());
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
