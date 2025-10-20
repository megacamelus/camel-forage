package org.apache.camel.forage.springboot.common.jta;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionSynchronizationRegistryImple;
import com.arjuna.ats.internal.jta.transaction.arjunacore.UserTransactionImple;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Base auto-configuration for JTA transaction management in Forage Spring Boot applications.
 * This class provides common transaction management beans that can be reused across different
 * Forage modules (JDBC, JMS, etc.).
 *
 * <p>Subclasses should:
 * <ul>
 * <li>Be annotated with {@code @Configuration}</li>
 * <li>Use {@code @ConditionalOnForageProperty} to enable based on module-specific configuration</li>
 * <li>Be annotated with {@code @EnableTransactionManagement}</li>
 * <li>Override {@code init()} method with {@code @PostConstruct} for module-specific initialization logging</li>
 * </ul>
 */
public abstract class ForageTransactionManagementAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ForageTransactionManagementAutoConfiguration.class);

    @ConditionalOnMissingBean
    @Bean
    public JtaTransactionManager jtaTransactionManager() {
        log.info("Creating JtaTransactionManager bean");

        JtaTransactionManager transactionManager = new JtaTransactionManager(
                new UserTransactionImple(), com.arjuna.ats.jta.TransactionManager.transactionManager());

        transactionManager.setTransactionSynchronizationRegistry(new TransactionSynchronizationRegistryImple());
        log.debug("JtaTransactionManager created successfully");
        return transactionManager;
    }

    @ConditionalOnMissingBean
    @Bean("PROPAGATION_REQUIRED")
    public SpringTransactionPolicy propagationRequired(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_REQUIRED");

        return springTransactionPolicy;
    }

    @ConditionalOnMissingBean
    @Bean("NESTED")
    public SpringTransactionPolicy nested(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_NESTED");

        return springTransactionPolicy;
    }

    @ConditionalOnMissingBean
    @Bean("MANDATORY")
    public SpringTransactionPolicy mandatory(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_MANDATORY");

        return springTransactionPolicy;
    }

    @ConditionalOnMissingBean
    @Bean("NEVER")
    public SpringTransactionPolicy never(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_NEVER");

        return springTransactionPolicy;
    }

    @ConditionalOnMissingBean
    @Bean("NOT_SUPPORTED")
    public SpringTransactionPolicy notSupported(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_NOT_SUPPORTED");

        return springTransactionPolicy;
    }

    @ConditionalOnMissingBean
    @Bean("REQUIRES_NEW")
    public SpringTransactionPolicy requiresNew(JtaTransactionManager jtaTransactionManager) {
        SpringTransactionPolicy springTransactionPolicy =
                createSpringBootTransactionPolicy(jtaTransactionManager, "PROPAGATION_REQUIRES_NEW");

        return springTransactionPolicy;
    }

    @ConditionalOnMissingBean
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
