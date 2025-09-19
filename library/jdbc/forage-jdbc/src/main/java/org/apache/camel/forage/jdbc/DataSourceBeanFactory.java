package org.apache.camel.forage.jdbc;

import java.util.List;
import java.util.ServiceLoader;
import javax.sql.DataSource;
import org.apache.camel.CamelContext;
import org.apache.camel.forage.core.annotations.ForageFactory;
import org.apache.camel.forage.core.common.BeanFactory;
import org.apache.camel.forage.core.common.ServiceLoaderHelper;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.forage.jdbc.common.MultiDataSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ForageFactory(
        value = "CamelDataSourceFactory",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "Default DataSource factory with ServiceLoader discovery",
        factoryType = "DataSource",
        autowired = true)
public class DataSourceBeanFactory implements BeanFactory {
    private final Logger LOG = LoggerFactory.getLogger(DataSourceBeanFactory.class);

    private CamelContext camelContext;
    private final MultiDataSourceConfig config = new MultiDataSourceConfig();
    private static final String DEFAULT_DATASOURCE = "dataSource";

    @Override
    public void configure() {
        if (config != null
                && config.multiDataSourceNames() != null
                && !config.multiDataSourceNames().isEmpty()) {
            for (String name : config.multiDataSourceNames()) {
                if (camelContext.getRegistry().lookupByNameAndType(name, DataSource.class) == null) {
                    DataSourceFactoryConfig dsFactoryConfig = new DataSourceFactoryConfig(name);
                    DataSource agroalDataSource = newDataSource(dsFactoryConfig, name);
                    camelContext.getRegistry().bind(name, agroalDataSource);
                }
            }
        } else {
            try {
                if (camelContext.getRegistry().lookupByNameAndType("dataSource", DataSource.class) == null) {
                    final List<ServiceLoader.Provider<DataSourceProvider>> providers =
                            findProviders(DataSourceProvider.class);
                    if (providers.size() == 1) {
                        DataSource agroalDataSource = providers.get(0).get().create();
                        camelContext.getRegistry().bind(DEFAULT_DATASOURCE, agroalDataSource);
                    } else {
                        throw new IllegalArgumentException("No dataSource implementation is present in the classpath");
                    }
                }
            } catch (Exception ex) {
                LOG.debug(ex.getMessage(), ex);
            }
        }
    }

    private synchronized DataSource newDataSource(DataSourceFactoryConfig dataSourceFactoryConfig, String name) {
        final String dataSourceProviderClass = dataSourceFactoryConfig.providerDataSourceClass();
        LOG.info("Creating DataSource of type {}", dataSourceProviderClass);

        final List<ServiceLoader.Provider<DataSourceProvider>> providers = findProviders(DataSourceProvider.class);

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
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }
}
