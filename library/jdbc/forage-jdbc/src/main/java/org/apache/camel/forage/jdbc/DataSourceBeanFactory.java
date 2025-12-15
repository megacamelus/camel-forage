package org.apache.camel.forage.jdbc;

import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.camel.CamelContext;
import org.apache.camel.forage.core.annotations.ConditionalBean;
import org.apache.camel.forage.core.annotations.ConditionalBeanGroup;
import org.apache.camel.forage.core.annotations.ForageFactory;
import org.apache.camel.forage.core.common.BeanFactory;
import org.apache.camel.forage.core.common.ServiceLoaderHelper;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.core.jta.MandatoryJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.NeverJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.NotSupportedJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.RequiredJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.RequiresNewJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.SupportsJtaTransactionPolicy;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.jdbc.common.DataSourceCommonExportHelper;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.forage.jdbc.common.ForageDataSource;
import org.apache.camel.forage.jdbc.common.aggregation.ForageAggregationRepository;
import org.apache.camel.forage.jdbc.common.idempotent.ForageIdRepository;
import org.apache.camel.forage.jdbc.common.idempotent.ForageJdbcMessageIdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ForageFactory(
        value = "CamelDataSourceFactory",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "Default DataSource factory with ServiceLoader discovery",
        factoryType = "DataSource",
        autowired = true,
        conditionalBeans = {
            @ConditionalBeanGroup(
                    id = "jta-transaction-policies",
                    description = "JTA Transaction Policy beans for Camel transacted routes",
                    configEntry = "jdbc.transaction.enabled",
                    beans = {
                        @ConditionalBean(
                                name = "PROPAGATION_REQUIRED",
                                javaType = "org.apache.camel.spi.TransactedPolicy",
                                description =
                                        "Starts a new transaction if none exists, otherwise joins the existing one"),
                        @ConditionalBean(
                                name = "MANDATORY",
                                javaType = "org.apache.camel.spi.TransactedPolicy",
                                description = "Requires an existing transaction, throws exception if none exists"),
                        @ConditionalBean(
                                name = "NEVER",
                                javaType = "org.apache.camel.spi.TransactedPolicy",
                                description = "Must execute without a transaction, throws exception if one exists"),
                        @ConditionalBean(
                                name = "NOT_SUPPORTED",
                                javaType = "org.apache.camel.spi.TransactedPolicy",
                                description = "Suspends any existing transaction and executes without one"),
                        @ConditionalBean(
                                name = "REQUIRES_NEW",
                                javaType = "org.apache.camel.spi.TransactedPolicy",
                                description = "Always starts a new transaction, suspending any existing one"),
                        @ConditionalBean(
                                name = "SUPPORTS",
                                javaType = "org.apache.camel.spi.TransactedPolicy",
                                description = "Joins existing transaction if present, otherwise runs without one")
                    }),
            @ConditionalBeanGroup(
                    id = "aggregation-repository",
                    description = "JDBC-backed aggregation repository for Camel aggregator EIP",
                    configEntry = "jdbc.aggregation.repository.enabled",
                    beans = {
                        @ConditionalBean(
                                nameFromConfig = "jdbc.aggregation.repository.name",
                                javaType = "org.apache.camel.processor.aggregate.jdbc.JdbcAggregationRepository",
                                description = "Transactional JDBC aggregation repository")
                    }),
            @ConditionalBeanGroup(
                    id = "idempotent-repository",
                    description = "JDBC-backed idempotent repository for message deduplication",
                    configEntry = "jdbc.idempotent.repository.enabled",
                    beans = {
                        @ConditionalBean(
                                nameFromConfig = "jdbc.idempotent.repository.table.name",
                                javaType = "org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository",
                                description = "JDBC message ID repository for idempotent consumer")
                    })
        })
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
                    ForageDataSource forageDataSource = newDataSource(dsFactoryConfig, name);
                    camelContext.getRegistry().bind(name, forageDataSource.dataSource());
                    createAggregationRepository(dsFactoryConfig, forageDataSource.dataSource());
                    createIdempotentRepository(
                            dsFactoryConfig, forageDataSource.dataSource(), forageDataSource.forageIdRepository());
                }
            }
        } else {
            try {
                if (camelContext.getRegistry().lookupByNameAndType("dataSource", DataSource.class) == null) {
                    final List<ServiceLoader.Provider<DataSourceProvider>> providers =
                            findProviders(DataSourceProvider.class);
                    if (providers.size() == 1) {
                        ForageDataSource forageDataSource = doCreateDataSource(providers.get(0), null);
                        camelContext.getRegistry().bind(DEFAULT_DATASOURCE, forageDataSource.dataSource());
                        createAggregationRepository(config, forageDataSource.dataSource());
                        createIdempotentRepository(
                                config, forageDataSource.dataSource(), forageDataSource.forageIdRepository());
                    } else {
                        throw new IllegalArgumentException("No dataSource implementation is present in the classpath");
                    }
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private void createIdempotentRepository(
            DataSourceFactoryConfig config, DataSource agroalDataSource, ForageIdRepository forageIdRepository) {
        if (config.enableIdempotentRepository()) {
            ForageJdbcMessageIdRepository forageJdbcMessageIdRepository =
                    new ForageJdbcMessageIdRepository(config, agroalDataSource, forageIdRepository);

            camelContext.getRegistry().bind(config.idempotentRepositoryTableName(), forageJdbcMessageIdRepository);
        }
    }

    private void createAggregationRepository(DataSourceFactoryConfig dsFactoryConfig, DataSource agroalDataSource) {
        if (!dsFactoryConfig.transactionEnabled() && dsFactoryConfig.aggregationRepositoryName() != null) {
            LOG.warn("Transactions have to be enabled in order to create aggregation repositories");
            return;
        }
        if (dsFactoryConfig.aggregationRepositoryName() != null) {
            camelContext
                    .getRegistry()
                    .bind(
                            dsFactoryConfig.aggregationRepositoryName(),
                            new ForageAggregationRepository(
                                    agroalDataSource,
                                    com.arjuna.ats.jta.TransactionManager.transactionManager(),
                                    dsFactoryConfig));
        }
    }

    private synchronized ForageDataSource newDataSource(DataSourceFactoryConfig dataSourceFactoryConfig, String name) {
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

    private ForageDataSource doCreateDataSource(ServiceLoader.Provider<DataSourceProvider> provider, String name) {
        final DataSourceProvider dataSourceProvider = provider.get();
        ForageIdRepository forageIdRepository = null;
        if (dataSourceProvider instanceof ForageIdRepository forageIdRepo) {
            forageIdRepository = forageIdRepo;
        }
        return new ForageDataSource(dataSourceProvider.create(name), forageIdRepository);
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
