package io.kaoto.forage.vectordb.infinispan;

import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigTag;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InfinispanConfigEntries extends ConfigEntries {
    public static final ConfigModule CACHE_NAME = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.cache.name",
            "Name of the Infinispan cache",
            "Cache Name",
            null,
            "string",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule DIMENSION = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.dimension",
            "Vector dimension for embeddings",
            "Dimension",
            null,
            "integer",
            true,
            ConfigTag.COMMON);
    public static final ConfigModule DISTANCE = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.distance",
            "Distance metric for similarity (3 for cosine)",
            "Distance",
            "3",
            "integer",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule SIMILARITY = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.similarity",
            "Similarity algorithm",
            "Similarity",
            "COSINE",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule CACHE_CONFIG = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.cache.config",
            "Cache configuration settings",
            "Cache Config",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule PACKAGE_NAME = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.package.name",
            "Package name for generated classes",
            "Package Name",
            "io.kaoto.forage.vectordb.infinispan.schema",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule FILE_NAME = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.file.name",
            "Schema file name",
            "File Name",
            "langchain-item.proto",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule LANGCHAIN_ITEM_NAME = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.langchain.item.name",
            "LangChain item class name",
            "LangChain Item Name",
            "LangChainItem",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule METADATA_ITEM_NAME = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.metadata.item.name",
            "Metadata item class name",
            "Metadata Item Name",
            "MetadataItem",
            "string",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule REGISTER_SCHEMA = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.register.schema",
            "Whether to register schema automatically",
            "Register Schema",
            "true",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule CREATE_CACHE = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.create.cache",
            "Whether to create cache if it doesn't exist",
            "Create Cache",
            "true",
            "boolean",
            false,
            ConfigTag.ADVANCED);
    public static final ConfigModule HOST = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.host",
            "Infinispan server host address",
            "Host",
            "localhost",
            "string",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule PORT = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.port",
            "Infinispan server port number",
            "Port",
            "11222",
            "integer",
            false,
            ConfigTag.COMMON);
    public static final ConfigModule USERNAME = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.username",
            "Username for authentication",
            "Username",
            null,
            "string",
            false,
            ConfigTag.SECURITY);
    public static final ConfigModule PASSWORD = ConfigModule.of(
            InfinispanConfig.class,
            "forage.infinispan.password",
            "Password for authentication",
            "Password",
            null,
            "password",
            false,
            ConfigTag.SECURITY);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(CACHE_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DIMENSION, ConfigEntry.fromModule());
        CONFIG_MODULES.put(DISTANCE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(SIMILARITY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CACHE_CONFIG, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PACKAGE_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(FILE_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(LANGCHAIN_ITEM_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(METADATA_ITEM_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(REGISTER_SCHEMA, ConfigEntry.fromModule());
        CONFIG_MODULES.put(CREATE_CACHE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(HOST, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PORT, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USERNAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PASSWORD, ConfigEntry.fromModule());
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
