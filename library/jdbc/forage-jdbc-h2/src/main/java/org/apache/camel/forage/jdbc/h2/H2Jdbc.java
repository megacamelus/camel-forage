package org.apache.camel.forage.jdbc.h2;

import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.jdbc.common.PooledDataSource;
import org.h2.Driver;
import org.h2.jdbcx.JdbcDataSource;

/**
 * H2 Database implementation extending PooledJdbc.
 * Provides H2-specific connection provider configuration.
 */
@ForageBean(
        value = "h2",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "H2 database")
public class H2Jdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        if (getConfig().transactionEnabled()) {
            return JdbcDataSource.class;
        } else {
            return Driver.class;
        }
    }

    @Override
    public String getTestQuery() {
        return "SELECT H2VERSION(), SCHEMA(), USER()";
    }
}
