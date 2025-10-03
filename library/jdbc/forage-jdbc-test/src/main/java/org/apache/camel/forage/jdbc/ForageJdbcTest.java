package org.apache.camel.forage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;

public abstract class ForageJdbcTest {

    protected abstract DataSourceProvider createDataSourceProvider();

    protected abstract void setUpDataSource(String dataSourceName);

    protected abstract void validateTestQueryResult(ResultSet rs) throws SQLException;

    public void setUpPoolConfiguration() {
        System.setProperty("normal.jdbc.pool.initial.size", "5");
        System.setProperty("normal.jdbc.pool.min.size", "2");
        System.setProperty("normal.jdbc.pool.max.size", "20");
        System.setProperty("normal.jdbc.pool.acquisition.timeout.seconds", "5");
        System.setProperty("normal.jdbc.pool.validation.timeout.seconds", "3");
        System.setProperty("normal.jdbc.pool.leak.timeout.minutes", "10");
        System.setProperty("normal.jdbc.pool.idle.validation.timeout.minutes", "3");
        System.setProperty("normal.jdbc.pool.leak.timeout.seconds", "10");

        System.setProperty("transacted.jdbc.pool.initial.size", "5");
        System.setProperty("transacted.jdbc.pool.min.size", "2");
        System.setProperty("transacted.jdbc.pool.max.size", "20");
        System.setProperty("transacted.jdbc.pool.acquisition.timeout.seconds", "5");
        System.setProperty("transacted.jdbc.pool.validation.timeout.seconds", "3");
        System.setProperty("transacted.jdbc.pool.leak.timeout.minutes", "10");
        System.setProperty("transacted.jdbc.pool.idle.validation.timeout.minutes", "3");
        System.setProperty("transacted.jdbc.pool.leak.timeout.seconds", "10");
    }

    public void setUpTransactionConfiguration() {
        System.setProperty("transacted.jdbc.transaction.enabled", "true");
        System.setProperty("transacted.jdbc.transaction.timeout.seconds", "30");
    }
}
