package io.kaoto.forage.springboot.jms.jta;

import io.kaoto.forage.jms.common.ConnectionFactoryConfig;
import io.kaoto.forage.springboot.common.ConditionalOnForageProperty;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ConditionalOnForageProperty(
        configClass = ConnectionFactoryConfig.class,
        property = "jms.transaction.enabled",
        havingValue = "true")
@EnableTransactionManagement
public class ForageTransactionManagementAutoConfiguration
        extends io.kaoto.forage.springboot.common.jta.ForageTransactionManagementAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ForageTransactionManagementAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("JMS ForageTransactionManagementAutoConfiguration initialized - transaction management enabled");
    }
}
