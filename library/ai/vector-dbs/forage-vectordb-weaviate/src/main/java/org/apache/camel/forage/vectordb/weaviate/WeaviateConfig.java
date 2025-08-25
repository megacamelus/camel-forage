package org.apache.camel.forage.vectordb.weaviate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

public class WeaviateConfig implements Config {

    private static final ConfigModule API_KEY = ConfigModule.of(WeaviateConfig.class, "api-key");
    private static final ConfigModule SCHEME = ConfigModule.of(WeaviateConfig.class, "scheme");
    private static final ConfigModule HOST = ConfigModule.of(WeaviateConfig.class, "host");
    private static final ConfigModule PORT = ConfigModule.of(WeaviateConfig.class, "port");
    private static final ConfigModule USE_GRPC_FOR_INSERTS =
            ConfigModule.of(WeaviateConfig.class, "use-grpc-for-inserts");
    private static final ConfigModule SECURED_GRPC = ConfigModule.of(WeaviateConfig.class, "secured-grpc");
    private static final ConfigModule GRPC_PORT = ConfigModule.of(WeaviateConfig.class, "grpc-port");
    private static final ConfigModule OBJECT_CLASS = ConfigModule.of(WeaviateConfig.class, "object-class");
    private static final ConfigModule AVOID_DUPS = ConfigModule.of(WeaviateConfig.class, "avoid-dups");
    private static final ConfigModule CONSISTENCY_LEVEL = ConfigModule.of(WeaviateConfig.class, "consistency-level");
    private static final ConfigModule METADATA_KEYS = ConfigModule.of(WeaviateConfig.class, "metadata-keys");
    private static final ConfigModule TEXT_FIELD_NAME = ConfigModule.of(WeaviateConfig.class, "text-field-name");
    private static final ConfigModule METADATA_FIELD_NAME =
            ConfigModule.of(WeaviateConfig.class, "metadata-field-name");

    public WeaviateConfig() {
        ConfigStore.getInstance().add(API_KEY, ConfigEntry.fromEnv("WEAVIATE_API_KEY"));
        ConfigStore.getInstance().add(SCHEME, ConfigEntry.fromEnv("WEAVIATE_SCHEME"));
        ConfigStore.getInstance().add(HOST, ConfigEntry.fromEnv("WEAVIATE_HOST"));
        ConfigStore.getInstance().add(PORT, ConfigEntry.fromEnv("WEAVIATE_PORT"));
        ConfigStore.getInstance().add(USE_GRPC_FOR_INSERTS, ConfigEntry.fromEnv("WEAVIATE_USE_GRPC_FOR_INSERTS"));
        ConfigStore.getInstance().add(SECURED_GRPC, ConfigEntry.fromEnv("WEAVIATE_SECURED_GRPC"));
        ConfigStore.getInstance().add(GRPC_PORT, ConfigEntry.fromEnv("WEAVIATE_GRPC_PORT"));
        ConfigStore.getInstance().add(OBJECT_CLASS, ConfigEntry.fromEnv("WEAVIATE_OBJECT_CLASS"));
        ConfigStore.getInstance().add(AVOID_DUPS, ConfigEntry.fromEnv("WEAVIATE_AVOID_DUPS"));
        ConfigStore.getInstance().add(CONSISTENCY_LEVEL, ConfigEntry.fromEnv("WEAVIATE_CONSISTENCY_LEVEL"));
        ConfigStore.getInstance().add(METADATA_KEYS, ConfigEntry.fromEnv("WEAVIATE_METADATA_KEYS"));
        ConfigStore.getInstance().add(TEXT_FIELD_NAME, ConfigEntry.fromEnv("WEAVIATE_TEXT_FIELD_NAME"));
        ConfigStore.getInstance().add(METADATA_FIELD_NAME, ConfigEntry.fromEnv("WEAVIATE_METADATA_FIELD_NAME"));
        ConfigStore.getInstance().add(WeaviateConfig.class, this);
    }

    @Override
    public String name() {
        return "forage-vectordb-weaviate";
    }

    public String apiKey() {
        return ConfigStore.getInstance()
                .get(API_KEY)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate API key"));
    }

    public String scheme() {
        return ConfigStore.getInstance()
                .get(SCHEME)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate scheme"));
    }

    public String host() {
        return ConfigStore.getInstance()
                .get(HOST)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate host"));
    }

    public Integer port() {
        return ConfigStore.getInstance()
                .get(PORT)
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate port"));
    }

    public Boolean useGrpcForInserts() {
        return ConfigStore.getInstance()
                .get(USE_GRPC_FOR_INSERTS)
                .map(Boolean::parseBoolean)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate use grpc for inserts"));
    }

    public Boolean securedGrpc() {
        return ConfigStore.getInstance()
                .get(SECURED_GRPC)
                .map(Boolean::parseBoolean)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate secured grpc"));
    }

    public Integer grpcPort() {
        return ConfigStore.getInstance()
                .get(GRPC_PORT)
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate grpc port"));
    }

    public String objectClass() {
        return ConfigStore.getInstance()
                .get(OBJECT_CLASS)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate object class"));
    }

    public Boolean avoidDups() {
        return ConfigStore.getInstance()
                .get(AVOID_DUPS)
                .map(Boolean::parseBoolean)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate avoid dups"));
    }

    public String consistencyLevel() {
        return ConfigStore.getInstance()
                .get(CONSISTENCY_LEVEL)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate consistency level"));
    }

    public Collection<String> metadataKeys() {
        return ConfigStore.getInstance().get(METADATA_KEYS).map(Arrays::asList).orElse(Collections.emptyList());
    }

    public String textFieldName() {
        return ConfigStore.getInstance().get(TEXT_FIELD_NAME).orElse("text");
    }

    public String metadataFieldName() {
        return ConfigStore.getInstance().get(METADATA_FIELD_NAME).orElse("_metadata");
    }
}
