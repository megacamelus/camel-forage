package org.apache.camel.forage.vectordb.weaviate;

import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.API_KEY;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.AVOID_DUPS;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.CONSISTENCY_LEVEL;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.GRPC_PORT;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.HOST;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.METADATA_FIELD_NAME;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.METADATA_KEYS;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.OBJECT_CLASS;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.PORT;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.SCHEME;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.SECURED_GRPC;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.TEXT_FIELD_NAME;
import static org.apache.camel.forage.vectordb.weaviate.WeaviateConfigEntries.USE_GRPC_FOR_INSERTS;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

public class WeaviateConfig implements Config {

    private final String prefix;

    public WeaviateConfig() {
        this(null);
    }

    public WeaviateConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        WeaviateConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(WeaviateConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        WeaviateConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-vectordb-weaviate";
    }

    public String apiKey() {
        return ConfigStore.getInstance().get(API_KEY.asNamed(prefix)).orElse(null);
    }

    public String scheme() {
        return ConfigStore.getInstance()
                .get(SCHEME.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate scheme"));
    }

    public String host() {
        return ConfigStore.getInstance()
                .get(HOST.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate host"));
    }

    public Integer port() {
        return ConfigStore.getInstance()
                .get(PORT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate port"));
    }

    public Boolean useGrpcForInserts() {
        return ConfigStore.getInstance()
                .get(USE_GRPC_FOR_INSERTS.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate use grpc for inserts"));
    }

    public Boolean securedGrpc() {
        return ConfigStore.getInstance()
                .get(SECURED_GRPC.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate secured grpc"));
    }

    public Integer grpcPort() {
        return ConfigStore.getInstance()
                .get(GRPC_PORT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate grpc port"));
    }

    public String objectClass() {
        return ConfigStore.getInstance()
                .get(OBJECT_CLASS.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Weaviate object class"));
    }

    public Boolean avoidDups() {
        return ConfigStore.getInstance()
                .get(AVOID_DUPS.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    public String consistencyLevel() {
        return ConfigStore.getInstance().get(CONSISTENCY_LEVEL.asNamed(prefix)).orElse("ALL");
    }

    public Collection<String> metadataKeys() {
        return ConfigStore.getInstance()
                .get(METADATA_KEYS.asNamed(prefix))
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
    }

    public String textFieldName() {
        return ConfigStore.getInstance().get(TEXT_FIELD_NAME.asNamed(prefix)).orElse("text");
    }

    public String metadataFieldName() {
        return ConfigStore.getInstance()
                .get(METADATA_FIELD_NAME.asNamed(prefix))
                .orElse("_metadata");
    }

    public String toString() {
        String result = String.format(
                "apiKey: %s, scheme %s, host %s, port %s, useGrpcForInserts %s, securedGrpc %s, objectClass %s, avoidDups %s, consistencyLevel %s",
                apiKey(),
                scheme(),
                host(),
                port().toString(),
                useGrpcForInserts().toString(),
                securedGrpc().toString(),
                objectClass(),
                avoidDups().toString(),
                consistencyLevel().toString());
        return result;
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = WeaviateConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }
}
