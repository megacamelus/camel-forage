package org.apache.camel.forage.quarkus.jdbc;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import javax.sql.DataSource;
import org.apache.camel.CamelContext;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.forage.jdbc.common.aggregation.ForageAggregationRepository;
import org.apache.camel.forage.jdbc.common.idempotent.ForageIdRepository;
import org.apache.camel.forage.jdbc.common.idempotent.ForageJdbcMessageIdRepository;
import org.apache.camel.forage.jdbc.db2.Db2Jdbc;
import org.apache.camel.forage.jdbc.h2.H2Jdbc;
import org.apache.camel.forage.jdbc.hsqldb.HsqldbJdbc;
import org.apache.camel.forage.jdbc.mariadb.MariadbJdbc;
import org.apache.camel.forage.jdbc.mssql.MssqlJdbc;
import org.apache.camel.forage.jdbc.mysql.MysqlJdbc;
import org.apache.camel.forage.jdbc.oracle.OracleJdbc;
import org.apache.camel.forage.jdbc.postgresql.PostgresqlJdbc;
import org.apache.camel.processor.aggregate.jdbc.JdbcAggregationRepository;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;
import org.jboss.logging.Logger;

/**
 * Aggregation repository is created via Recorder
 */
@Recorder
public class ForageJdbcRecorder {
    private static final org.jboss.logging.Logger LOG = Logger.getLogger(ForageJdbcRecorder.class);

    public RuntimeValue<JdbcAggregationRepository> createAggregationRepository(
            String dsName, RuntimeValue<CamelContext> camelContext, DataSourceFactoryConfig config) {

        CamelContext context = camelContext.getValue();
        DataSource agroalDataSource = context.getRegistry().lookupByNameAndType(dsName, DataSource.class);
        JdbcAggregationRepository ar = createAggregationRepository(config, agroalDataSource);
        if (ar != null) {
            return new RuntimeValue<>(ar);
        }
        return null;
    }

    public RuntimeValue<JdbcMessageIdRepository> createIdempotentRepository(
            String dsName, RuntimeValue<CamelContext> camelContext, DataSourceFactoryConfig config) {

        CamelContext context = camelContext.getValue();
        DataSource agroalDataSource = context.getRegistry().lookupByNameAndType(dsName, DataSource.class);
        JdbcMessageIdRepository ir = createIdempotentRepository(config, agroalDataSource);
        if (ir != null) {
            return new RuntimeValue<>(ir);
        }
        return null;
    }

    private JdbcAggregationRepository createAggregationRepository(
            DataSourceFactoryConfig dsFactoryConfig, DataSource agroalDataSource) {
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

    private JdbcMessageIdRepository createIdempotentRepository(
            DataSourceFactoryConfig config, DataSource agroalDataSource) {

        ForageIdRepository forageIdRepository =
                switch (config.dbKind()) {
                    case "db2" -> new Db2Jdbc();
                    case "h2" -> new H2Jdbc();
                    case "hsqldb" -> new HsqldbJdbc();
                    case "mariadb" -> new MariadbJdbc();
                    case "mssql" -> new MssqlJdbc();
                    case "mysql" -> new MysqlJdbc();
                    case "oracle" -> new OracleJdbc();
                    case "postgresql" -> new PostgresqlJdbc();
                    default -> null;
                };

        if (config.enableIdempotentRepository()) {
            if (forageIdRepository == null) {
                LOG.warn("Unsupported type of db ('%s') for the idempotent repository".formatted(config.dbKind()));
                return null;
            }
            if (config.idempotentRepositoryTableName() != null) {
                return new ForageJdbcMessageIdRepository(config, agroalDataSource, forageIdRepository);
            }
        }

        return null;
    }
}
