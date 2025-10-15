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

import java.util.Map;
import org.apache.camel.forage.integration.tests.ForageIntegrationTest;
import org.apache.camel.forage.integration.tests.IntegrationTestSetupExtension;
import org.citrusframework.GherkinTestActionRunner;
import org.citrusframework.TestActionSupport;
import org.citrusframework.annotations.CitrusResource;
import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.junit.jupiter.CitrusSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Disabled // POC
@CitrusSupport
@Testcontainers
@ExtendWith(IntegrationTestSetupExtension.class)
public class MultiIT implements TestActionSupport, ForageIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
                    DockerImageName.parse("mirror.gcr.io/postgres:15.0").asCompatibleSubstituteFor("postgres"))
            .withExposedPorts(5432)
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("postgresql")
            .withInitScript("singleITInitScript.sql");

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(
                    DockerImageName.parse("mirror.gcr.io/mysql:8.4").asCompatibleSubstituteFor("mysql"))
            .withExposedPorts(3306)
            .withInitScript("multiITmysqlInitScript.sql");

    @Override
    public void runBeforeAll(
            org.citrusframework.TestCaseRunner runner, org.citrusframework.actions.camel.CamelActionBuilder camel) {
        // running jbang forage run with required resources and required runtime
        runner.when(camel.jbang()
                .custom("forage", "run")
                .processName("route")
                .addResource("route.camel.yaml")
                .addResource("forage-datasource-factory.properties")
                .withArg(System.getProperty(IntegrationTestSetupExtension.RUNTIME_PROPERTY))
                .withEnvs(Map.of("DS1_JDBC_URL", postgres.getJdbcUrl(), "DS2_JDBC_URL", mysql.getJdbcUrl())));
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
