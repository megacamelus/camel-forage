package org.apache.camel.forage.jdbc;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.AgroalDataSourceConfiguration;
import io.agroal.api.configuration.supplier.AgroalConnectionFactoryConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalConnectionPoolConfigurationSupplier;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;
import io.agroal.api.transaction.TransactionIntegration;
import java.time.Duration;
import javax.sql.DataSource;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for pooled JDBC implementations using Agroal connection pooling.
 * Provides database-agnostic DataSource configuration with optimized pool settings.
 */
public abstract class PooledDataSource implements DataSourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(PooledDataSource.class);

    /**
     * Returns the connection provider class name for the specific database implementation.
     *
     * @return the JDBC driver class name
     */
    protected abstract Class getConnectionProviderClass();

    @Override
    public DataSource create(String id) {
        LOG.info("Creating DataSource with id: {}", id);
        return createPooledDataSource(id);
    }

    /**
     * Creates a pooled DataSource with the given configuration.
     *
     * @param id the configuration ID for logging
     * @return configured pooled DataSource
     * @throws RuntimeException if DataSource creation fails
     */
    protected AgroalDataSource createPooledDataSource(String id) {
        DataSourceFactoryConfig config = new DataSourceFactoryConfig(id);

        LOG.info(
                "DataSource configuration - JDBC URL: {}, Username: {}, Initial Size: {}, Min Size: {}, Max Size: {}, "
                        + "Acquisition Timeout: {}s, Validation Timeout: {}s, Leak Timeout: {}min, Idle Validation Timeout: {}min, "
                        + "Transaction Timeout: {}s",
                config.jdbcUrl(),
                config.username(),
                config.initialSize(),
                config.minSize(),
                config.maxSize(),
                config.acquisitionTimeoutSeconds(),
                config.validationTimeoutSeconds(),
                config.leakTimeoutMinutes(),
                config.idleValidationTimeoutMinutes(),
                config.transactionTimeoutSeconds());

        AgroalDataSourceConfigurationSupplier configSupplier = new AgroalDataSourceConfigurationSupplier();
        configSupplier.metricsEnabled(true);

        // Configure connection factory
        AgroalConnectionFactoryConfigurationSupplier connectionFactoryConfig =
                configSupplier.connectionPoolConfiguration().connectionFactoryConfiguration();

        connectionFactoryConfig
                .jdbcUrl(config.jdbcUrl())
                .connectionProviderClass(getConnectionProviderClass())
                .principal(new NamePrincipal(config.username()))
                .credential(new SimplePassword(config.password()));

        // Configure connection pool settings
        AgroalConnectionPoolConfigurationSupplier poolConfig = configSupplier.connectionPoolConfiguration();

        poolConfig
                .initialSize(config.initialSize())
                .minSize(config.minSize())
                .maxSize(config.maxSize())
                .acquisitionTimeout(Duration.ofSeconds(config.acquisitionTimeoutSeconds()))
                .validationTimeout(Duration.ofSeconds(config.validationTimeoutSeconds()))
                .leakTimeout(Duration.ofMinutes(config.leakTimeoutMinutes()))
                .idleValidationTimeout(Duration.ofMinutes(config.idleValidationTimeoutMinutes()));

        poolConfig.transactionIntegration(TransactionIntegration.none());

        // Build the configuration
        AgroalDataSourceConfiguration dsConfig = configSupplier.get();

        LOG.info("Pooled DataSource initialized successfully for id: {}", id);
        try {
            return AgroalDataSource.from(dsConfig);
        } catch (Exception e) {
            LOG.error("Failed to create DataSource for id: {}", id, e);
            throw new RuntimeException("Failed to create DataSource", e);
        }
    }
}
