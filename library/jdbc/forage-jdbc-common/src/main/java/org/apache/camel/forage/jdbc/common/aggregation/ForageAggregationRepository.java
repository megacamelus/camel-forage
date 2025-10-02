package org.apache.camel.forage.jdbc.common.aggregation;

import jakarta.transaction.TransactionManager;
import javax.sql.DataSource;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.processor.aggregate.jdbc.JdbcAggregationRepository;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class ForageAggregationRepository extends JdbcAggregationRepository {

    public ForageAggregationRepository(
            DataSource dataSource,
            TransactionManager transactionManager,
            DataSourceFactoryConfig dataSourceFactoryConfig) {
        setRepositoryName(dataSourceFactoryConfig.aggregationRepositoryName());
        setDataSource(dataSource);
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        setTransactionManager(dataSourceTransactionManager);

        setHeadersToStoreAsText(dataSourceFactoryConfig.aggregationRepositoryHeadersToStore()); // comma separated list
        setStoreBodyAsText(dataSourceFactoryConfig.aggregationRepositoryStoreBody());
        setDeadLetterUri(dataSourceFactoryConfig.aggregationRepositoryDeadLetterUri());
        setAllowSerializedHeaders(dataSourceFactoryConfig.aggregationRepositoryAllowSerializedHeaders());
        setMaximumRedeliveries(dataSourceFactoryConfig.aggregationRepositoryMaximumRedeliveries());
        setUseRecovery(dataSourceFactoryConfig.aggregationRepositoryUseRecovery());
        setPropagationBehaviorName(dataSourceFactoryConfig.aggregationRepositoryPropagationBehaviourName());
    }
}
