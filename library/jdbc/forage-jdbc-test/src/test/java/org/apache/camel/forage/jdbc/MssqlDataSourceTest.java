package org.apache.camel.forage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.jdbc.mssql.MssqlJdbc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@DisabledIfSystemProperty(named = "ci.env.name", matches = ".*", disabledReason = "Slow or flaky on GitHub action")
public class MssqlDataSourceTest extends DataSourceTest {

    private static final String MSSQL_DATABASE = "master";

    @Container
    static GenericContainer<?> mssql = new GenericContainer<>(
                    DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest"))
            .withExposedPorts(1433)
            .withEnv("ACCEPT_EULA", "Y")
            .withEnv("MSSQL_SA_PASSWORD", "YourStrong!Passw0rd")
            .withEnv("MSSQL_PID", "Developer")
            .waitingFor(Wait.forListeningPort())
            .waitingFor(Wait.forLogMessage(".*The tempdb database has 8.*", 1));

    @Override
    protected DataSourceProvider createDataSourceProvider() {
        return new MssqlJdbc();
    }

    @Override
    protected void setUpDataSource(String dataSourceName) {
        System.setProperty("forage." + dataSourceName + ".jdbc.db.kind", "mssql");
        System.setProperty(
                "forage." + dataSourceName + ".jdbc.url",
                "jdbc:sqlserver://localhost:" + mssql.getMappedPort(1433) + ";databaseName=" + MSSQL_DATABASE
                        + ";trustServerCertificate=true");
        System.setProperty("forage." + dataSourceName + ".jdbc.username", "sa");
        System.setProperty("forage." + dataSourceName + ".jdbc.password", "YourStrong!Passw0rd");
    }

    @Override
    protected void validateTestQueryResult(ResultSet rs) throws SQLException {
        rs.next();

        Assertions.assertThat(rs.getString(1)).contains("Microsoft SQL Server");
        Assertions.assertThat(rs.getString(2)).contains(MSSQL_DATABASE);
        Assertions.assertThat(rs.getString(3)).contains("sa");
    }

    @Test
    public void testDefaultTransactedDataSource() throws Exception {
        // XA Transactions do not work with mssql docker image
    }
}
