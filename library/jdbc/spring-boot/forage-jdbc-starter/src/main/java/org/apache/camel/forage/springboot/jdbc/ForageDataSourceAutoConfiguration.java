package org.apache.camel.forage.springboot.jdbc;

import io.agroal.api.AgroalDataSource;
import io.agroal.springframework.boot.AgroalDataSourceAutoConfiguration;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.camel.forage.core.annotations.FactoryType;
import org.apache.camel.forage.core.annotations.FactoryVariant;
import org.apache.camel.forage.core.annotations.ForageFactory;
import org.apache.camel.forage.core.common.ServiceLoaderHelper;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.core.jta.MandatoryJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.NeverJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.NotSupportedJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.RequiredJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.RequiresNewJtaTransactionPolicy;
import org.apache.camel.forage.core.jta.SupportsJtaTransactionPolicy;
import org.apache.camel.forage.core.util.config.ConfigHelper;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.jdbc.common.DataSourceCommonExportHelper;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.forage.jdbc.common.ForageDataSource;
import org.apache.camel.forage.jdbc.common.aggregation.ForageAggregationRepository;
import org.apache.camel.forage.jdbc.common.idempotent.ForageIdRepository;
import org.apache.camel.forage.jdbc.common.idempotent.ForageJdbcMessageIdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Auto-configuration for Forage DataSource creation using ServiceLoader discovery.
 * Automatically creates DataSource beans from JDBC configuration properties,
 * supporting both single and multi-instance (prefixed) configurations.
 */
@ForageFactory(
        value = "CamelSpringBootDataSourceFactory",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "Spring Boot DataSource factory with ServiceLoader discovery",
        type = FactoryType.DATA_SOURCE,
        autowired = true,
        configClass = DataSourceFactoryConfig.class,
        variant = FactoryVariant.SPRING_BOOT)
@Configuration
@AutoConfigureBefore({DataSourceAutoConfiguration.class, AgroalDataSourceAutoConfiguration.class})
public class ForageDataSourceAutoConfiguration implements BeanFactoryAware {

    private static final Logger log = LoggerFactory.getLogger(ForageDataSourceAutoConfiguration.class);

    /**
     * Transaction management configuration that enables Spring transaction support
     * when JDBC transactions are configured in Forage DataSource settings.
     */
    @Configuration
    @ConditionalOnProperty(value = "forage.jdbc.transaction.enabled", havingValue = "true")
    @EnableTransactionManagement
    class ForageTransactionManagement {

        @PostConstruct
        public void init() {
            log.info("ForageTransactionManagement configuration enabled");
        }
    }

    private BeanFactory beanFactory;

    @PostConstruct
    public void createJdbcBeans() {
        log.info("Initializing Forage DataSource auto-configuration");
        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;

        DataSourceFactoryConfig config = new DataSourceFactoryConfig();

        // Register transaction policy beans if transactions are enabled
        if (config.transactionEnabled()) {
            log.info("JDBC transactions enabled, registering transaction policy beans");
            configurableBeanFactory.registerSingleton("PROPAGATION_REQUIRED", new RequiredJtaTransactionPolicy());
            configurableBeanFactory.registerSingleton("MANDATORY", new MandatoryJtaTransactionPolicy());
            configurableBeanFactory.registerSingleton("NEVER", new NeverJtaTransactionPolicy());
            configurableBeanFactory.registerSingleton("NOT_SUPPORTED", new NotSupportedJtaTransactionPolicy());
            configurableBeanFactory.registerSingleton("REQUIRES_NEW", new RequiresNewJtaTransactionPolicy());
            configurableBeanFactory.registerSingleton("SUPPORTS", new SupportsJtaTransactionPolicy());
        }

        Set<String> prefixes =
                ConfigStore.getInstance().readPrefixes(config, ConfigHelper.getNamedPropertyRegexp("jdbc"));
        log.debug("Found {} prefixes for JDBC configuration: {}", prefixes.size(), prefixes);

        if (!prefixes.isEmpty()) {
            log.info("Creating named DataSource beans for prefixes: {}", prefixes);
            boolean isDataSourceCrated = false;
            for (String name : prefixes) {
                if (!configurableBeanFactory.containsBean(name)) {
                    log.debug("Creating DataSource bean with name: {}", name);
                    DataSourceFactoryConfig dsFactoryConfig = new DataSourceFactoryConfig(name);
                    ForageDataSource forageDataSource = newDataSource(dsFactoryConfig, name);
                    configurableBeanFactory.registerSingleton(name, forageDataSource.dataSource());
                    log.info("Registered DataSource bean: {}", name);
                    if (!isDataSourceCrated) {
                        // This is needed for Spring Boot AutoConfiguration, let's just register the first datasource
                        // as the default dataSource too
                        configurableBeanFactory.registerSingleton("dataSource", forageDataSource.dataSource());
                        createAggregationRepository(
                                configurableBeanFactory, dsFactoryConfig, forageDataSource.dataSource());
                        createIdempotentRepository(
                                configurableBeanFactory,
                                dsFactoryConfig,
                                forageDataSource.dataSource(),
                                forageDataSource.forageIdRepository());
                        log.info("Registered default DataSource bean using: {}", name);
                        isDataSourceCrated = true;
                    }
                } else {
                    log.debug("DataSource bean {} already exists, skipping creation", name);
                }
            }
        } else {
            log.debug("No prefixed JDBC configurations found, looking for single DataSource provider");
            final List<ServiceLoader.Provider<DataSourceProvider>> providers = findDataSourceProviders();
            if (providers.size() == 1) {
                log.info(
                        "Creating default DataSource using single provider: {}",
                        providers.get(0).type().getName());
                ForageDataSource forageDataSource = doCreateDataSource(providers.get(0), null);

                configurableBeanFactory.registerSingleton("dataSource", forageDataSource.dataSource());
                createAggregationRepository(configurableBeanFactory, config, forageDataSource.dataSource());
                createIdempotentRepository(
                        configurableBeanFactory,
                        config,
                        forageDataSource.dataSource(),
                        forageDataSource.forageIdRepository());
                log.info("Registered default DataSource bean");
            } else {
                log.error(
                        "Expected exactly 1 DataSource provider, but found {}: {}",
                        providers.size(),
                        providers.stream().map(p -> p.type().getName()).toList());
                throw new IllegalArgumentException("No dataSource implementation is present in the classpath");
            }
        }
    }

    private synchronized ForageDataSource newDataSource(DataSourceFactoryConfig dataSourceFactoryConfig, String name) {
        log.debug("Creating new DataSource for name: {} with dbKind: {}", name, dataSourceFactoryConfig.dbKind());
        final String dataSourceProviderClass =
                DataSourceCommonExportHelper.transformDbKindIntoProviderClass(dataSourceFactoryConfig.dbKind());
        log.debug("Resolved provider class: {}", dataSourceProviderClass);

        final List<ServiceLoader.Provider<DataSourceProvider>> providers = findDataSourceProviders();
        log.debug("Found {} DataSource providers", providers.size());

        ServiceLoader.Provider<DataSourceProvider> dataSourceProvider;
        if (providers.size() == 1) {
            dataSourceProvider = providers.get(0);
            log.debug(
                    "Using single available provider: {}",
                    dataSourceProvider.type().getName());
        } else {
            dataSourceProvider = ServiceLoaderHelper.findProviderByClassName(providers, dataSourceProviderClass);
            log.debug(
                    "Selected provider by class name: {}",
                    dataSourceProvider != null ? dataSourceProvider.type().getName() : "null");
        }

        if (dataSourceProvider == null) {
            log.error("No DataSource provider found for class: {}", dataSourceProviderClass);
            return null;
        }

        return doCreateDataSource(dataSourceProvider, name);
    }

    private ForageDataSource doCreateDataSource(ServiceLoader.Provider<DataSourceProvider> provider, String name) {
        log.debug(
                "Creating DataSource instance using provider: {} for name: {}",
                provider.type().getName(),
                name);
        final DataSourceProvider dataSourceProvider = provider.get();
        AgroalDataSource dataSource = (AgroalDataSource) dataSourceProvider.create(name);
        log.debug("Successfully created DataSource instance for: {}", name);

        ForageIdRepository forageIdRepository = null;
        if (dataSourceProvider instanceof ForageIdRepository forageIdRepo) {
            forageIdRepository = forageIdRepo;
        }

        return new ForageDataSource(dataSource, forageIdRepository);
    }

    private List<ServiceLoader.Provider<DataSourceProvider>> findDataSourceProviders() {
        ServiceLoader<DataSourceProvider> serviceLoader = ServiceLoader.load(
                DataSourceProvider.class, beanFactory.getClass().getClassLoader());

        List<ServiceLoader.Provider<DataSourceProvider>> providers =
                serviceLoader.stream().toList();
        log.debug(
                "Found {} DataSource providers: {}",
                providers.size(),
                providers.stream().map(p -> p.type().getName()).toList());
        return providers;
    }

    private void createAggregationRepository(
            ConfigurableBeanFactory configurableBeanFactory,
            DataSourceFactoryConfig dsFactoryConfig,
            DataSource agroalDataSource) {
        if (!dsFactoryConfig.transactionEnabled() && dsFactoryConfig.aggregationRepositoryName() != null) {
            log.warn("Transactions have to be enabled in order to create aggregation repositories");
            return;
        }
        if (dsFactoryConfig.aggregationRepositoryName() != null) {
            configurableBeanFactory.registerSingleton(
                    dsFactoryConfig.aggregationRepositoryName(),
                    new ForageAggregationRepository(
                            agroalDataSource,
                            com.arjuna.ats.jta.TransactionManager.transactionManager(),
                            dsFactoryConfig));
        }
    }

    private void createIdempotentRepository(
            ConfigurableBeanFactory configurableBeanFactory,
            DataSourceFactoryConfig config,
            DataSource agroalDataSource,
            ForageIdRepository forageIdRepository) {
        if (config.enableIdempotentRepository()) {
            ForageJdbcMessageIdRepository forageJdbcMessageIdRepository =
                    new ForageJdbcMessageIdRepository(config, agroalDataSource, forageIdRepository);

            configurableBeanFactory.registerSingleton(
                    config.idempotentRepositoryTableName(), forageJdbcMessageIdRepository);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
