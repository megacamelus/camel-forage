package org.apache.camel.forage.jdbc.common;

import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;
import org.apache.camel.forage.jdbc.common.idempotent.ForageJdbcMessageIdRepository;

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
                .get(DataSourceFactoryConfigEntries.DB_KIND.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Db kind is required but not configured"));
    }

    public String jdbcUrl() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.JDBC_URL.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("JDBC URL is required but not configured"));
    }

    public String username() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.USERNAME.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Database username is required but not configured"));
    }

    public String password() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.PASSWORD.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Database password is required but not configured"));
    }

    // Connection pool configuration methods
    public int initialSize() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.INITIAL_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(5);
    }

    public int minSize() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.MIN_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(2);
    }

    public int maxSize() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.MAX_SIZE.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(20);
    }

    public int acquisitionTimeoutSeconds() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.ACQUISITION_TIMEOUT_SECONDS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(5);
    }

    public int validationTimeoutSeconds() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.VALIDATION_TIMEOUT_SECONDS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(3);
    }

    public int leakTimeoutMinutes() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.LEAK_TIMEOUT_MINUTES.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(10);
    }

    public int idleValidationTimeoutMinutes() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.IDLE_VALIDATION_TIMEOUT_MINUTES.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(3);
    }

    // Transaction configuration methods
    public int transactionTimeoutSeconds() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_TIMEOUT_SECONDS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(30);
    }

    public boolean transactionEnabled() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_ENABLED.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public String transactionNodeId() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_NODE_ID.asNamed(prefix))
                .orElse(null);
    }

    public String transactionObjectStoreId() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_ID.asNamed(prefix))
                .orElse(null);
    }

    public boolean transactionEnableRecovery() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_ENABLE_RECOVERY.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public String transactionRecoveryModules() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_RECOVERY_MODULES.asNamed(prefix))
                .orElse("com.arjuna.ats.internal.arjuna.recovery.AtomicActionRecoveryModule,"
                        + "com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule");
    }

    public String transactionExpiryScanners() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_EXPIRY_SCANNERS.asNamed(prefix))
                .orElse("com.arjuna.ats.internal.arjuna.recovery.ExpiredTransactionStatusManagerScanner");
    }

    public String transactionXaResourceOrphanFilters() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS.asNamed(prefix))
                .orElse(
                        "com.arjuna.ats.internal.jta.recovery.arjunacore.JTATransactionLogXAResourceOrphanFilter,"
                                + "com.arjuna.ats.internal.jta.recovery.arjunacore.JTANodeNameXAResourceOrphanFilter,"
                                + "com.arjuna.ats.internal.jta.recovery.arjunacore.JTAActionStatusServiceXAResourceOrphanFilter");
    }

    public String transactionObjectStoreDirectory() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_DIRECTORY.asNamed(prefix))
                .orElse("ObjectStore");
    }

    public String transactionObjectStoreType() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_TYPE.asNamed(prefix))
                .orElse("file-system");
    }

    public String transactionObjectStoreDataSource() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_DATASOURCE.asNamed(prefix))
                .orElse(null);
    }

    public boolean transactionObjectStoreCreateTable() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_CREATE_TABLE.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public boolean transactionObjectStoreDropTable() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_DROP_TABLE.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public String transactionObjectStoreTablePrefix() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.TRANSACTION_OBJECT_STORE_TABLE_PREFIX.asNamed(prefix))
                .orElse("forage_");
    }

    public String aggregationRepositoryName() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_NAME.asNamed(prefix))
                .orElse(null);
    }

    public String aggregationRepositoryHeadersToStore() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_HEADERS_TO_STORE.asNamed(prefix))
                .orElse(null);
    }

    public Boolean aggregationRepositoryStoreBody() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_STORE_BODY.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(null);
    }

    public String aggregationRepositoryDeadLetterUri() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_DEAD_LETTER_URI.asNamed(prefix))
                .orElse(null);
    }

    public Boolean aggregationRepositoryAllowSerializedHeaders() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_ALLOW_SERIALIZED_HEADERS.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(null);
    }

    public Integer aggregationRepositoryMaximumRedeliveries() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_MAXIMUM_REDELIVERIES.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(null);
    }

    public Boolean aggregationRepositoryUseRecovery() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_USE_RECOVERY.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(null);
    }

    public String aggregationRepositoryPropagationBehaviourName() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.AGGREGATION_REPOSITORY_PROPAGATION_BEHAVIOUR_NAME.asNamed(prefix))
                .orElse(null);
    }

    public boolean enableIdempotentRepository() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.ENABLE_IDEMPOTENT_REPOSITORY.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public String idempotentRepositoryTableName() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.IDEMPOTENT_REPOSITORY_TABLE_NAME.asNamed(prefix))
                .orElse(ForageJdbcMessageIdRepository.DEFAULT_TABLENAME);
    }

    public boolean enableIdempotentRepositoryTableCreate() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.IDEMPOTENT_REPOSITORY_TABLE_IF_NOT_EXISTS.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    public String idempotentRepositoryProcessorName() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.IDEMPOTENT_REPOSITORY_TABLE_IF_NOT_EXISTS.asNamed(prefix))
                .orElse("FORAGE_PROCESSOR_" + idempotentRepositoryTableName());
    }
}
