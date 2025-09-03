package org.apache.camel.forage.vectordb.qdrant;

import static org.apache.camel.forage.vectordb.qdrant.QdrantConfigEntries.API_KEY;
import static org.apache.camel.forage.vectordb.qdrant.QdrantConfigEntries.COLLECTION_NAME;
import static org.apache.camel.forage.vectordb.qdrant.QdrantConfigEntries.HOST;
import static org.apache.camel.forage.vectordb.qdrant.QdrantConfigEntries.PAYLOAD_TEXT_KEY;
import static org.apache.camel.forage.vectordb.qdrant.QdrantConfigEntries.PORT;
import static org.apache.camel.forage.vectordb.qdrant.QdrantConfigEntries.USE_TLS;

import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

public class QdrantConfig implements Config {

    private final String prefix;

    public QdrantConfig() {
        this(null);
    }

    public QdrantConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        QdrantConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(QdrantConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        QdrantConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-vectordb-qdrant";
    }

    public String collectionName() {
        return ConfigStore.getInstance()
                .get(COLLECTION_NAME.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Qdrant collection name"));
    }

    public String host() {
        return ConfigStore.getInstance()
                .get(HOST.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Qdrant host"));
    }

    public Integer port() {
        return ConfigStore.getInstance()
                .get(PORT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Qdrant port"));
    }

    public Boolean useTls() {
        return ConfigStore.getInstance()
                .get(USE_TLS.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public String payloadTextKey() {
        return ConfigStore.getInstance().get(PAYLOAD_TEXT_KEY.asNamed(prefix)).orElse("text_segment");
    }

    public String apiKey() {
        return ConfigStore.getInstance().get(API_KEY.asNamed(prefix)).orElse(null);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = QdrantConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }
}
