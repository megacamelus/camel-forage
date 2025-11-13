/*
 * Copyright the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.camel.forage.jdbc;

import static org.citrusframework.camel.dsl.CamelSupport.camel;

import java.util.Map;
import java.util.function.Consumer;
import org.apache.camel.forage.integration.tests.ForageIntegrationTest;
import org.apache.camel.forage.integration.tests.IntegrationTestSetupExtension;
import org.citrusframework.GherkinTestActionRunner;
import org.citrusframework.TestActionSupport;
import org.citrusframework.TestCaseRunner;
import org.citrusframework.annotations.CitrusResource;
import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.junit.jupiter.CitrusSupport;
import org.citrusframework.spi.Resources;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Test class starts route only once, before all tests are executed.
 */
@CitrusSupport
@Testcontainers
@ExtendWith(IntegrationTestSetupExtension.class)
public class MultiTest implements TestActionSupport, ForageIntegrationTest {

    static final String POSTGRES_IMAGE_NAME =
            ConfigProvider.getConfig().getValue("postgres.container.image", String.class);
    static final String MYSQL_IMAGE_NAME = ConfigProvider.getConfig().getValue("mysql.container.image", String.class);

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
                    DockerImageName.parse(POSTGRES_IMAGE_NAME).asCompatibleSubstituteFor("postgres"))
            .withExposedPorts(5432)
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("postgresql")
            .withInitScript("singleITInitScript.sql");

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(
                    DockerImageName.parse(MYSQL_IMAGE_NAME).asCompatibleSubstituteFor("mysql"))
            .withExposedPorts(3306)
            .withInitScript("multiITmysqlInitScript.sql");

    @Override
    public void runBeforeAll(TestCaseRunner runner, Consumer<AutoCloseable> afterAll) {
        runner.when((camel())
                .jbang()
                .custom("forage", "run")
                .processName("route")
                .addResource(Resources.fromClasspath(getClass().getSimpleName() + "/route.camel.yaml", getClass()))
                .addResource(Resources.fromClasspath(
                        getClass().getSimpleName() + "/forage-datasource-factory.properties", getClass()))
                .withArg(System.getProperty(IntegrationTestSetupExtension.RUNTIME_PROPERTY))
                .dumpIntegrationOutput(true)
                .autoRemove(false)
                .withEnvs(Map.of("DS1_JDBC_URL", postgres.getJdbcUrl(), "DS2_JDBC_URL", mysql.getJdbcUrl())));

        afterAll.accept(() -> runner.then(camel().jbang().stop().integration("route")));
    }

    @Test
    @CitrusTest()
    public void postgresql(@CitrusResource GherkinTestActionRunner runner) {
        // validation of logged message
        runner.then(camel().jbang()
                .verify()
                .integration("route")
                .waitForLogMessage("from jdbc postgresql - [{id=1, content=postgres 1}, {id=2, content=postgres 2}]"));
    }

    @Test
    @CitrusTest()
    public void mysql(@CitrusResource GherkinTestActionRunner runner) {
        // validation of logged message
        runner.then(camel().jbang()
                .verify()
                .integration("route")
                .waitForLogMessage("from sql mysql - [{id=1, content=mysql 1}, {id=2, content=mysql 2}]"));
    }
}
