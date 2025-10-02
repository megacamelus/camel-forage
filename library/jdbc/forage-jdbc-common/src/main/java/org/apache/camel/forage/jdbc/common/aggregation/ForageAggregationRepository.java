package org.apache.camel.forage.jdbc.common.aggregation;

import jakarta.transaction.TransactionManager;
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

        setHeadersToStoreAsText(dataSourceFactoryConfig.aggregationRepositoryHeadersToStore()); // comma separated list
        setStoreBodyAsText(dataSourceFactoryConfig.aggregationRepositoryStoreBody());
        setDeadLetterUri(dataSourceFactoryConfig.aggregationRepositoryDeadLetterUri());
        setAllowSerializedHeaders(dataSourceFactoryConfig.aggregationRepositoryAllowSerializedHeaders());
        setMaximumRedeliveries(dataSourceFactoryConfig.aggregationRepositoryMaximumRedeliveries());
        setUseRecovery(dataSourceFactoryConfig.aggregationRepositoryUseRecovery());
        setPropagationBehaviorName(dataSourceFactoryConfig.aggregationRepositoryPropagationBehaviourName());
    }
}
