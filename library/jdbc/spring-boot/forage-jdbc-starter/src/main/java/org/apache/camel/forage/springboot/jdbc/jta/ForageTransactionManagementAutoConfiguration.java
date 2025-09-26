package org.apache.camel.forage.springboot.jdbc.jta;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionSynchronizationRegistryImple;
import com.arjuna.ats.internal.jta.transaction.arjunacore.UserTransactionImple;
import jakarta.annotation.PostConstruct;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.forage.springboot.jdbc.ConditionalOnForageProperty;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@ConditionalOnForageProperty(
        configClass = DataSourceFactoryConfig.class,
        property = "jdbc.transaction.enabled",
        havingValue = "true")
@EnableTransactionManagement
public class ForageTransactionManagementAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ForageTransactionManagementAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("ForageTransactionManagementAutoConfiguration initialized - transaction management enabled");
    }

    @Bean
    public JtaTransactionManager jtaTransactionManager() {
        log.info("Creating JtaTransactionManager bean");

        JtaTransactionManager transactionManager = new JtaTransactionManager(
                new UserTransactionImple(), com.arjuna.ats.jta.TransactionManager.transactionManager());

        transactionManager.setTransactionSynchronizationRegistry(new TransactionSynchronizationRegistryImple());
        log.debug("JtaTransactionManager created successfully");
        return transactionManager;
    }

    @Bean("PROPAGATION_REQUIRED")
    public SpringTransactionPolicy propagationRequired(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_REQUIRED");

        return springTransactionPolicy;
    }

    @Bean("NESTED")
    public SpringTransactionPolicy nested(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_NESTED");

        return springTransactionPolicy;
    }

    @Bean("MANDATORY")
    public SpringTransactionPolicy mandatory(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_MANDATORY");

        return springTransactionPolicy;
    }

    @Bean("NEVER")
    public SpringTransactionPolicy never(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_NEVER");

        return springTransactionPolicy;
    }

    @Bean("NOT_SUPPORTED")
    public SpringTransactionPolicy notSupported(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_NOT_SUPPORTED");

        return springTransactionPolicy;
    }

    @Bean("REQUIRES_NEW")
    public SpringTransactionPolicy requiresNew(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_REQUIRES_NEW");

        return springTransactionPolicy;
    }

    @Bean("SUPPORTS")
    public SpringTransactionPolicy supports(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_SUPPORTS");

        return springTransactionPolicy;
    }

    private static SpringTransactionPolicy createSpringBootTransactionPolicy(
            JtaTransactionManager jtaTransactionManager, String propagationBehavior) {
        SpringTransactionPolicy springTransactionPolicy = new SpringTransactionPolicy(jtaTransactionManager);
        springTransactionPolicy.setPropagationBehaviorName(propagationBehavior);
        return springTransactionPolicy;
    }
}
