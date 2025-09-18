package org.apache.camel.forage.jdbc.mssql;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import org.apache.camel.forage.jdbc.PooledDataSource;

/**
 * Microsoft SQL Server implementation extending PooledJdbc.
 * Provides SQL Server-specific connection provider configuration.
 */
public class MssqlJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return SQLServerDriver.class;
    }
}
