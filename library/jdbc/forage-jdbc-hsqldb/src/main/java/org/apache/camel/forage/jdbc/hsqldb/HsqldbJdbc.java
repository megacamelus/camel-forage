package org.apache.camel.forage.jdbc.hsqldb;

import org.apache.camel.forage.jdbc.PooledJdbc;
import org.hsqldb.jdbc.JDBCDriver;

/**
 * HSQLDB implementation extending PooledJdbc.
 * Provides HSQLDB-specific connection provider configuration.
 */
public class HsqldbJdbc extends PooledJdbc {

    @Override
    protected Class getConnectionProviderClass() {
        return JDBCDriver.class;
    }
}
