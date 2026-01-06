package io.kaoto.forage.jdbc.common;

import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.ACQUISITION_TIMEOUT_SECONDS;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_ALLOW_SERIALIZED_HEADERS;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_DEAD_LETTER_URI;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_ENABLED;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_HEADERS_TO_STORE;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_MAXIMUM_REDELIVERIES;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_NAME;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_PROPAGATION_BEHAVIOUR_NAME;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_STORE_BODY;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_USE_RECOVERY;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.DB_KIND;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.ENABLE_IDEMPOTENT_REPOSITORY;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.IDEMPOTENT_REPOSITORY_PROCESSOR_NAME;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.IDEMPOTENT_REPOSITORY_TABLE_IF_NOT_EXISTS;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.IDEMPOTENT_REPOSITORY_TABLE_NAME;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.IDLE_VALIDATION_TIMEOUT_MINUTES;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.INITIAL_SIZE;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.JDBC_URL;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.LEAK_TIMEOUT_MINUTES;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.MAX_SIZE;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.MIN_SIZE;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.PASSWORD;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_ENABLED;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_ENABLE_RECOVERY;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_EXPIRY_SCANNERS;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_NODE_ID;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_CREATE_TABLE;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_DATASOURCE;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_DIRECTORY;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_DROP_TABLE;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_ID;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_TABLE_PREFIX;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_TYPE;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_RECOVERY_MODULES;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_TIMEOUT_SECONDS;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.USERNAME;
import static io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries.VALIDATION_TIMEOUT_SECONDS;

import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;
import io.kaoto.forage.jdbc.common.idempotent.ForageJdbcMessageIdRepository;
import java.util.Optional;

/**
 * Configuration for data source factory with JDBC connection settings and pool parameters.
 */
public class DataSourceFactoryConfig implements Config {

    private final String prefix;

    public DataSourceFactoryConfig() {
        this(null);
    }

    public DataSourceFactoryConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        DataSourceFactoryConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(DataSourceFactoryConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        DataSourceFactoryConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-datasource-factory";
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = DataSourceFactoryConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    // Database connection methods
    public String dbKind() {
        return ConfigStore.getInstance()
                .get(DB_KIND.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Db kind is required but not configured"));
    }

    public String jdbcUrl() {
        return ConfigStore.getInstance()
                .get(JDBC_URL.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("JDBC URL is required but not configured"));
    }

    public String username() {
        return ConfigStore.getInstance()
                .get(USERNAME.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Database username is required but not configured"));
    }

    public String password() {
        return ConfigStore.getInstance()
                .get(PASSWORD.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Database password is required but not configured"));
    }

    // Connection pool configuration methods
    public int initialSize() {
        return ConfigStore.getInstance()
                .get(INITIAL_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(INITIAL_SIZE.defaultValue()));
    }

    public int minSize() {
        return ConfigStore.getInstance()
                .get(MIN_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(MIN_SIZE.defaultValue()));
    }

    public int maxSize() {
        return ConfigStore.getInstance()
                .get(MAX_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(MAX_SIZE.defaultValue()));
    }

    public int acquisitionTimeoutSeconds() {
        return ConfigStore.getInstance()
                .get(ACQUISITION_TIMEOUT_SECONDS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(ACQUISITION_TIMEOUT_SECONDS.defaultValue()));
    }

    public int validationTimeoutSeconds() {
        return ConfigStore.getInstance()
                .get(VALIDATION_TIMEOUT_SECONDS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(VALIDATION_TIMEOUT_SECONDS.defaultValue()));
    }

    public int leakTimeoutMinutes() {
        return ConfigStore.getInstance()
                .get(LEAK_TIMEOUT_MINUTES.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(LEAK_TIMEOUT_MINUTES.defaultValue()));
    }

    public int idleValidationTimeoutMinutes() {
        return ConfigStore.getInstance()
                .get(IDLE_VALIDATION_TIMEOUT_MINUTES.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(IDLE_VALIDATION_TIMEOUT_MINUTES.defaultValue()));
    }

    // Transaction configuration methods
    public int transactionTimeoutSeconds() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_TIMEOUT_SECONDS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(TRANSACTION_TIMEOUT_SECONDS.defaultValue()));
    }

    public boolean transactionEnabled() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_ENABLED.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(Boolean.parseBoolean(TRANSACTION_ENABLED.defaultValue()));
    }

    public String transactionNodeId() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_NODE_ID.asNamed(prefix))
                .orElse(null);
    }

    public String transactionObjectStoreId() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_OBJECT_STORE_ID.asNamed(prefix))
                .orElse(null);
    }

    public boolean transactionEnableRecovery() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_ENABLE_RECOVERY.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(Boolean.parseBoolean(TRANSACTION_ENABLE_RECOVERY.defaultValue()));
    }

    public String transactionRecoveryModules() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_RECOVERY_MODULES.asNamed(prefix))
                .orElse(TRANSACTION_RECOVERY_MODULES.defaultValue());
    }

    public String transactionExpiryScanners() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_EXPIRY_SCANNERS.asNamed(prefix))
                .orElse(TRANSACTION_EXPIRY_SCANNERS.defaultValue());
    }

    public String transactionXaResourceOrphanFilters() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS.asNamed(prefix))
                .orElse(TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS.defaultValue());
    }

    public String transactionObjectStoreDirectory() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_OBJECT_STORE_DIRECTORY.asNamed(prefix))
                .orElse(TRANSACTION_OBJECT_STORE_DIRECTORY.defaultValue());
    }

    public String transactionObjectStoreType() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_OBJECT_STORE_TYPE.asNamed(prefix))
                .orElse(TRANSACTION_OBJECT_STORE_TYPE.defaultValue());
    }

    public String transactionObjectStoreDataSource() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_OBJECT_STORE_DATASOURCE.asNamed(prefix))
                .orElse(null);
    }

    public boolean transactionObjectStoreCreateTable() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_OBJECT_STORE_CREATE_TABLE.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(Boolean.parseBoolean(TRANSACTION_OBJECT_STORE_CREATE_TABLE.defaultValue()));
    }

    public boolean transactionObjectStoreDropTable() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_OBJECT_STORE_DROP_TABLE.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(Boolean.parseBoolean(TRANSACTION_OBJECT_STORE_DROP_TABLE.defaultValue()));
    }

    public String transactionObjectStoreTablePrefix() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_OBJECT_STORE_TABLE_PREFIX.asNamed(prefix))
                .orElse(TRANSACTION_OBJECT_STORE_TABLE_PREFIX.defaultValue());
    }

    public String aggregationRepositoryName() {
        return ConfigStore.getInstance()
                .get(AGGREGATION_REPOSITORY_NAME.asNamed(prefix))
                .orElse(null);
    }

    public String aggregationRepositoryHeadersToStore() {
        return ConfigStore.getInstance()
                .get(AGGREGATION_REPOSITORY_HEADERS_TO_STORE.asNamed(prefix))
                .orElse(null);
    }

    public Boolean aggregationRepositoryStoreBody() {
        return ConfigStore.getInstance()
                .get(AGGREGATION_REPOSITORY_STORE_BODY.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(null);
    }

    public String aggregationRepositoryDeadLetterUri() {
        return ConfigStore.getInstance()
                .get(AGGREGATION_REPOSITORY_DEAD_LETTER_URI.asNamed(prefix))
                .orElse(null);
    }

    public Boolean aggregationRepositoryAllowSerializedHeaders() {
        return ConfigStore.getInstance()
                .get(AGGREGATION_REPOSITORY_ALLOW_SERIALIZED_HEADERS.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(null);
    }

    public Integer aggregationRepositoryMaximumRedeliveries() {
        return ConfigStore.getInstance()
                .get(AGGREGATION_REPOSITORY_MAXIMUM_REDELIVERIES.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(null);
    }

    public Boolean aggregationRepositoryUseRecovery() {
        return ConfigStore.getInstance()
                .get(AGGREGATION_REPOSITORY_USE_RECOVERY.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(null);
    }

    public String aggregationRepositoryPropagationBehaviourName() {
        return ConfigStore.getInstance()
                .get(AGGREGATION_REPOSITORY_PROPAGATION_BEHAVIOUR_NAME.asNamed(prefix))
                .orElse(null);
    }

    public boolean aggregationRepositoryEnabled() {
        return ConfigStore.getInstance()
                .get(AGGREGATION_REPOSITORY_ENABLED.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(Boolean.parseBoolean(AGGREGATION_REPOSITORY_ENABLED.defaultValue()));
    }

    public boolean enableIdempotentRepository() {
        return ConfigStore.getInstance()
                .get(ENABLE_IDEMPOTENT_REPOSITORY.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(Boolean.parseBoolean(ENABLE_IDEMPOTENT_REPOSITORY.defaultValue()));
    }

    public String idempotentRepositoryTableName() {
        return ConfigStore.getInstance()
                .get(IDEMPOTENT_REPOSITORY_TABLE_NAME.asNamed(prefix))
                .orElse(ForageJdbcMessageIdRepository.DEFAULT_TABLENAME);
    }

    public boolean enableIdempotentRepositoryTableCreate() {
        return ConfigStore.getInstance()
                .get(IDEMPOTENT_REPOSITORY_TABLE_IF_NOT_EXISTS.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(Boolean.parseBoolean(IDEMPOTENT_REPOSITORY_TABLE_IF_NOT_EXISTS.defaultValue()));
    }

    public String idempotentRepositoryProcessorName() {
        return ConfigStore.getInstance()
                .get(IDEMPOTENT_REPOSITORY_PROCESSOR_NAME.asNamed(prefix))
                .orElse("FORAGE_PROCESSOR_" + idempotentRepositoryTableName());
    }
}
