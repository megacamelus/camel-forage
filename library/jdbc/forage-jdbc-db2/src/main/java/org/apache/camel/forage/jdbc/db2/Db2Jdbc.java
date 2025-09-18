package org.apache.camel.forage.jdbc.db2;

import com.ibm.db2.jcc.DB2Driver;
import org.apache.camel.forage.jdbc.PooledDataSource;

/**
 * IBM DB2 implementation extending PooledJdbc.
 * Provides DB2-specific connection provider configuration.
 */
public class Db2Jdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return DB2Driver.class;
    }
}
