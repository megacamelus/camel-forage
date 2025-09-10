package org.apache.camel.forage.springboot.jdbc;

import io.agroal.api.AgroalDataSource;
import jakarta.annotation.PostConstruct;
import org.apache.camel.forage.jdbc.PooledJdbc;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class ForageJdbcAutoConfiguration extends PooledJdbc implements BeanFactoryAware {

    protected abstract Class getConnectionProviderClass();

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    public void createJdbcBeans() {
        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;

        // TODO dataSource is the default, in case of multidatasourceconfig, multiple datasources need to be configured
        // and the route has to be updated accordingly ?dataSource=#myDs
        String beanName = "dataSource";
        AgroalDataSource agroalDataSource = createPooledDataSource(null);
        configurableBeanFactory.registerSingleton(beanName, agroalDataSource);
    }
}
