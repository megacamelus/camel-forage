package org.apache.camel.forage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.jdbc.h2.H2Jdbc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class H2DataSourceTest extends DataSourceTest {

    @Override
    protected DataSourceProvider createDataSourceProvider() {
        return new H2Jdbc();
    }

    @Override
    protected void setUpDataSource(String dataSourceName) {
        System.setProperty(dataSourceName + ".jdbc.db.kind", "h2");
        System.setProperty(dataSourceName + ".jdbc.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        System.setProperty(dataSourceName + ".jdbc.username", "sa");
        System.setProperty(dataSourceName + ".jdbc.password", "");
    }

    @Override
    protected void validateTestQueryResult(ResultSet rs) throws SQLException {
        rs.next();

        Assertions.assertThat(rs.getString(1)).isNotEmpty();
        Assertions.assertThat(rs.getString(2)).isEqualTo("PUBLIC");
        Assertions.assertThat(rs.getString(3)).isEqualTo("SA");
    }
}
