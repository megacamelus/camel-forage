package org.apache.camel.forage.jdbc.db2;

import com.ibm.db2.jcc.DB2Driver;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.jdbc.common.PooledDataSource;

/**
 * IBM DB2 implementation extending PooledJdbc.
 * Provides DB2-specific connection provider configuration.
 */
@ForageBean(
        value = "db2",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "DB2 database DataSource Provider")
public class Db2Jdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return DB2Driver.class;
    }

    @Override
    public String getTestQuery() {
        return "SELECT service_level, current_schema, current_user FROM sysibm.sysversions";
    }
}
