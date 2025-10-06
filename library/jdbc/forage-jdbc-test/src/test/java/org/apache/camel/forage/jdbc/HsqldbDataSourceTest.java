package org.apache.camel.forage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.jdbc.hsqldb.HsqldbJdbc;
import org.assertj.core.api.Assertions;

public class HsqldbDataSourceTest extends DataSourceTest {

    @Override
    protected DataSourceProvider createDataSourceProvider() {
        return new HsqldbJdbc();
    }

    @Override
    protected void setUpDataSource(String dataSourceName) {
        System.setProperty(dataSourceName + ".jdbc.db.kind", "hsqldb");
        System.setProperty(dataSourceName + ".jdbc.url", "jdbc:hsqldb:mem:testdb");
        System.setProperty(dataSourceName + ".jdbc.username", "SA");
        System.setProperty(dataSourceName + ".jdbc.password", "");
    }

    @Override
    protected void validateTestQueryResult(ResultSet rs) throws SQLException {
        rs.next();

        Assertions.assertThat(rs.getString(1)).contains("HSQLDB");
    }
}
