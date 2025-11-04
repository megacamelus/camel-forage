package org.apache.camel.forage.quarkus.jdbc;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import javax.sql.DataSource;
import org.apache.camel.CamelContext;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.forage.jdbc.common.aggregation.ForageAggregationRepository;
import org.apache.camel.processor.aggregate.jdbc.JdbcAggregationRepository;
import org.jboss.logging.Logger;

/**
 * Aggregation repository is created via Recorder
 */
@Recorder
public class ForageJdbcRecorder {
    private static final org.jboss.logging.Logger LOG = Logger.getLogger(ForageJdbcRecorder.class);

    public RuntimeValue<JdbcAggregationRepository> configureAggregator(
            String dsName, RuntimeValue<CamelContext> camelContext, DataSourceFactoryConfig config) {

        CamelContext context = camelContext.getValue();
        DataSource agroalDataSource = context.getRegistry().lookupByNameAndType(dsName, DataSource.class);
        JdbcAggregationRepository ar = createAggregationRepository(context, config, agroalDataSource);
        if (ar != null) {
            return new RuntimeValue<>(ar);
        }
        return null;
    }

    private JdbcAggregationRepository createAggregationRepository(
            CamelContext camelContext, DataSourceFactoryConfig dsFactoryConfig, DataSource agroalDataSource) {
        if (!dsFactoryConfig.transactionEnabled() && dsFactoryConfig.aggregationRepositoryName() != null) {
            LOG.warn("Transactions have to be enabled in order to create aggregation repositories");
            return null;
        }
        if (dsFactoryConfig.aggregationRepositoryName() != null) {
            return new ForageAggregationRepository(
                    agroalDataSource, com.arjuna.ats.jta.TransactionManager.transactionManager(), dsFactoryConfig);
        }
        return null;
    }
}
