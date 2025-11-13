package org.apache.camel.forage.jdbc;

import java.util.Collections;
import org.apache.camel.forage.integration.tests.IntegrationTestSetupExtension;
import org.citrusframework.GherkinTestActionRunner;
import org.citrusframework.TestActionSupport;
import org.citrusframework.annotations.CitrusResource;
import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.junit.jupiter.CitrusSupport;
import org.citrusframework.spi.Resources;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@CitrusSupport
@Testcontainers
@ExtendWith(IntegrationTestSetupExtension.class)
public class AggregationTest implements TestActionSupport {

    static final String IMAGE_NAME = ConfigProvider.getConfig().getValue("postgres.container.image", String.class);

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
                    DockerImageName.parse(IMAGE_NAME).asCompatibleSubstituteFor("postgres"))
            .withExposedPorts(5432)
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("postgresql")
            .withInitScript("aggregationITInitScript.sql");

    @Test
    @CitrusTest()
    public void aggregationTest(@CitrusResource GherkinTestActionRunner runner) {
        // running jbang forage run with required resources and required runtime
        runner.given(camel().jbang()
                .custom("forage", "run")
                .processName("route")
                .addResource(
                        Resources.fromClasspath(getClass().getSimpleName() + "/event-batching.camel.yaml", getClass()))
                .addResource(Resources.fromClasspath(
                        getClass().getSimpleName() + "/forage-datasource-factory.properties", getClass()))
                .addResource(
                        Resources.fromClasspath(getClass().getSimpleName() + "/MyAggregationStrategy.java", getClass()))
                .withArg(System.getProperty(IntegrationTestSetupExtension.RUNTIME_PROPERTY))
                .withEnvs(Collections.singletonMap("JDBC_URL", postgres.getJdbcUrl()))
                .dumpIntegrationOutput(true));

        // send events to be aggregated together
        runner.when(camel().jbang()
                        .cmd()
                        .send()
                        .endpoint("direct:events")
                        .integration("event-batching")
                        .body("Hello 1!")
                        .header("eventId", "1"))
                .and(camel().jbang()
                        .cmd()
                        .send()
                        .endpoint("direct:events")
                        .integration("event-batching")
                        .body("Hello 2!")
                        .header("eventId", "1"))
                .and(camel().jbang()
                        .cmd()
                        .send()
                        .endpoint("direct:events")
                        .integration("event-batching")
                        .body("Hello 3!")
                        .header("eventId", "1"));

        // validation of logged message
        runner.then(camel().jbang().verify().integration("route").waitForLogMessage("Batch complete with 3 event"));
    }
}
