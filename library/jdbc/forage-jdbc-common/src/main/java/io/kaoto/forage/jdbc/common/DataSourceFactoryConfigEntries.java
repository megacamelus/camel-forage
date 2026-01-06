package io.kaoto.forage.jdbc.common;

import io.kaoto.forage.core.util.config.ConfigEntries;
import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigTag;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class DataSourceFactoryConfigEntries extends ConfigEntries {

    // Database connection configuration
    public static final ConfigModule DB_KIND = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.db.kind",
            "The database kind/type",
            "Database Kind",
            null,
            "bean-name",
            true,
            ConfigTag.COMMON);

    public static final ConfigModule JDBC_URL = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.url",
            "The JDBC connection URL",
            "JDBC URL",
            null,
            "string",
            true,
            ConfigTag.COMMON);

    public static final ConfigModule USERNAME = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.username",
            "The database username",
            "Username",
            null,
            "string",
            true,
            ConfigTag.SECURITY);

    public static final ConfigModule PASSWORD = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.password",
            "The database password",
            "Password",
            null,
            "string",
            true,
            ConfigTag.SECURITY);

    // Connection pool configuration
    public static final ConfigModule INITIAL_SIZE = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.pool.initial.size",
            "Initial size of the connection pool",
            "Initial Pool Size",
            "5",
            "integer",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule MIN_SIZE = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.pool.min.size",
            "Minimum size of the connection pool",
            "Min Pool Size",
            "2",
            "integer",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule MAX_SIZE = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.pool.max.size",
            "Maximum size of the connection pool",
            "Max Pool Size",
            "20",
            "integer",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule ACQUISITION_TIMEOUT_SECONDS = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.pool.acquisition.timeout.seconds",
            "Timeout for acquiring a connection from the pool (seconds)",
            "Acquisition Timeout",
            "5",
            "integer",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule VALIDATION_TIMEOUT_SECONDS = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.pool.validation.timeout.seconds",
            "Timeout for validating a connection (seconds)",
            "Validation Timeout",
            "3",
            "integer",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule LEAK_TIMEOUT_MINUTES = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.pool.leak.timeout.minutes",
            "Timeout for detecting connection leaks (minutes)",
            "Leak Timeout",
            "10",
            "integer",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule IDLE_VALIDATION_TIMEOUT_MINUTES = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.pool.idle.validation.timeout.minutes",
            "Timeout for validating idle connections (minutes)",
            "Idle Validation Timeout",
            "3",
            "integer",
            false,
            ConfigTag.ADVANCED);

    // Transaction configuration
    public static final ConfigModule TRANSACTION_ENABLED = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.enabled",
            "Enable transaction management",
            "Transaction Enabled",
            "false",
            "boolean",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule TRANSACTION_TIMEOUT_SECONDS = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.timeout.seconds",
            "Timeout for transactions (seconds)",
            "Transaction Timeout",
            "30",
            "integer",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_NODE_ID = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.node.id",
            "The transaction node identifier",
            "Transaction Node ID",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_OBJECT_STORE_ID = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.object.store.id",
            "The transaction object store identifier",
            "Object Store ID",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_ENABLE_RECOVERY = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.enable.recovery",
            "Enable transaction recovery",
            "Enable Recovery",
            "false",
            "boolean",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_RECOVERY_MODULES = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.recovery.modules",
            "Comma-separated list of transaction recovery modules",
            "Recovery Modules",
            "com.arjuna.ats.internal.arjuna.recovery.AtomicActionRecoveryModule,"
                    + "com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule",
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_EXPIRY_SCANNERS = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.expiry.scanners",
            "Comma-separated list of transaction expiry scanners",
            "Expiry Scanners",
            "com.arjuna.ats.internal.arjuna.recovery.ExpiredTransactionStatusManagerScanner",
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.xa.resource.orphan.filters",
            "Comma-separated list of XA resource orphan filters",
            "XA Orphan Filters",
            "com.arjuna.ats.internal.jta.recovery.arjunacore.JTATransactionLogXAResourceOrphanFilter,"
                    + "com.arjuna.ats.internal.jta.recovery.arjunacore.JTANodeNameXAResourceOrphanFilter,"
                    + "com.arjuna.ats.internal.jta.recovery.arjunacore.JTAActionStatusServiceXAResourceOrphanFilter",
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_OBJECT_STORE_DIRECTORY = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.object.store.directory",
            "Directory for transaction object store",
            "Object Store Directory",
            "ObjectStore",
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_OBJECT_STORE_TYPE = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.object.store.type",
            "Type of transaction object store (file-system or jdbc)",
            "Object Store Type",
            "file-system",
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_OBJECT_STORE_DATASOURCE = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.object.store.datasource",
            "DataSource name for JDBC object store",
            "Object Store DataSource",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_OBJECT_STORE_CREATE_TABLE = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.object.store.create.table",
            "Create object store table if not exists",
            "Create Table",
            "false",
            "boolean",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_OBJECT_STORE_DROP_TABLE = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.object.store.drop.table",
            "Drop object store table on shutdown",
            "Drop Table",
            "false",
            "boolean",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule TRANSACTION_OBJECT_STORE_TABLE_PREFIX = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.transaction.object.store.table.prefix",
            "Prefix for object store tables",
            "Table Prefix",
            "forage_",
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule AGGREGATION_REPOSITORY_NAME = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.aggregation.repository.name",
            "Name of the aggregation repository",
            "Repository Name",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule AGGREGATION_REPOSITORY_HEADERS_TO_STORE = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.aggregation.repository.headers.to.store",
            "Comma-separated list of headers to store",
            "Headers to Store",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule AGGREGATION_REPOSITORY_STORE_BODY = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.aggregation.repository.store.body",
            "Store message body in repository",
            "Store Body",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule AGGREGATION_REPOSITORY_DEAD_LETTER_URI = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.aggregation.repository.dead.letter.uri",
            "Dead letter queue URI for failed aggregations",
            "Dead Letter URI",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule AGGREGATION_REPOSITORY_ALLOW_SERIALIZED_HEADERS = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.aggregation.repository.allow.serialized.headers",
            "Allow serialized headers in repository",
            "Allow Serialized Headers",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule AGGREGATION_REPOSITORY_MAXIMUM_REDELIVERIES = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.aggregation.repository.maximum.redeliveries",
            "Maximum number of redelivery attempts",
            "Max Redeliveries",
            null,
            "integer",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule AGGREGATION_REPOSITORY_USE_RECOVERY = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.aggregation.repository.use.recovery",
            "Enable recovery for aggregation repository",
            "Use Recovery",
            null,
            "boolean",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule AGGREGATION_REPOSITORY_PROPAGATION_BEHAVIOUR_NAME = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.aggregation.repository.propagation.behaviour.name",
            "Transaction propagation behaviour name",
            "Propagation Behaviour",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule AGGREGATION_REPOSITORY_ENABLED = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.aggregation.repository.enabled",
            "Enable aggregation repository (requires transactions to be enabled)",
            "Aggregation Repository Enabled",
            "false",
            "boolean",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule ENABLE_IDEMPOTENT_REPOSITORY = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.idempotent.repository.enabled",
            "Enable idempotent repository",
            "Idempotent Enabled (Transactions are required, please enable transactions too)",
            "false",
            "boolean",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule IDEMPOTENT_REPOSITORY_TABLE_NAME = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.idempotent.repository.table.name",
            "Table name for idempotent repository",
            "Idempotent Table Name",
            null,
            "string",
            false,
            ConfigTag.COMMON);

    public static final ConfigModule IDEMPOTENT_REPOSITORY_TABLE_IF_NOT_EXISTS = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.idempotent.repository.table.create",
            "Create idempotent table if not exists",
            "Create Table",
            "true",
            "boolean",
            false,
            ConfigTag.ADVANCED);

    public static final ConfigModule IDEMPOTENT_REPOSITORY_PROCESSOR_NAME = ConfigModule.of(
            DataSourceFactoryConfig.class,
            "forage.jdbc.idempotent.repository.processor.name",
            "Processor name for idempotent repository",
            "Processor Name",
            null,
            "string",
            false,
            ConfigTag.ADVANCED);

    private static final Map<ConfigModule, ConfigEntry> CONFIG_MODULES = new ConcurrentHashMap<>();

    static {
        init();
    }

    static void init() {
        CONFIG_MODULES.put(DB_KIND, ConfigEntry.fromModule());
        CONFIG_MODULES.put(JDBC_URL, ConfigEntry.fromModule());
        CONFIG_MODULES.put(USERNAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(PASSWORD, ConfigEntry.fromModule());
        CONFIG_MODULES.put(INITIAL_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MIN_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(MAX_SIZE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ACQUISITION_TIMEOUT_SECONDS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(VALIDATION_TIMEOUT_SECONDS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(LEAK_TIMEOUT_MINUTES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(IDLE_VALIDATION_TIMEOUT_MINUTES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_TIMEOUT_SECONDS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_ENABLED, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_NODE_ID, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_ID, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_ENABLE_RECOVERY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_RECOVERY_MODULES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_EXPIRY_SCANNERS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_DIRECTORY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_TYPE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_DATASOURCE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_CREATE_TABLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_DROP_TABLE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(TRANSACTION_OBJECT_STORE_TABLE_PREFIX, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_HEADERS_TO_STORE, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_STORE_BODY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_DEAD_LETTER_URI, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_ALLOW_SERIALIZED_HEADERS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_MAXIMUM_REDELIVERIES, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_USE_RECOVERY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_PROPAGATION_BEHAVIOUR_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(AGGREGATION_REPOSITORY_ENABLED, ConfigEntry.fromModule());
        CONFIG_MODULES.put(ENABLE_IDEMPOTENT_REPOSITORY, ConfigEntry.fromModule());
        CONFIG_MODULES.put(IDEMPOTENT_REPOSITORY_TABLE_NAME, ConfigEntry.fromModule());
        CONFIG_MODULES.put(IDEMPOTENT_REPOSITORY_TABLE_IF_NOT_EXISTS, ConfigEntry.fromModule());
        CONFIG_MODULES.put(IDEMPOTENT_REPOSITORY_PROCESSOR_NAME, ConfigEntry.fromModule());
    }

    public static Map<ConfigModule, ConfigEntry> entries() {
        return Collections.unmodifiableMap(CONFIG_MODULES);
    }

    public static Optional<ConfigModule> find(String prefix, String name) {
        return find(CONFIG_MODULES, prefix, name);
    }

    /**
     * Registers new known configuration if a prefix is provided (otherwise is ignored)
     * @param prefix the prefix to register
     */
    public static void register(String prefix) {
        if (prefix != null) {
            for (Map.Entry<ConfigModule, ConfigEntry> entry : entries().entrySet()) {
                ConfigModule configModule = entry.getKey().asNamed(prefix);
                CONFIG_MODULES.put(configModule, ConfigEntry.fromModule());
            }
        }
    }

    /**
     * Load override configurations (which are defined via environment variables and/or system properties)
     * @param prefix and optional prefix to use
     */
    public static void loadOverrides(String prefix) {
        load(CONFIG_MODULES, prefix);
    }
}
