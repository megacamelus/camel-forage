package org.apache.camel.forage.springboot.jdbc.postgres;

import io.agroal.springframework.boot.AgroalDataSourceAutoConfiguration;
import org.apache.camel.forage.springboot.jdbc.ForageJdbcAutoConfiguration;
import org.postgresql.Driver;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;

@AutoConfigureBefore({AgroalDataSourceAutoConfiguration.class})
public class PostgresSpringBootJdbc extends ForageJdbcAutoConfiguration {

    @Override
    protected Class getConnectionProviderClass() {
        return Driver.class;
    }
}
