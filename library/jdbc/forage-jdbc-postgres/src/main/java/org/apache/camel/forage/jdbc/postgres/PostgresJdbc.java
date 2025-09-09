package org.apache.camel.forage.jdbc.postgres;

import org.apache.camel.forage.jdbc.PooledJdbc;
import org.postgresql.Driver;

/**
 * PostgreSQL implementation extending PooledJdbc.
 * Provides PostgreSQL-specific connection provider configuration.
 */
public class PostgresJdbc extends PooledJdbc {

    @Override
    protected Class getConnectionProviderClass() {
        return Driver.class;
    }
}
