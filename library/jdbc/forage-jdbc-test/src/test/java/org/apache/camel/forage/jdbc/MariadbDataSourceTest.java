package org.apache.camel.forage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.jdbc.mariadb.MariadbJdbc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@DisabledIfSystemProperty(named = "ci.env.name", matches = ".*", disabledReason = "Slow or flaky on GitHub action")
public class MariadbDataSourceTest extends DataSourceTest {

    private static final String MARIADB_DATABASE = "myDatabase";
    private static final String VERSION = "11.6";

    @Container
    static GenericContainer<?> mariadb = new GenericContainer<>(DockerImageName.parse("mariadb:" + VERSION))
            .withExposedPorts(3306)
            .withEnv("MYSQL_ROOT_PASSWORD", "pwd")
            .withEnv("MYSQL_DATABASE", MARIADB_DATABASE);

    @Override
    protected DataSourceProvider createDataSourceProvider() {
        return new MariadbJdbc();
    }

    @Override
    protected void setUpDataSource(String dataSourceName) {
        System.setProperty("forage." + dataSourceName + ".jdbc.db.kind", "mariadb");
        System.setProperty(
                "forage." + dataSourceName + ".jdbc.url",
                "jdbc:mariadb://localhost:" + mariadb.getMappedPort(3306) + "/" + MARIADB_DATABASE);
        System.setProperty("forage." + dataSourceName + ".jdbc.username", "root");
        System.setProperty("forage." + dataSourceName + ".jdbc.password", "pwd");
    }

    @Override
    protected void validateTestQueryResult(ResultSet rs) throws SQLException {
        rs.next();

        Assertions.assertThat(rs.getString(1)).contains(VERSION);
        Assertions.assertThat(rs.getString(2)).contains(MARIADB_DATABASE);
    }
}
