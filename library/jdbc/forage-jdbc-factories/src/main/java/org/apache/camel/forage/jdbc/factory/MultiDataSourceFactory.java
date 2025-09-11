package org.apache.camel.forage.jdbc.factory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.forage.core.common.ServiceLoaderHelper;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.support.sql.DataSourceFactory;
import org.apache.camel.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public DataSource createDataSource(Endpoint endpoint) throws Exception {
        final String dataSourceId = getDataSourceName(endpoint);

        LOG.info("Creating DataSource for endpoint {} using ID {}", endpoint, dataSourceId);

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

            endpoint.getCamelContext().getRegistry().bind(dataSourceId, dataSource);

            LOG.info("Using dataSource {} for {}", dataSource, dataSourceId);
            dataSources.put(dataSourceId, new DataSourcePair(dsFactoryConfig, dataSource));

            return dataSource;
        } else {
            throw new IllegalArgumentException(String.format(
                    "DataSource '%s' is not defined in multi.datasource.names configuration. "
                            + "Available DataSources: %s",
                    dataSourceId, config.multiDataSourceNames()));
        }
    }

    private String getDataSourceName(Endpoint endpoint) {
        Map<String, Object> parameters;
        try {
            if (endpoint.getEndpointBaseUri().startsWith("jdbc")
                    || endpoint.getEndpointBaseUri().startsWith("spring-jdbc")) {
                URI uri = new URI(endpoint.getEndpointBaseUri());
                String pathPart = URISupport.extractRemainderPath(uri, false);

                return pathPart;
            } else if (endpoint.getEndpointBaseUri().startsWith("sql")) {
                parameters = URISupport.parseParameters(URI.create(endpoint.getEndpointUri()));

                String dataSourceName = Optional.ofNullable((String) parameters.get("dataSource"))
                        .or(() -> Optional.ofNullable((String) parameters.get("dataSourceName")))
                        .orElseThrow(() -> new RuntimeException("dataSource or dataSourceName must be provided"));

                return dataSourceName.replace("#", "");
            } else {
                throw new IllegalArgumentException("Endpoint URI " + endpoint.getEndpointUri()
                        + " is not supported, only sql, jdbc and spring-jdbc are supported");
            }

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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

    //    @Override
    //    public PlatformTransactionManager createTransactionManager(DataSource dataSource) throws Exception {
    //        return new DataSourceTransactionManager(dataSource);
    //    }
    //
    //    public TransactionTemplate createTransactionTemplate(PlatformTransactionManager transactionManager)
    //            throws Exception {
    //        TransactionTemplate template = new TransactionTemplate(transactionManager);
    //        template.setTimeout(30); // Default 30 seconds
    //        return template;
    //    }
}
