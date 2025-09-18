package org.apache.camel.forage.jdbc.mysql;

import com.mysql.cj.jdbc.Driver;
import org.apache.camel.forage.jdbc.PooledDataSource;

/**
 * MySQL implementation extending PooledJdbc.
 * Provides MySQL-specific connection provider configuration.
 */
public class MysqlJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return Driver.class;
    }
}
