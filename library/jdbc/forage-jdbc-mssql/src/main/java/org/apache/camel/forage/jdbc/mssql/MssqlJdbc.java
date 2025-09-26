package org.apache.camel.forage.jdbc.mssql;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.jdbc.common.PooledDataSource;

/**
 * Microsoft SQL Server implementation extending PooledJdbc.
 * Provides SQL Server-specific connection provider configuration.
 */
@ForageBean(
        value = "mssql",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "MSSQL database DataSource Provider")
public class MssqlJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        if (getConfig().transactionEnabled()) {
            return SQLServerXADataSource.class;
        } else {
            return SQLServerDriver.class;
        }
    }

    @Override
    public String getTestQuery() {
        return "SELECT @@VERSION, DB_NAME(), SUSER_SNAME()";
    }
}
