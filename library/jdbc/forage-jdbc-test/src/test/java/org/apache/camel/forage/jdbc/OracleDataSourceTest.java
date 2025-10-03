package org.apache.camel.forage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.jdbc.oracle.OracleJdbc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
public class OracleDataSourceTest extends DataSourceTest {

    @Container
    static GenericContainer<?> oracle = new GenericContainer<>(DockerImageName.parse("gvenzl/oracle-free:latest"))
            .withExposedPorts(1521)
            .withEnv("ORACLE_PASSWORD", "oracle")
            .withEnv("APP_USER", "testuser")
            .withEnv("APP_USER_PASSWORD", "testpass")
            .waitingFor(Wait.forLogMessage(".*Pluggable database FREEPDB1 opened read write.*", 1));

    @Override
    protected DataSourceProvider createDataSourceProvider() {
        return new OracleJdbc();
    }

    @Override
    protected void setUpDataSource(String dataSourceName) {
        System.setProperty(dataSourceName + ".jdbc.db.kind", "oracle");
        System.setProperty(
                dataSourceName + ".jdbc.url",
                "jdbc:oracle:thin:@localhost:" + oracle.getMappedPort(1521) + "/FREEPDB1");
        System.setProperty(dataSourceName + ".jdbc.username", "testuser");
        System.setProperty(dataSourceName + ".jdbc.password", "testpass");
    }

    @Override
    protected void validateTestQueryResult(ResultSet rs) throws SQLException {
        rs.next();

        Assertions.assertThat(rs.getString(1)).contains("Oracle Database");
    }

    @Test
    public void testDefaultTransactedDataSource() throws Exception {
        // XA Transactions do not work with oracle-free docker image
    }
}
