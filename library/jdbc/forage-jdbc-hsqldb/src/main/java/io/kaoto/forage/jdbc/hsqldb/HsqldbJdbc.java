package io.kaoto.forage.jdbc.hsqldb;

import io.kaoto.forage.core.annotations.ForageBean;
import io.kaoto.forage.jdbc.common.PooledDataSource;
import org.hsqldb.jdbc.JDBCDriver;
import org.hsqldb.jdbc.pool.JDBCXADataSource;

/**
 * HSQLDB implementation extending PooledJdbc.
 * Provides HSQLDB-specific connection provider configuration.
 */
@ForageBean(
        value = "hsqldb",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "HSQLDB database")
public class HsqldbJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        if (getConfig().transactionEnabled()) {
            return JDBCXADataSource.class;
        } else {
            return JDBCDriver.class;
        }
    }

    @Override
    public String getTestQuery() {
        return "SELECT 'HSQLDB ' || database_version() FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_SCHEM = 'INFORMATION_SCHEMA' LIMIT 1";
    }
}
