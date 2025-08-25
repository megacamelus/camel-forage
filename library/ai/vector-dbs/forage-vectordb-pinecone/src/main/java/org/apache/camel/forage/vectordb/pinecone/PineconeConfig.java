package org.apache.camel.forage.vectordb.pinecone;

import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;
import org.openapitools.db_control.client.model.DeletionProtection;

public class PineconeConfig implements Config {

    private static final ConfigModule API_KEY = ConfigModule.of(PineconeConfig.class, "api-key");
    private static final ConfigModule INDEX = ConfigModule.of(PineconeConfig.class, "index");
    private static final ConfigModule NAME_SPACE = ConfigModule.of(PineconeConfig.class, "name-space");
    private static final ConfigModule METADATA_TEXT_KEY = ConfigModule.of(PineconeConfig.class, "metadata-text-key");
    private static final ConfigModule CREATE_INDEX = ConfigModule.of(PineconeConfig.class, "create-index");
    private static final ConfigModule ENVIRONMENT = ConfigModule.of(PineconeConfig.class, "environment");
    private static final ConfigModule PROJECT_ID = ConfigModule.of(PineconeConfig.class, "project-id");
    private static final ConfigModule DIMENSION = ConfigModule.of(PineconeConfig.class, "dimension");
    private static final ConfigModule CLOUD = ConfigModule.of(PineconeConfig.class, "cloud");
    private static final ConfigModule REGION = ConfigModule.of(PineconeConfig.class, "region");
    private static final ConfigModule DELETION_PROTECTION =
            ConfigModule.of(PineconeConfig.class, "deletion-protection");

    public PineconeConfig() {
        ConfigStore.getInstance().add(API_KEY, ConfigEntry.fromEnv("PINECONE_API_KEY"));
        ConfigStore.getInstance().add(INDEX, ConfigEntry.fromEnv("PINECONE_INDEX"));
        ConfigStore.getInstance().add(NAME_SPACE, ConfigEntry.fromEnv("PINECONE_NAME_SPACE"));
        ConfigStore.getInstance().add(METADATA_TEXT_KEY, ConfigEntry.fromEnv("PINECONE_METADATA_TEXT_KEY"));
        ConfigStore.getInstance().add(CREATE_INDEX, ConfigEntry.fromEnv("PINECONE_CREATE_INDEX"));
        ConfigStore.getInstance().add(ENVIRONMENT, ConfigEntry.fromEnv("PINECONE_ENVIRONMENT"));
        ConfigStore.getInstance().add(PROJECT_ID, ConfigEntry.fromEnv("PINECONE_PROJECT_ID"));
        ConfigStore.getInstance().add(DIMENSION, ConfigEntry.fromEnv("PINECONE_DIMENSION"));
        ConfigStore.getInstance().add(CLOUD, ConfigEntry.fromEnv("PINECONE_CLOUD"));
        ConfigStore.getInstance().add(REGION, ConfigEntry.fromEnv("PINECONE_REGION"));
        ConfigStore.getInstance().add(DELETION_PROTECTION, ConfigEntry.fromEnv("PINECONE_DELETION_PROTECTION"));
        ConfigStore.getInstance().add(PineconeConfig.class, this);
    }

    @Override
    public String name() {
        return "forage-vectordb-pinecone";
    }

    public String apiKey() {
        return ConfigStore.getInstance()
                .get(API_KEY)
                .orElseThrow(() -> new MissingConfigException("Missing Google API key"));
    }

    public String index() {
        return ConfigStore.getInstance()
                .get(INDEX)
                .orElseThrow(() -> new MissingConfigException("Missing Pinecone index"));
    }

    public String nameSpace() {
        return ConfigStore.getInstance().get(NAME_SPACE).orElse("default");
    }

    public String metadataTextKey() {
        return ConfigStore.getInstance().get(METADATA_TEXT_KEY).orElse("text_segment");
    }

    public String environment() {
        return ConfigStore.getInstance()
                .get(ENVIRONMENT)
                .orElseThrow(() -> new MissingConfigException("Missing Pinecone environment"));
    }

    public String projectId() {
        return ConfigStore.getInstance()
                .get(PROJECT_ID)
                .orElseThrow(() -> new MissingConfigException("Missing Pinecone project ID"));
    }

    public Integer dimension() {
        return ConfigStore.getInstance()
                .get(DIMENSION)
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Pinecone dimension"));
    }

    public String cloud() {
        return ConfigStore.getInstance()
                .get(CLOUD)
                .orElseThrow(() -> new MissingConfigException("Missing Pinecone cloud"));
    }

    public String region() {
        return ConfigStore.getInstance()
                .get(REGION)
                .orElseThrow(() -> new MissingConfigException("Missing Pinecone region"));
    }

    public DeletionProtection deletionProtection() {
        return ConfigStore.getInstance()
                .get(DELETION_PROTECTION)
                .map(DeletionProtection::valueOf)
                .orElse(DeletionProtection.ENABLED);
    }
}
