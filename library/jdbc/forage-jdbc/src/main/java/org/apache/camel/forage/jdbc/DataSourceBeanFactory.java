package org.apache.camel.forage.jdbc;

import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.camel.CamelContext;
import org.apache.camel.forage.core.annotations.ForageFactory;
import org.apache.camel.forage.core.common.BeanFactory;
import org.apache.camel.forage.core.common.ServiceLoaderHelper;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.jdbc.common.DataSourceCommonExportHelper;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.forage.jdbc.jta.MandatoryJtaTransactionPolicy;
import org.apache.camel.forage.jdbc.jta.NeverJtaTransactionPolicy;
import org.apache.camel.forage.jdbc.jta.NotSupportedJtaTransactionPolicy;
import org.apache.camel.forage.jdbc.jta.RequiredJtaTransactionPolicy;
import org.apache.camel.forage.jdbc.jta.RequiresNewJtaTransactionPolicy;
import org.apache.camel.forage.jdbc.jta.SupportsJtaTransactionPolicy;
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
    private static final String DEFAULT_DATASOURCE = "dataSource";

    @Override
    public void configure() {

        DataSourceFactoryConfig config = new DataSourceFactoryConfig();
        Set<String> prefixes = ConfigStore.getInstance().readPrefixes(config, "(.+).jdbc\\..*");

        if (config.transactionEnabled()) {
            camelContext.getRegistry().bind("PROPAGATION_REQUIRED", new RequiredJtaTransactionPolicy());
            camelContext.getRegistry().bind("MANDATORY", new MandatoryJtaTransactionPolicy());
            camelContext.getRegistry().bind("NEVER", new NeverJtaTransactionPolicy());
            camelContext.getRegistry().bind("NOT_SUPPORTED", new NotSupportedJtaTransactionPolicy());
            camelContext.getRegistry().bind("REQUIRES_NEW", new RequiresNewJtaTransactionPolicy());
            camelContext.getRegistry().bind("SUPPORTS", new SupportsJtaTransactionPolicy());
        }

        if (!prefixes.isEmpty()) {
            for (String name : prefixes) {
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
        final String dataSourceProviderClass =
                DataSourceCommonExportHelper.transformDbKindIntoProviderClass(dataSourceFactoryConfig.dbKind());
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
