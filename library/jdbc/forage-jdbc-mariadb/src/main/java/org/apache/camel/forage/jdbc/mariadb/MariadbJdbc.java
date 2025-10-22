package org.apache.camel.forage.jdbc.mariadb;

import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.jdbc.common.PooledDataSource;
import org.mariadb.jdbc.Driver;
import org.mariadb.jdbc.MariaDbDataSource;

/**
 * MariaDB implementation extending PooledJdbc.
 * Provides MariaDB-specific connection provider configuration.
 */
@ForageBean(
        value = "mariadb",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "MariaDB database")
public class MariadbJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        if (getConfig().transactionEnabled()) {
            return MariaDbDataSource.class;
        } else {
            return Driver.class;
        }
    }

    @Override
    public String getTestQuery() {
        return "SELECT VERSION(), DATABASE(), USER()";
    }
}
