package org.apache.camel.forage.jdbc.factory;

import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

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

    // Provider configuration method
    public String providerDataSourceClass() {
        return ConfigStore.getInstance()
                .get(DataSourceFactoryConfigEntries.PROVIDER_DATASOURCE_CLASS.asNamed(prefix))
                .orElseThrow(
                        () -> new IllegalStateException("Provider datasource class is required but not configured"));
    }
}
