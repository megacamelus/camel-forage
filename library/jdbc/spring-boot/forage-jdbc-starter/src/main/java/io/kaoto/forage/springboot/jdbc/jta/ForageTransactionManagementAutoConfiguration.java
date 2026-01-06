package io.kaoto.forage.springboot.jdbc.jta;

import jakarta.annotation.PostConstruct;
import io.kaoto.forage.jdbc.common.DataSourceFactoryConfig;
import io.kaoto.forage.springboot.common.ConditionalOnForageProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ConditionalOnForageProperty(
        configClass = DataSourceFactoryConfig.class,
        property = "jdbc.transaction.enabled",
        havingValue = "true")
@EnableTransactionManagement
public class ForageTransactionManagementAutoConfiguration
        extends io.kaoto.forage.springboot.common.jta.ForageTransactionManagementAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ForageTransactionManagementAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("JDBC ForageTransactionManagementAutoConfiguration initialized - transaction management enabled");
    }
}
