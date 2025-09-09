package org.apache.camel.forage.jdbc.mariadb;

import org.apache.camel.forage.jdbc.PooledJdbc;
import org.mariadb.jdbc.Driver;

/**
 * MariaDB implementation extending PooledJdbc.
 * Provides MariaDB-specific connection provider configuration.
 */
public class MariadbJdbc extends PooledJdbc {

    @Override
    protected Class getConnectionProviderClass() {
        return Driver.class;
    }
}
