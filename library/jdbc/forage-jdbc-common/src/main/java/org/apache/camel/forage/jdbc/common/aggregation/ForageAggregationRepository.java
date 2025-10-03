package org.apache.camel.forage.jdbc.common.aggregation;

import jakarta.transaction.TransactionManager;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.processor.aggregate.jdbc.JdbcAggregationRepository;
import org.springframework.transaction.jta.JtaTransactionManager;

public class ForageAggregationRepository extends JdbcAggregationRepository {

    public ForageAggregationRepository(
            DataSource dataSource,
            TransactionManager transactionManager,
            DataSourceFactoryConfig dataSourceFactoryConfig) {
        setRepositoryName(dataSourceFactoryConfig.aggregationRepositoryName());
        setDataSource(dataSource);

        JtaTransactionManager jtaTransactionManager =
                new JtaTransactionManager(com.arjuna.ats.jta.TransactionManager.transactionManager());
        setTransactionManager(jtaTransactionManager);

        setIfNotNull(dataSourceFactoryConfig.aggregationRepositoryStoreBody(), this::setStoreBodyAsText);
        setIfNotNull(dataSourceFactoryConfig.aggregationRepositoryDeadLetterUri(), this::setDeadLetterUri);
        setIfNotNull(
                dataSourceFactoryConfig.aggregationRepositoryAllowSerializedHeaders(), this::setAllowSerializedHeaders);
        setIfNotNull(dataSourceFactoryConfig.aggregationRepositoryMaximumRedeliveries(), this::setMaximumRedeliveries);
        setIfNotNull(dataSourceFactoryConfig.aggregationRepositoryUseRecovery(), this::setUseRecovery);
        setIfNotNull(
                dataSourceFactoryConfig.aggregationRepositoryPropagationBehaviourName(),
                this::setPropagationBehaviorName);
        setIfNotNull(dataSourceFactoryConfig.aggregationRepositoryHeadersToStore(), this::setHeadersToStoreAsText);
    }

    private <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
