package org.apache.camel.forage.jdbc.factory;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.sql.DataSourceFactory;
import org.apache.camel.forage.core.common.ServiceLoaderHelper;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Multi-DataSource factory that uses ServiceLoader to discover and create multiple DataSource providers.
 * Supports multiple named DataSource instances with different database types and configurations.
 */
public class MultiDataSourceFactory implements DataSourceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MultiDataSourceFactory.class);

    private CamelContext camelContext;
    private final MultiDataSourceConfig config = new MultiDataSourceConfig();

    private record DataSourcePair(DataSourceFactoryConfig dataSourceFactoryConfig, DataSource dataSource) {}

    private Map<String, DataSourcePair> dataSources = new ConcurrentHashMap<>();

    public MultiDataSourceFactory() {
        LOG.trace("Creating MultiDataSourceFactory");
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;

        ConfigStore.getInstance().setClassLoader(camelContext.getApplicationContextClassLoader());
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }

    private List<ServiceLoader.Provider<DataSourceProvider>> findDataSourceProviders() {
        ServiceLoader<DataSourceProvider> serviceLoader =
                ServiceLoader.load(DataSourceProvider.class, camelContext.getApplicationContextClassLoader());

        return serviceLoader.stream().toList();
    }

    @Override
    public DataSource createDataSource(Exchange exchange) throws Exception {
        DataSourceIdSelector dataSourceIdSource = DataSourceIdSourceFactory.create(config);
        final String dataSourceId = dataSourceIdSource.select(exchange);

        LOG.info("Creating DataSource for {} using ID {}", exchange.getExchangeId(), dataSourceId);

        if (LOG.isTraceEnabled()) {
            LOG.debug("Available dataSources: {}", dataSources);
        }

        if (dataSources.containsKey(dataSourceId)) {
            LOG.debug("Reusing existing DataSource for {}", dataSourceId);
            final DataSourcePair dataSourcePair = dataSources.get(dataSourceId);
            return dataSourcePair.dataSource;
        }

        final List<String> definedDataSources = config.multiDataSourceNames();

        if (definedDataSources.contains(dataSourceId)) {
            LOG.info("Creating new DataSource for {}", dataSourceId);
            DataSourceFactoryConfig dsFactoryConfig = new DataSourceFactoryConfig(dataSourceId);

            LOG.info("Using factory {} for {}", dsFactoryConfig.name(), dataSourceId);

            DataSource dataSource = newDataSource(dsFactoryConfig, dataSourceId);

            LOG.info("Using dataSource {} for {}", dataSource, dataSourceId);
            dataSources.put(dataSourceId, new DataSourcePair(dsFactoryConfig, dataSource));

            return dataSource;
        }

        throw DataSourceIdSourceFactory.newUndefinedDataSourceException(config, exchange);
    }

    private synchronized DataSource newDataSource(DataSourceFactoryConfig dataSourceFactoryConfig, String name) {
        final String dataSourceProviderClass = dataSourceFactoryConfig.providerDataSourceClass();
        LOG.info("Creating DataSource of type {}", dataSourceProviderClass);

        final List<ServiceLoader.Provider<DataSourceProvider>> providers = findDataSourceProviders();

        final ServiceLoader.Provider<DataSourceProvider> dataSourceProvider =
                ServiceLoaderHelper.findProviderByClassName(providers, dataSourceProviderClass);

        if (dataSourceProvider == null) {
            LOG.warn("DataSource {} has no provider for {}", name, dataSourceProviderClass);
            return null;
        }

        return doCreateDataSource(dataSourceProvider, name);
    }

    private DataSource doCreateDataSource(ServiceLoader.Provider<DataSourceProvider> provider, String name) {
        final DataSourceProvider dataSourceProvider = provider.get();
        return dataSourceProvider.create(name);
    }

    @Override
    public PlatformTransactionManager createTransactionManager(DataSource dataSource) throws Exception {
        return new DataSourceTransactionManager(dataSource);
    }

    public TransactionTemplate createTransactionTemplate(PlatformTransactionManager transactionManager)
            throws Exception {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setTimeout(30); // Default 30 seconds
        return template;
    }
}
