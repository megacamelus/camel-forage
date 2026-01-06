package io.kaoto.forage.jdbc.db2;

import com.ibm.db2.jcc.DB2Driver;
import com.ibm.db2.jcc.DB2XADataSource;
import io.kaoto.forage.core.annotations.ForageBean;
import io.kaoto.forage.jdbc.common.PooledDataSource;

/**
 * IBM DB2 implementation extending PooledJdbc.
 * Provides DB2-specific connection provider configuration.
 */
@ForageBean(
        value = "db2",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "IBM DB2 database")
public class Db2Jdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        if (getConfig().transactionEnabled()) {
            return DB2XADataSource.class;
        } else {
            return DB2Driver.class;
        }
    }

    @Override
    public String getTestQuery() {
        return "SELECT service_level, fixpack_num, bld_level FROM TABLE (sysproc.env_get_inst_info()) as A";
    }
}
