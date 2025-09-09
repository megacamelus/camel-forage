package org.apache.camel.forage.jdbc.mssql;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import org.apache.camel.forage.jdbc.PooledJdbc;

/**
 * Microsoft SQL Server implementation extending PooledJdbc.
 * Provides SQL Server-specific connection provider configuration.
 */
public class MssqlJdbc extends PooledJdbc {

    @Override
    protected Class getConnectionProviderClass() {
        return SQLServerDriver.class;
    }
}
