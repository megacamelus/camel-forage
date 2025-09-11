package org.apache.camel.forage.springboot.jdbc;

import io.agroal.api.AgroalDataSource;
import io.agroal.springframework.boot.AgroalDataSourceAutoConfiguration;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.ServiceLoader;
import org.apache.camel.forage.core.common.ServiceLoaderHelper;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.jdbc.factory.DataSourceFactoryConfig;
import org.apache.camel.forage.jdbc.factory.MultiDataSourceConfig;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({DataSourceAutoConfiguration.class, AgroalDataSourceAutoConfiguration.class})
public class ForageDataSourceAutoConfiguration implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @PostConstruct
    public void createJdbcBeans() {
        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;

        MultiDataSourceConfig config = new MultiDataSourceConfig();

        if (config != null
                && config.multiDataSourceNames() != null
                && !config.multiDataSourceNames().isEmpty()) {
            boolean isDataSourceCrated = false;
            for (String name : config.multiDataSourceNames()) {
                if (!configurableBeanFactory.containsBean(name)) {
                    DataSourceFactoryConfig dsFactoryConfig = new DataSourceFactoryConfig(name);
                    AgroalDataSource agroalDataSource = newDataSource(dsFactoryConfig, name);
                    configurableBeanFactory.registerSingleton(name, agroalDataSource);
                    if (!isDataSourceCrated) {
                        // This is needed for Spring Boot AutoConfiguration, let's just register the first datasource
                        // as the default dataSource too
                        configurableBeanFactory.registerSingleton("dataSource", agroalDataSource);
                        isDataSourceCrated = true;
                    }
                }
            }
        } else {
            final List<ServiceLoader.Provider<DataSourceProvider>> providers = findDataSourceProviders();
            if (providers.size() == 1) {
                AgroalDataSource agroalDataSource =
                        (AgroalDataSource) providers.get(0).get().create();
                configurableBeanFactory.registerSingleton("dataSource", agroalDataSource);
            } else {
                throw new IllegalArgumentException("No dataSource implementation is present in the classpath");
            }
        }
    }

    private synchronized AgroalDataSource newDataSource(DataSourceFactoryConfig dataSourceFactoryConfig, String name) {
        final String dataSourceProviderClass = dataSourceFactoryConfig.providerDataSourceClass();

        final List<ServiceLoader.Provider<DataSourceProvider>> providers = findDataSourceProviders();

        ServiceLoader.Provider<DataSourceProvider> dataSourceProvider;
        if (providers.size() == 1) {
            dataSourceProvider = providers.get(0);
        } else {
            dataSourceProvider = ServiceLoaderHelper.findProviderByClassName(providers, dataSourceProviderClass);
        }

        if (dataSourceProvider == null) {
            return null;
        }

        return doCreateDataSource(dataSourceProvider, name);
    }

    private AgroalDataSource doCreateDataSource(ServiceLoader.Provider<DataSourceProvider> provider, String name) {
        final DataSourceProvider dataSourceProvider = provider.get();
        return (AgroalDataSource) dataSourceProvider.create(name);
    }

    private List<ServiceLoader.Provider<DataSourceProvider>> findDataSourceProviders() {
        ServiceLoader<DataSourceProvider> serviceLoader = ServiceLoader.load(
                DataSourceProvider.class, beanFactory.getClass().getClassLoader());

        return serviceLoader.stream().toList();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
