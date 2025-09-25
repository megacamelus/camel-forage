package org.apache.camel.forage.jdbc.mysql;

import com.mysql.cj.jdbc.Driver;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.jdbc.common.PooledDataSource;

/**
 * MySQL implementation extending PooledJdbc.
 * Provides MySQL-specific connection provider configuration.
 */
@ForageBean(
        value = "mysql",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "MySQL database DataSource Provider")
public class MysqlJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return Driver.class;
    }

    @Override
    public String getTestQuery() {
        return "SELECT VERSION(), DATABASE(), USER()";
    }
}
