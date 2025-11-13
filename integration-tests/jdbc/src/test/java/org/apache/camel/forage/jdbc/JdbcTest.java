package org.apache.camel.forage.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.function.Consumer;
import org.apache.camel.forage.integration.tests.ForageIntegrationTest;
import org.apache.camel.forage.integration.tests.IntegrationTestSetupExtension;
import org.citrusframework.GherkinTestActionRunner;
import org.citrusframework.TestActionSupport;
import org.citrusframework.TestCaseRunner;
import org.citrusframework.annotations.CitrusResource;
import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.junit.jupiter.CitrusSupport;
import org.citrusframework.spi.Resource;
import org.citrusframework.spi.Resources;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@CitrusSupport
@Testcontainers
@ExtendWith(IntegrationTestSetupExtension.class)
public class JdbcTest implements TestActionSupport, ForageIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcTest.class);

    static final Path INPUT_FOLDER = Paths.get("target", "data", "in");
    static final String POSTGRES_IMAGE_NAME =
            ConfigProvider.getConfig().getValue("postgres.container.image", String.class);
    public static final String INNTEGRATION_NAME = "jdbc-routes";

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
                    DockerImageName.parse(POSTGRES_IMAGE_NAME).asCompatibleSubstituteFor("postgres"))
            .withExposedPorts(5432)
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("postgresql")
            .withInitScripts("singleTest-postgresql-initScript.sql", "aggregationTest-postgresql-InitScript.sql");

    @BeforeAll
    static void populateTemplate() throws IOException {
        Resource resource = Resources.fromClasspath(
                JdbcTest.class.getSimpleName() + "/jdbc-routes-template.camel.yaml", JdbcTest.class);
        try (InputStream is = resource.getInputStream()) {
            String content = new String(is.readAllBytes());
            // Use hardcoded path
            Files.createDirectories(INPUT_FOLDER);
            content = content.replace("${path}", INPUT_FOLDER.toAbsolutePath().toString());

            Files.writeString(
                    resource.getFile().toPath().getParent().resolve(INNTEGRATION_NAME.toLowerCase() + ".camel.yaml"),
                    content);
        }
    }

    @Override
    public void runBeforeAll(TestCaseRunner runner, Consumer<AutoCloseable> afterAll) {
        // running jbang forage run with required resources and required runtime
        runner.when(camel().jbang()
                .custom("forage", "run")
                .processName(INNTEGRATION_NAME)
                .addResource(
                        Resources.fromClasspath(getClass().getSimpleName() + "/jdbc-routes.camel.yaml", getClass()))
                .addResource(
                        Resources.fromClasspath(getClass().getSimpleName() + "/MyAggregationStrategy.java", getClass()))
                .addResource(Resources.fromClasspath(
                        getClass().getSimpleName() + "/forage-datasource-factory.properties", getClass()))
                // required if more test are using the same route
                .autoRemove(false)
                .withEnvs(Collections.singletonMap("JDBC_URL", postgres.getJdbcUrl())));

        afterAll.accept(() -> runner.then(camel().jbang().stop().integration(INNTEGRATION_NAME)));
    }

    @Test
    @CitrusTest()
    public void single(@CitrusResource GherkinTestActionRunner runner) {

        // validation of logged message
        runner.then(camel().jbang()
                .verify()
                .integration(INNTEGRATION_NAME)
                .waitForLogMessage("from jdbc default ds - [{id=1, content=postgres 1}, {id=2, content=postgres 2}]"));
    }

    @Test
    @CitrusTest()
    public void aggregationTest(@CitrusResource GherkinTestActionRunner runner) {

        // send events to be aggregated together
        runner.when(camel().jbang()
                        .cmd()
                        .send()
                        .endpoint("direct:events")
                        .integration(INNTEGRATION_NAME)
                        .body("Hello 1!")
                        .header("eventId", "1"))
                .and(camel().jbang()
                        .cmd()
                        .send()
                        .endpoint("direct:events")
                        .integration(INNTEGRATION_NAME)
                        .body("Hello 2!")
                        .header("eventId", "1"))
                .and(camel().jbang()
                        .cmd()
                        .send()
                        .endpoint("direct:events")
                        .integration(INNTEGRATION_NAME)
                        .body("Hello 3!")
                        .header("eventId", "1"));

        // validation of logged message
        runner.then(camel().jbang()
                .verify()
                .integration(INNTEGRATION_NAME)
                .waitForLogMessage("Batch complete with 3 event"));
    }

    @Test
    @CitrusTest()
    public void idempotentTest(@CitrusResource GherkinTestActionRunner runner)
            throws IOException, InterruptedException {
        Files.createDirectories(INPUT_FOLDER);

        // create a temp file with content `A`
        Path fileA = Files.write(Files.createTempFile("tempFile", ".txt"), "A".getBytes(), StandardOpenOption.WRITE);

        // copy test.txt to input folder
        Files.move(fileA, INPUT_FOLDER.resolve("test.txt"));

        // validation of logged message
        runner.then(camel().jbang()
                .verify()
                .integration(INNTEGRATION_NAME)
                .waitForLogMessage("Processed file: test.txt with content: A"));

        // create a temp file with content `A`
        Path fileB = Files.write(Files.createTempFile("tempFile", ".txt"), "B".getBytes(), StandardOpenOption.WRITE);

        // copy test.txt to input folder
        Files.move(fileB, INPUT_FOLDER.resolve("test.txt"));

        String error = null;
        try {
            // failure is expected
            runner.then(camel().jbang()
                    .verify()
                    .integration(INNTEGRATION_NAME)
                    .maxAttempts(3)
                    .delayBetweenAttempts(5000)
                    .waitForLogMessage("Processed file: test.txt with content: B"));
        } catch (Exception e) {
            error = e.getMessage();
        }
        Assertions.assertTrue(error != null && error.startsWith("Action timeout after"));
    }
}
