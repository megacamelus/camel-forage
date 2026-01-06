package io.kaoto.forage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import io.kaoto.forage.core.jdbc.DataSourceProvider;
import io.kaoto.forage.jdbc.postgresql.PostgresqlJdbc;
import org.assertj.core.api.Assertions;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
public class PostgresqlDataSourceTest extends DataSourceTest {

    @Container
    static GenericContainer<?> postgresql = new GenericContainer<>(DockerImageName.parse("postgres:14-alpine3.21"))
            .withExposedPorts(5432)
            .withEnv("POSTGRES_PASSWORD", "postgresql")
            .withEnv("POSTGRES_USER", "postgresql")
            .withEnv("POSTGRES_DB", "postgresql");

    @Override
    protected DataSourceProvider createDataSourceProvider() {
        return new PostgresqlJdbc();
    }

    @Override
    protected void setUpDataSource(String dataSourceName) {
        System.setProperty("forage." + dataSourceName + ".jdbc.db.kind", "postgresql");
        System.setProperty(
                "forage." + dataSourceName + ".jdbc.url",
                "jdbc:postgresql://localhost:" + postgresql.getMappedPort(5432) + "/postgresql");
        System.setProperty("forage." + dataSourceName + ".jdbc.username", "postgresql");
        System.setProperty("forage." + dataSourceName + ".jdbc.password", "postgresql");
    }

    @Override
    protected void validateTestQueryResult(ResultSet rs) throws SQLException {
        rs.next();

        Assertions.assertThat(rs.getString(1)).contains("PostgreSQL");
        Assertions.assertThat(rs.getString(2)).contains("postgresql");
    }
}
