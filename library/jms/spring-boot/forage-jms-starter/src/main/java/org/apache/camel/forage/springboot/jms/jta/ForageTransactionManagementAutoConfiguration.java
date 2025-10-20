package org.apache.camel.forage.springboot.jms.jta;

import jakarta.annotation.PostConstruct;
import org.apache.camel.forage.jms.common.ConnectionFactoryConfig;
import org.apache.camel.forage.springboot.common.ConditionalOnForageProperty;
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
        extends org.apache.camel.forage.springboot.common.jta.ForageTransactionManagementAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ForageTransactionManagementAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("JMS ForageTransactionManagementAutoConfiguration initialized - transaction management enabled");
    }
}
