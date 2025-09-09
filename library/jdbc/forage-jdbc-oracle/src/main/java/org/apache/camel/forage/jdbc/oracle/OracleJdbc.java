package org.apache.camel.forage.jdbc.oracle;

import oracle.jdbc.OracleDriver;
import org.apache.camel.forage.jdbc.PooledJdbc;

/**
 * Oracle Database implementation extending PooledJdbc.
 * Provides Oracle-specific connection provider configuration.
 */
public class OracleJdbc extends PooledJdbc {

    @Override
    protected Class getConnectionProviderClass() {
        return OracleDriver.class;
    }
}
