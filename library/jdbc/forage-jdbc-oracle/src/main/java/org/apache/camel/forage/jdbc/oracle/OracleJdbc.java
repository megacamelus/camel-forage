package org.apache.camel.forage.jdbc.oracle;

import oracle.jdbc.OracleDriver;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.apache.camel.forage.jdbc.common.PooledDataSource;

/**
 * Oracle Database implementation extending PooledJdbc.
 * Provides Oracle-specific connection provider configuration.
 */
@ForageBean(
        value = "oracle",
        components = {"camel-sql", "camel-jdbc", "camel-spring-jdbc"},
        description = "Oracle database DataSource Provider")
public class OracleJdbc extends PooledDataSource {

    @Override
    protected Class getConnectionProviderClass() {
        return OracleDriver.class;
    }
}
