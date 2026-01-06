package io.kaoto.forage.jdbc;

import io.kaoto.forage.core.jdbc.DataSourceProvider;
import io.kaoto.forage.jdbc.hsqldb.HsqldbJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.assertj.core.api.Assertions;

public class HsqldbDataSourceTest extends DataSourceTest {

    @Override
    protected DataSourceProvider createDataSourceProvider() {
        return new HsqldbJdbc();
    }

    @Override
    protected void setUpDataSource(String dataSourceName) {
        System.setProperty("forage." + dataSourceName + ".jdbc.db.kind", "hsqldb");
        System.setProperty("forage." + dataSourceName + ".jdbc.url", "jdbc:hsqldb:mem:testdb");
        System.setProperty("forage." + dataSourceName + ".jdbc.username", "SA");
        System.setProperty("forage." + dataSourceName + ".jdbc.password", "");
    }

    @Override
    protected void validateTestQueryResult(ResultSet rs) throws SQLException {
        rs.next();

        Assertions.assertThat(rs.getString(1)).contains("HSQLDB");
    }
}
