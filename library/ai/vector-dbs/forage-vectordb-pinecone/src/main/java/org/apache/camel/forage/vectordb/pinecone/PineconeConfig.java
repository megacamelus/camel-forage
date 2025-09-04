package org.apache.camel.forage.vectordb.pinecone;

import static org.apache.camel.forage.vectordb.pinecone.PineconeConfigEntries.API_KEY;
import static org.apache.camel.forage.vectordb.pinecone.PineconeConfigEntries.CLOUD;
import static org.apache.camel.forage.vectordb.pinecone.PineconeConfigEntries.DELETION_PROTECTION;
import static org.apache.camel.forage.vectordb.pinecone.PineconeConfigEntries.DIMENSION;
import static org.apache.camel.forage.vectordb.pinecone.PineconeConfigEntries.INDEX;
import static org.apache.camel.forage.vectordb.pinecone.PineconeConfigEntries.METADATA_TEXT_KEY;
import static org.apache.camel.forage.vectordb.pinecone.PineconeConfigEntries.NAME_SPACE;
import static org.apache.camel.forage.vectordb.pinecone.PineconeConfigEntries.REGION;

import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;
import org.openapitools.db_control.client.model.DeletionProtection;

public class PineconeConfig implements Config {

    private final String prefix;

    public PineconeConfig() {
        this(null);
    }

    public PineconeConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        PineconeConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(PineconeConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        PineconeConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-vectordb-pinecone";
    }

    public String apiKey() {
        return ConfigStore.getInstance()
                .get(API_KEY.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Pinecone API key"));
    }

    public String index() {
        return ConfigStore.getInstance()
                .get(INDEX.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Pinecone index"));
    }

    public String nameSpace() {
        return ConfigStore.getInstance().get(NAME_SPACE.asNamed(prefix)).orElse("default");
    }

    public String metadataTextKey() {
        return ConfigStore.getInstance().get(METADATA_TEXT_KEY.asNamed(prefix)).orElse("text_segment");
    }

    public Integer dimension() {
        return ConfigStore.getInstance()
                .get(DIMENSION.asNamed(prefix))
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Pinecone dimension"));
    }

    public String cloud() {
        return ConfigStore.getInstance()
                .get(CLOUD.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Pinecone cloud"));
    }

    public String region() {
        return ConfigStore.getInstance()
                .get(REGION.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Pinecone region"));
    }

    public DeletionProtection deletionProtection() {
        return ConfigStore.getInstance()
                .get(DELETION_PROTECTION.asNamed(prefix))
                .map(DeletionProtection::valueOf)
                .orElse(DeletionProtection.ENABLED);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = PineconeConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }
}
