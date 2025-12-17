package org.apache.camel.forage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.jdbc.mysql.MysqlJdbc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@DisabledIfSystemProperty(named = "ci.env.name", matches = ".*", disabledReason = "Slow or flaky on GitHub action")
public class MySQLDataSourceTest extends DataSourceTest {

    private static final String MYSQL_DATABASE = "myDatabase";
    private static final String VERSION = "8.0";

    @Container
    static GenericContainer<?> mysql = new GenericContainer<>(DockerImageName.parse("mysql:" + VERSION + "-debian"))
            .withExposedPorts(3306)
            .withEnv("MYSQL_ROOT_PASSWORD", "pwd")
            .withEnv("MYSQL_DATABASE", MYSQL_DATABASE);

    @Override
    protected DataSourceProvider createDataSourceProvider() {
        return new MysqlJdbc();
    }

    @Override
    protected void setUpDataSource(String dataSourceName) {
        System.setProperty("forage." + dataSourceName + ".jdbc.db.kind", "mysql");
        System.setProperty(
                "forage." + dataSourceName + ".jdbc.url",
                "jdbc:mysql://localhost:" + mysql.getMappedPort(3306) + "/" + MYSQL_DATABASE);
        System.setProperty("forage." + dataSourceName + ".jdbc.username", "root");
        System.setProperty("forage." + dataSourceName + ".jdbc.password", "pwd");
    }

    @Override
    protected void validateTestQueryResult(ResultSet rs) throws SQLException {
        rs.next();

        Assertions.assertThat(rs.getString(1)).contains(VERSION);
        Assertions.assertThat(rs.getString(2)).contains(MYSQL_DATABASE);
    }
}
