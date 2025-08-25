package org.apache.camel.forage.vectordb.qdrant;

import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

public class QdrantConfig implements Config {

    private static final ConfigModule COLLECTION_NAME = ConfigModule.of(QdrantConfig.class, "collection-name");
    private static final ConfigModule HOST = ConfigModule.of(QdrantConfig.class, "host");
    private static final ConfigModule PORT = ConfigModule.of(QdrantConfig.class, "port");
    private static final ConfigModule USE_TLS = ConfigModule.of(QdrantConfig.class, "use-tls");
    private static final ConfigModule PAYLOAD_TEXT_KEY = ConfigModule.of(QdrantConfig.class, "payload-text-key");
    private static final ConfigModule API_KEY = ConfigModule.of(QdrantConfig.class, "api-key");

    public QdrantConfig() {
        ConfigStore.getInstance().add(COLLECTION_NAME, ConfigEntry.fromEnv("QDRANT_COLLECTION_NAME"));
        ConfigStore.getInstance().add(HOST, ConfigEntry.fromEnv("QDRANT_HOST"));
        ConfigStore.getInstance().add(PORT, ConfigEntry.fromEnv("QDRANT_PORT"));
        ConfigStore.getInstance().add(USE_TLS, ConfigEntry.fromEnv("QDRANT_USE_TLS"));
        ConfigStore.getInstance().add(PAYLOAD_TEXT_KEY, ConfigEntry.fromEnv("QDRANT_PAYLOAD_TEXT_KEY"));
        ConfigStore.getInstance().add(API_KEY, ConfigEntry.fromEnv("QDRANT_API_KEY"));
        ConfigStore.getInstance().add(QdrantConfig.class, this);
    }

    @Override
    public String name() {
        return "forage-vectordb-qdrant";
    }

    public String collectionName() {
        return ConfigStore.getInstance()
                .get(COLLECTION_NAME)
                .orElseThrow(() -> new MissingConfigException("Missing Qdrant collection name"));
    }

    public String host() {
        return ConfigStore.getInstance().get(HOST).orElseThrow(() -> new MissingConfigException("Missing Qdrant host"));
    }

    public Integer port() {
        return ConfigStore.getInstance()
                .get(PORT)
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Qdrant port"));
    }

    public Boolean useTls() {
        return ConfigStore.getInstance().get(USE_TLS).map(Boolean::parseBoolean).orElse(false);
    }

    public String payloadTextKey() {
        return ConfigStore.getInstance().get(PAYLOAD_TEXT_KEY).orElse("text_segment");
    }

    public String apiKey() {
        return ConfigStore.getInstance().get(API_KEY).orElse(null);
    }
}
