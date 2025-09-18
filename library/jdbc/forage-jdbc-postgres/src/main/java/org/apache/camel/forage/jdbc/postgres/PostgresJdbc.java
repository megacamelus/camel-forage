package org.apache.camel.forage.jdbc.postgres;

import org.apache.camel.forage.jdbc.PooledDataSource;
import org.postgresql.Driver;

/**
 * PostgreSQL implementation extending PooledJdbc.
 * Provides PostgreSQL-specific connection provider configuration.
 */
public class PostgresJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return Driver.class;
    }
}
