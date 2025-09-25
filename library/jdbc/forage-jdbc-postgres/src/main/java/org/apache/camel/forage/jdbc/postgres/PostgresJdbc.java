package org.apache.camel.forage.jdbc.postgres;

import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.jdbc.common.PooledDataSource;
import org.postgresql.Driver;

/**
 * PostgreSQL implementation extending PooledJdbc.
 * Provides PostgreSQL-specific connection provider configuration.
 */
@ForageBean(
        value = "postgres",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "Postgres database DataSource Provider")
public class PostgresJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return Driver.class;
    }

    @Override
    public String getTestQuery() {
        return "SELECT version(), current_database(), current_user";
    }
}
