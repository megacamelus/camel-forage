package org.apache.camel.forage.jms.common;

import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

/**
 * Configuration for JMS connection factory with broker connection settings and pool parameters.
 */
public class ConnectionFactoryConfig implements Config {

    private final String prefix;

    public ConnectionFactoryConfig() {
        this(null);
    }

    public ConnectionFactoryConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        ConnectionFactoryConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(ConnectionFactoryConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        ConnectionFactoryConfigEntries.loadOverrides(prefix);
    }

    @Override
    public String name() {
        return "forage-connectionfactory";
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = ConnectionFactoryConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    // JMS connection methods
    public String jmsKind() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.JMS_KIND.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("JMS kind is required but not configured"));
    }

    public String brokerUrl() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.BROKER_URL.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Broker URL is required but not configured"));
    }

    public String username() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.USERNAME.asNamed(prefix))
                .orElse(null);
    }

    public String password() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.PASSWORD.asNamed(prefix))
                .orElse(null);
    }

    public String clientId() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.CLIENT_ID.asNamed(prefix))
                .orElse(null);
    }

    // Connection pool configuration methods
    public boolean poolEnabled() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.POOL_ENABLED.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    public int maxConnections() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.MAX_CONNECTIONS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(10);
    }

    public int maxSessionsPerConnection() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.MAX_SESSIONS_PER_CONNECTION.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(500);
    }

    public long idleTimeoutMillis() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.IDLE_TIMEOUT_MILLIS.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(30000L);
    }

    public long expiryTimeoutMillis() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.EXPIRY_TIMEOUT_MILLIS.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(0L);
    }

    public long connectionTimeoutMillis() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.CONNECTION_TIMEOUT_MILLIS.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(30000L);
    }

    public boolean blockIfFull() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.BLOCK_IF_FULL.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    public long blockIfFullTimeoutMillis() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.BLOCK_IF_FULL_TIMEOUT_MILLIS.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(-1L);
    }

    // Transaction configuration methods
    public boolean transactionEnabled() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.TRANSACTION_ENABLED.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public int transactionTimeoutSeconds() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.TRANSACTION_TIMEOUT_SECONDS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(30);
    }

    public String transactionNodeId() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.TRANSACTION_NODE_ID.asNamed(prefix))
                .orElse(null);
    }

    public String transactionObjectStoreId() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.TRANSACTION_OBJECT_STORE_ID.asNamed(prefix))
                .orElse(null);
    }

    public boolean transactionEnableRecovery() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.TRANSACTION_ENABLE_RECOVERY.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public String transactionRecoveryModules() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.TRANSACTION_RECOVERY_MODULES.asNamed(prefix))
                .orElse("com.arjuna.ats.internal.arjuna.recovery.AtomicActionRecoveryModule,"
                        + "com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule");
    }

    public String transactionExpiryScanners() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.TRANSACTION_EXPIRY_SCANNERS.asNamed(prefix))
                .orElse("com.arjuna.ats.internal.arjuna.recovery.ExpiredTransactionStatusManagerScanner");
    }

    public String transactionXaResourceOrphanFilters() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS.asNamed(prefix))
                .orElse(
                        "com.arjuna.ats.internal.jta.recovery.arjunacore.JTATransactionLogXAResourceOrphanFilter,"
                                + "com.arjuna.ats.internal.jta.recovery.arjunacore.JTANodeNameXAResourceOrphanFilter,"
                                + "com.arjuna.ats.internal.jta.recovery.arjunacore.JTAActionStatusServiceXAResourceOrphanFilter");
    }

    public String transactionObjectStoreDirectory() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.TRANSACTION_OBJECT_STORE_DIRECTORY.asNamed(prefix))
                .orElse("ObjectStore");
    }

    public String transactionObjectStoreType() {
        return ConfigStore.getInstance()
                .get(ConnectionFactoryConfigEntries.TRANSACTION_OBJECT_STORE_TYPE.asNamed(prefix))
                .orElse("file-system");
    }
}
