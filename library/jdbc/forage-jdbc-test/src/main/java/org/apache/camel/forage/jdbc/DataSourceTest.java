package org.apache.camel.forage.jdbc;

import java.sql.ResultSet;
import javax.sql.DataSource;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DataSourceTest extends ForageJdbcTest {

    @BeforeAll
    public void setupProperties() {
        setUpDataSource("normal");
        setUpDataSource("transacted");

        setUpPoolConfiguration();
        setUpTransactionConfiguration();

        //        setUpAggregationConfiguration();
        //        setUpIdempotentConfiguration();
    }

    @Test
    public void testDefaultDataSource() throws Exception {
        DataSourceProvider dataSourceProvider = createDataSourceProvider();

        DataSource dataSource = dataSourceProvider.create("normal");

        Assertions.assertThat(dataSource).isNotNull();
        Assertions.assertThat(dataSource).isInstanceOf(io.agroal.pool.DataSource.class);
        Assertions.assertThat(((io.agroal.pool.DataSource) dataSource)
                        .getConfiguration()
                        .connectionPoolConfiguration()
                        .maxSize())
                .isEqualTo(20);
        Assertions.assertThat(((io.agroal.pool.DataSource) dataSource)
                        .getConfiguration()
                        .connectionPoolConfiguration()
                        .transactionRequirement()
                        .toString())
                .isEqualTo("OFF");

        ResultSet resultSet =
                dataSource.getConnection().createStatement().executeQuery(dataSourceProvider.getTestQuery());

        validateTestQueryResult(resultSet);
    }

    @Test
    public void testDefaultTransactedDataSource() throws Exception {
        DataSourceProvider dataSourceProvider = createDataSourceProvider();
        DataSource transactedDataSource = dataSourceProvider.create("transacted");

        Assertions.assertThat(transactedDataSource).isNotNull();
        Assertions.assertThat(transactedDataSource).isInstanceOf(io.agroal.pool.DataSource.class);
        Assertions.assertThat(((io.agroal.pool.DataSource) transactedDataSource)
                        .getConfiguration()
                        .connectionPoolConfiguration()
                        .transactionIntegration()
                        .getClass()
                        .toString())
                .contains("NarayanaTransactionIntegration");

        jakarta.transaction.TransactionManager transactionManager =
                com.arjuna.ats.jta.TransactionManager.transactionManager();
        transactionManager.begin();
        try {
            ResultSet resultSet = transactedDataSource
                    .getConnection()
                    .createStatement()
                    .executeQuery(dataSourceProvider.getTestQuery());

            validateTestQueryResult(resultSet);

            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
        }
    }
}
