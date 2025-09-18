package org.apache.camel.forage.jdbc.mariadb;

import org.apache.camel.forage.jdbc.PooledDataSource;
import org.mariadb.jdbc.Driver;

/**
 * MariaDB implementation extending PooledJdbc.
 * Provides MariaDB-specific connection provider configuration.
 */
public class MariadbJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return Driver.class;
    }
}
