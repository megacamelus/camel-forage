package org.apache.camel.forage.jdbc.factory;

import java.util.ServiceLoader;
import javax.sql.DataSource;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.support.sql.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of DataSourceFactory that uses ServiceLoader to discover and create DataSource providers.
 * Supports multiple database types through pluggable DataSourceProvider implementations.
 */
public class DefaultDataSourceFactory implements DataSourceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDataSourceFactory.class);

    private CamelContext camelContext;
    private static DataSource dataSource;

    /**
     * Creates a DataSource using ServiceLoader to discover the first available provider.
     */
    @Override
    public DataSource createDataSource(Endpoint endpoint) throws Exception {
        if (dataSource != null) {
            return dataSource;
        }

        DataSource ds = doCreateDataSource();
        endpoint.getCamelContext().getRegistry().bind("dataSource", ds);

        return ds;
    }

    private synchronized DataSource doCreateDataSource() {
        LOG.trace("Creating DataSource using ServiceLoader");

        DataSourceProvider provider = newDataSourceProvider();
        dataSource = provider.create();

        LOG.info(
                "DataSource created successfully using provider: {}",
                provider.getClass().getSimpleName());
        return dataSource;
    }

    private DataSourceProvider newDataSourceProvider() {
        ServiceLoader<DataSourceProvider> serviceLoader =
                ServiceLoader.load(DataSourceProvider.class, camelContext.getApplicationContextClassLoader());

        return serviceLoader
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No DataSourceProvider implementation found via ServiceLoader"));
    }

    //    /**
    //     * Creates a Spring transaction manager for the data source.
    //     */
    //    @Override
    //    public PlatformTransactionManager createTransactionManager(DataSource dataSource) throws Exception {
    //        return new DataSourceTransactionManager(dataSource);
    //    }
    //
    //    /**
    //     * Creates a transaction template with default timeout.
    //     */
    //    public TransactionTemplate createTransactionTemplate(PlatformTransactionManager transactionManager)
    //            throws Exception {
    //        TransactionTemplate template = new TransactionTemplate(transactionManager);
    //        template.setTimeout(30); // Default 30 seconds
    //        return template;
    //    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;

        ConfigStore.getInstance().setClassLoader(camelContext.getApplicationContextClassLoader());
    }

    @Override
    public CamelContext getCamelContext() {
        return this.camelContext;
    }
}
