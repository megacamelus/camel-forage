package io.kaoto.forage.vectordb.pgvector;

import static io.kaoto.forage.vectordb.pgvector.PgVectorConfigEntries.CREATE_TABLE;
import static io.kaoto.forage.vectordb.pgvector.PgVectorConfigEntries.DATABASE;
import static io.kaoto.forage.vectordb.pgvector.PgVectorConfigEntries.DIMENSION;
import static io.kaoto.forage.vectordb.pgvector.PgVectorConfigEntries.DROP_TABLE_FIRST;
import static io.kaoto.forage.vectordb.pgvector.PgVectorConfigEntries.HOST;
import static io.kaoto.forage.vectordb.pgvector.PgVectorConfigEntries.INDEX_LIST_SIZE;
import static io.kaoto.forage.vectordb.pgvector.PgVectorConfigEntries.PASSWORD;
import static io.kaoto.forage.vectordb.pgvector.PgVectorConfigEntries.PORT;
import static io.kaoto.forage.vectordb.pgvector.PgVectorConfigEntries.TABLE;
import static io.kaoto.forage.vectordb.pgvector.PgVectorConfigEntries.USER;
import static io.kaoto.forage.vectordb.pgvector.PgVectorConfigEntries.USE_INDEX;

import dev.langchain4j.store.embedding.pgvector.DefaultMetadataStorageConfig;
import dev.langchain4j.store.embedding.pgvector.MetadataStorageConfig;
import java.util.Optional;
import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;

public class PgVectorConfig implements Config {

    private final String prefix;

    public PgVectorConfig() {
        this(null);
    }

    public PgVectorConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        PgVectorConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(PgVectorConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        PgVectorConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-vectordb-pgvector";
    }

    public String host() {
        return ConfigStore.getInstance()
                .get(HOST.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing PGVector host"));
    }

    public Integer port() {
        return ConfigStore.getInstance()
                .get(PORT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing PGVector port"));
    }

    public String user() {
        return ConfigStore.getInstance()
                .get(USER.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing PGVector user"));
    }

    public String password() {
        return ConfigStore.getInstance()
                .get(PASSWORD.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing PGVector password"));
    }

    public String database() {
        return ConfigStore.getInstance()
                .get(DATABASE.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing PGVector database"));
    }

    public String table() {
        return ConfigStore.getInstance()
                .get(TABLE.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing PGVector table"));
    }

    public Integer dimension() {
        return ConfigStore.getInstance()
                .get(DIMENSION.asNamed(prefix))
                .map(Integer::parseInt)
                .orElseThrow(() -> new MissingConfigException("Missing PGVector dimension"));
    }

    public Boolean useIndex() {
        return ConfigStore.getInstance()
                .get(USE_INDEX.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public Integer indexListSize() {
        return ConfigStore.getInstance()
                .get(INDEX_LIST_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(100);
    }

    public Boolean createTable() {
        return ConfigStore.getInstance()
                .get(CREATE_TABLE.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    public Boolean dropTableFirst() {
        return ConfigStore.getInstance()
                .get(DROP_TABLE_FIRST.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public MetadataStorageConfig metadataStorageConfig() {
        return DefaultMetadataStorageConfig.defaultConfig();
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = PgVectorConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }
}
