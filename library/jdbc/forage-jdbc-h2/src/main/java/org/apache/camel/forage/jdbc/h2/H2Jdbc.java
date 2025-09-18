package org.apache.camel.forage.jdbc.h2;

import org.apache.camel.forage.jdbc.PooledDataSource;
import org.h2.Driver;

/**
 * H2 Database implementation extending PooledJdbc.
 * Provides H2-specific connection provider configuration.
 */
public class H2Jdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return Driver.class;
    }
}
