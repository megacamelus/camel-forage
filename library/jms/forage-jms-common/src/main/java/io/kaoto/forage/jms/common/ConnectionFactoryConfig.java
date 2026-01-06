package io.kaoto.forage.jms.common;

import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.BLOCK_IF_FULL;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.BLOCK_IF_FULL_TIMEOUT_MILLIS;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.BROKER_URL;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.CLIENT_ID;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.CONNECTION_TIMEOUT_MILLIS;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.EXPIRY_TIMEOUT_MILLIS;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.IDLE_TIMEOUT_MILLIS;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.JMS_KIND;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.MAX_CONNECTIONS;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.MAX_SESSIONS_PER_CONNECTION;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.PASSWORD;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.POOL_ENABLED;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.TRANSACTION_ENABLED;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.TRANSACTION_ENABLE_RECOVERY;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.TRANSACTION_EXPIRY_SCANNERS;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.TRANSACTION_NODE_ID;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.TRANSACTION_OBJECT_STORE_DIRECTORY;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.TRANSACTION_OBJECT_STORE_ID;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.TRANSACTION_OBJECT_STORE_TYPE;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.TRANSACTION_RECOVERY_MODULES;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.TRANSACTION_TIMEOUT_SECONDS;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.TRANSACTION_XA_RESOURCE_ORPHAN_FILTERS;
import static io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries.USERNAME;

import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;
import java.util.Optional;

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
                .get(JMS_KIND.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("JMS kind is required but not configured"));
    }

    public String brokerUrl() {
        return ConfigStore.getInstance()
                .get(BROKER_URL.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Broker URL is required but not configured"));
    }

    public String username() {
        return ConfigStore.getInstance().get(USERNAME.asNamed(prefix)).orElse(null);
    }

    public String password() {
        return ConfigStore.getInstance().get(PASSWORD.asNamed(prefix)).orElse(null);
    }

    public String clientId() {
        return ConfigStore.getInstance().get(CLIENT_ID.asNamed(prefix)).orElse(null);
    }

    // Connection pool configuration methods
    public boolean poolEnabled() {
        return ConfigStore.getInstance()
                .get(POOL_ENABLED.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(Boolean.parseBoolean(POOL_ENABLED.defaultValue()));
    }

    public int maxConnections() {
        return ConfigStore.getInstance()
                .get(MAX_CONNECTIONS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(MAX_CONNECTIONS.defaultValue()));
    }

    public int maxSessionsPerConnection() {
        return ConfigStore.getInstance()
                .get(MAX_SESSIONS_PER_CONNECTION.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(MAX_SESSIONS_PER_CONNECTION.defaultValue()));
    }

    public long idleTimeoutMillis() {
        return ConfigStore.getInstance()
                .get(IDLE_TIMEOUT_MILLIS.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(Long.parseLong(IDLE_TIMEOUT_MILLIS.defaultValue()));
    }

    public long expiryTimeoutMillis() {
        return ConfigStore.getInstance()
                .get(EXPIRY_TIMEOUT_MILLIS.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(Long.parseLong(EXPIRY_TIMEOUT_MILLIS.defaultValue()));
    }

    // todo this property is not used, should we remove it?
    public long connectionTimeoutMillis() {
        return ConfigStore.getInstance()
                .get(CONNECTION_TIMEOUT_MILLIS.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(Long.parseLong(CONNECTION_TIMEOUT_MILLIS.defaultValue()));
    }

    public boolean blockIfFull() {
        return ConfigStore.getInstance()
                .get(BLOCK_IF_FULL.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(Boolean.parseBoolean(BLOCK_IF_FULL.defaultValue()));
    }

    public long blockIfFullTimeoutMillis() {
        return ConfigStore.getInstance()
                .get(BLOCK_IF_FULL_TIMEOUT_MILLIS.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(Long.parseLong(BLOCK_IF_FULL_TIMEOUT_MILLIS.defaultValue()));
    }

    // Transaction configuration methods
    public boolean transactionEnabled() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_ENABLED.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(Boolean.parseBoolean(TRANSACTION_ENABLED.defaultValue()));
    }

    public int transactionTimeoutSeconds() {
        return ConfigStore.getInstance()
                .get(TRANSACTION_TIMEOUT_SECONDS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(Integer.parseInt(TRANSACTION_TIMEOUT_SECONDS.defaultValue()));
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
}
