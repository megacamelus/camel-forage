package org.apache.camel.forage.jdbc.hsqldb;

import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.jdbc.common.PooledDataSource;
import org.hsqldb.jdbc.JDBCDriver;

/**
 * HSQLDB implementation extending PooledJdbc.
 * Provides HSQLDB-specific connection provider configuration.
 */
@ForageBean(
        value = "hsqldb",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "HSQLDB database DataSource Provider")
public class HsqldbJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return JDBCDriver.class;
    }
}
