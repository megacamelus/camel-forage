package org.apache.camel.forage.jdbc.hsqldb;

import org.apache.camel.forage.jdbc.PooledDataSource;
import org.hsqldb.jdbc.JDBCDriver;

/**
 * HSQLDB implementation extending PooledJdbc.
 * Provides HSQLDB-specific connection provider configuration.
 */
public class HsqldbJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return JDBCDriver.class;
    }
}
